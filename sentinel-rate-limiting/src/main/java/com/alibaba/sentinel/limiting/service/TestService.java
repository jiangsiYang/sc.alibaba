package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {
    @SentinelResource(value = "doSomeThing", blockHandler = "exceptionHandler")
    public void doSomeThing(String str) {
        System.out.println(str);
    }

    public void exceptionHandler(String str, BlockException ex) {
        System.out.println("blockHandler：" + str + "ex=" + ex);
    }


    /**
     * 使用非注解@SentinelResource 的例子
     *
     * @param argsp
     */
    public static void main(String argsp[]) {
        testOriginCode();
    }

    /**
     * 把需要控制流量的代码用 Sentinel API SphU.entry("HelloWorld") 和 entry.exit() 包围起来即可。在下面的例子中，
     * 我们将 System.out.println("hello wolrd"); 作为资源，用 API 包围起来。
     */
    public static void testOriginCode() {
        initFlowRules();
        while (true) {
            Entry entry = null;
            try {
                entry = SphU.entry("HelloWorld");
                //业务逻辑开始
                System.out.println("hello world");
                //业务逻辑结束
            } catch (BlockException e) {
                //流控逻辑处理
                System.out.println("block!");
                //流控逻辑结束
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }


        }
    }

    /**
     * 通过规则来指定允许该资源通过的请求次数，例如下面的代码定义了资源 HelloWorld 每秒最多只能通过 20 个请求。
     */
    private static void initFlowRules() {
        List<FlowRule> ruleList = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("HelloWorld");
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // set limit QPS to 20
        flowRule.setCount(20);
        ruleList.add(flowRule);
        FlowRuleManager.loadRules(ruleList);
    }


    /**
     * 熔断降级
     *
     * @param key
     */
    public static void testDegrade(String key) {
        Entry entry = null;
        try {
            entry = SphU.entry(key, EntryType.IN);
            System.out.println("业务开始");

        } catch (Throwable t) {
            //异常降级仅针对业务异常，对 Sentinel 限流降级本身的异常（BlockException）不生效。为了统计异常比例或异常数，需要通过 Tracer.trace(ex) 记录业务异常
            if (!BlockException.isBlockException(t)) {
                Tracer.trace(t);
            }
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }
}
