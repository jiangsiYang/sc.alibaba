package com.alibaba.sentinel.limiting.demo.flow.qps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

/**
 * 2.2 QPS流量控制
 * Warm Up（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）方式，即预热/冷启动方式。当系统长期处于低水位的情况下，当流量突然增加时，直接把系统拉升到高水位
 * 可能瞬间把系统压垮。通过"冷启动"，让通过的流量缓慢增加，在一定时间内逐渐增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。详细文档可以参考
 * 流量控制 - Warm Up 文档，具体的例子可以参见 WarmUpFlowDemo。
 *
 * 其实，它比直接拒绝模式只是单纯的不是一下子能到最大限制qps，而是逐步放开到max qps，对多出来的流量也是直接执行拒绝处理的。
 * 注意：启用了这种模式，就算你设置的max count=5000,一开始的qps=10，然后突然涌入了一波流量大概qps=4000，那么就算这波流量在你的系统的承受范围内（4000<5000）
 * 也会开始“warn up”，会拒绝一些流量，直到放开的流量增加到4000，可以换成CONTROL_BEHAVIOR_DEFAULT模式对比下
 *
 * 应该这里的用到了“令牌桶算法”来实现这个加热的操作，即在warmUpPeriodSec时间周期内，把桶里的每per时间产生的令牌增加到最大数量，参考
 * https://github.com/alibaba/Sentinel/wiki/%E9%99%90%E6%B5%81---%E5%86%B7%E5%90%AF%E5%8A%A8
 */

public class WarmUpFlowDemo {

    private static final String KEY = "abc";

    private static AtomicInteger pass = new AtomicInteger();
    private static AtomicInteger block = new AtomicInteger();
    private static AtomicInteger total = new AtomicInteger();

    private static volatile boolean stop = false;

    private static final int threadCount = 100;
    private static int seconds = 60 + 40;

    public static void main(String[] args) throws Exception {
        initFlowRule();
        // trigger Sentinel internal init
        Entry entry = null;
        try {
            entry = SphU.entry(KEY);
        } catch (Exception e) {
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }

        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();

        //first make the system run on a very low condition
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(new WarmUpTask());
            t.setName("sentinel-warmup-task");
            t.start();
        }
        //这是为了模拟一开始的低水位的qps，所以睡了20S，20S后开始高并发
        Thread.sleep(20000);
        System.out.println("Large amount of traffic is coming");
        /*
         * Start more thread to simulate more qps. Since we use {@link RuleConstant.CONTROL_BEHAVIOR_WARM_UP} as
         * {@link FlowRule#controlBehavior}, real passed qps will increase to {@link FlowRule#count} in
         * {@link FlowRule#warmUpPeriodSec} seconds.
         */
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new RunTask());
            t.setName("sentinel-run-task");
            t.start();
        }
    }

    private static void initFlowRule() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(KEY);
//        rule1.setCount(20);
        rule1.setCount(5000);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
//        rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        //warmUpPeriodSec 代表期待系统进入稳定状态的时间（即预热时长）。
        rule1.setWarmUpPeriodSec(10);

        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
    }

    static class WarmUpTask implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                Entry entry = null;
                try {
                    entry = SphU.entry(KEY);
                    // token acquired, means pass
                    pass.addAndGet(1);
                } catch (BlockException e1) {
                    block.incrementAndGet();
                } catch (Exception e2) {
                    // biz exception
                } finally {
                    total.incrementAndGet();
                    if (entry != null) {
                        entry.exit();
                    }
                }
                Random random2 = new Random();
                try {
                    TimeUnit.MILLISECONDS.sleep(random2.nextInt(2000));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    static class RunTask implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                Entry entry = null;
                try {
                    entry = SphU.entry(KEY);
                    pass.addAndGet(1);
                } catch (BlockException e1) {
                    block.incrementAndGet();
                } catch (Exception e2) {
                    // biz exception
                } finally {
                    total.incrementAndGet();
                    if (entry != null) {
                        entry.exit();
                    }
                }
                Random random2 = new Random();
                try {
                    TimeUnit.MILLISECONDS.sleep(random2.nextInt(50));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    static class TimerTask implements Runnable {

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            System.out.println("begin to statistic!!!");
            long oldTotal = 0;
            long oldPass = 0;
            long oldBlock = 0;
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }

                long globalTotal = total.get();
                long oneSecondTotal = globalTotal - oldTotal;
                oldTotal = globalTotal;

                long globalPass = pass.get();
                long oneSecondPass = globalPass - oldPass;
                oldPass = globalPass;

                long globalBlock = block.get();
                long oneSecondBlock = globalBlock - oldBlock;
                oldBlock = globalBlock;

                System.out.println(TimeUtil.currentTimeMillis() + ", total:" + oneSecondTotal
                        + ", pass:" + oneSecondPass
                        + ", block:" + oneSecondBlock);
                if (seconds-- <= 0) {
                    stop = true;
                }
            }

            long cost = System.currentTimeMillis() - start;
            System.out.println("time cost: " + cost + " ms");
            System.out.println("total:" + total.get() + ", pass:" + pass.get()
                    + ", block:" + block.get());
            System.exit(0);
        }
    }
}
