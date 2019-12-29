package com.alibaba.sentinel.limiting.controller;

import com.alibaba.sentinel.limiting.SimulateProperties;
import com.alibaba.sentinel.limiting.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {

    @Autowired
    private SimulateProperties simulateProperties;

    @Autowired
    private TestService testService;

    @GetMapping("/simulate")
    public String simulate() {
        return simulateProperties.getShuaiqi();
    }

    @GetMapping("/hello")
    public String hello() {
        testService.doSomeThing("hello " + new Date());
        return "didispace.com";
    }
}
