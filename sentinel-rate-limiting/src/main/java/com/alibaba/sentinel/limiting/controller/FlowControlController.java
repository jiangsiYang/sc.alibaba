package com.alibaba.sentinel.limiting.controller;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.sentinel.limiting.service.FlowControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 限流配置test
 */
@RestController
@RequestMapping(value = "/flow_control")
public class FlowControlController {

    @Autowired
    private FlowControlService flowControlService;

    @GetMapping("/hello")
    public String hello() {
        flowControlService.flowControl("hello " + new Date());
        return "didispace.com";
    }

    @GetMapping("/not_hello")
    public String notHello() {
        flowControlService.notAddFlowControl("hello " + new Date());
        return "didispace.com";
    }
}
