package com.alibaba.sentinel.limiting;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
//        List<DegradeRule> rules = new ArrayList<DegradeRule>();
//        DegradeRule rule = new DegradeRule();
//        rule.setResource("rtB");
//        // set threshold rt, 10 ms
//        rule.setCount(100);
//        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
//        rule.setTimeWindow(5);
//        rules.add(rule);
//        DegradeRuleManager.loadRules(rules);

        //按比例
//        List<DegradeRule> rules = new ArrayList<DegradeRule>();
//        DegradeRule rule = new DegradeRule();
//        rule.setResource("exRadioB");
//        // set limit exception ratio to 0.1
//        //如果调到0.6即不会降级
//        rule.setCount(0.1);
//        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
//        rule.setTimeWindow(10);
////        rule.setMinRequestAmount(20);  //demo有，但是报错，可能是新版本干掉了
//        rules.add(rule);
//        DegradeRuleManager.loadRules(rules);

        SpringApplication.run(TestApplication.class, args);
    }


}