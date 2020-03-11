package com.alibaba.sentinel.limiting.controller;

import com.alibaba.sentinel.limiting.service.CircuitBreakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 熔断配置test
 */
@RestController
@RequestMapping(value = "/circuit_breaking")
public class CircuitBreakingController {

    @Autowired
    private CircuitBreakingService circuitBreakingService;

    @GetMapping("/rt")
    public String hello() {
        circuitBreakingService.rtA("hello " + new Date());
        return "didispace.com";
    }

}
