package com.alibaba.sentinel.limiting.demo.flow;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2.1 并发线程数流量控制
 * 并发线程数限流用于保护业务线程数不被耗尽。例如，当应用所依赖的下游应用由于某种原因导致服务不稳定、响应延迟增加，对于调用者来说，意味着吞吐量下降和更多的线程数占用，极端情况下甚至导致线程池耗尽。为应对太多线程占用的情况，业内有使用隔离的方案，比如通过不同业务逻辑使用不同线程池来隔离业务自身之间的资源争抢（线程池隔离）。这种隔离方案虽然隔离性比较好，但是代价就是线程数目太多，线程上下文切换的 overhead 比较大，特别是对低延时的调用有比较大的影响。Sentinel 并发线程数限流不负责创建和管理线程池，而是简单统计当前请求上下文的线程数目，如果超出阈值，新的请求会被立即拒绝，效果类似于信号量隔离。
 * <p>
 * 例子参见：ThreadDemo
 */

public class FlowThreadDemo {
    private static AtomicInteger pass = new AtomicInteger();
    private static AtomicInteger block = new AtomicInteger();
    private static AtomicInteger total = new AtomicInteger();
    private static AtomicInteger activeThread = new AtomicInteger();

    private static volatile boolean stop = false;
    private static final int threadCount = 100;

    private static int seconds = 60 + 40;
    private static volatile int methodBRunningTime = 2000;

    public static void main(String[] args) throws Exception {
        System.out.println(
                "MethodA will call methodB. After running for a while, methodB becomes fast, "
                        + "which make methodA also become fast ");
        tick();
        initFlowRule();

        for (int i = 0; i < threadCount; i++) {
            Thread entryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Entry methodA = null;
                        try {
                            TimeUnit.MILLISECONDS.sleep(5);
                            //定义资源点methodA
                            methodA = SphU.entry("methodA");
                            activeThread.incrementAndGet();
                            //定义资源点methodB
                            Entry methodB = SphU.entry("methodB");
                            TimeUnit.MILLISECONDS.sleep(methodBRunningTime);
                            methodB.exit();
                            pass.addAndGet(1);
                        } catch (BlockException e1) {
                            block.incrementAndGet();
                        } catch (Exception e2) {
                            // biz exception
                        } finally {
                            total.incrementAndGet();
                            if (methodA != null) {
                                methodA.exit();
                                activeThread.decrementAndGet();
                            }
                        }
                    }
                }
            });
            entryThread.setName("working thread");
            entryThread.start();
        }
    }

    /**
     * 定义一个流控规则,针对资源名称为methodA,并发线程最大限制是20
     */
    private static void initFlowRule() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("methodA");
        // set limit concurrent thread for 'methodA' to 20
        rule1.setCount(20);
        rule1.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        rule1.setLimitApp("default");

        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
    }

    private static void tick() {
        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();
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

                System.out.println(seconds + " total qps is: " + oneSecondTotal);
                System.out.println(TimeUtil.currentTimeMillis() + ", total:" + oneSecondTotal
                        + ", pass:" + oneSecondPass
                        + ", block:" + oneSecondBlock
                        + " activeThread:" + activeThread.get());
                if (seconds-- <= 0) {
                    stop = true;
                }
                if (seconds == 40) {
                    System.out.println("method B is running much faster; more requests are allowed to pass");
                    methodBRunningTime = 20;
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
