package com.alibaba.sentinel.limiting.controller;

import com.alibaba.sentinel.limiting.pojo.User;
import com.alibaba.sentinel.limiting.service.ExceptionCountDegradeService;
import com.alibaba.sentinel.limiting.service.ExceptionRadioDegradeService;
import com.alibaba.sentinel.limiting.service.RtCircuitBreakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 熔断配置test
 */
@RestController
@RequestMapping(value = "/circuit_breaking")
public class CircuitBreakingController {

    @Autowired
    private RtCircuitBreakingService rtCircuitBreakingService;
    @Autowired
    private ExceptionRadioDegradeService exceptionRadioDegradeService;
    @Autowired
    private ExceptionCountDegradeService exceptionCountDegradeService;

    @GetMapping("/rt")
    public String hello() {
        User user = new User();
        user.setName("天天");
        rtCircuitBreakingService.rtA(user);
        return "didispace.com";
    }

    @GetMapping("/ex_ratio")
    public String exceptionRatio() {
        User user = new User();
        user.setName("天天");
        exceptionRadioDegradeService.rtA(user);
        return "didispace.com";
    }

    @GetMapping("/ex_count")
    public String exceptionCount() {
        User user = new User();
        user.setName("天天");
        exceptionCountDegradeService.rtA(user);
        return "didispace.com";
    }

}
