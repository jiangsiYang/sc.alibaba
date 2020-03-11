package com.alibaba.sentinel.limiting.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.sentinel.limiting.service.FlowControlService;
import com.alibaba.sentinel.limiting.service.ParamFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 热点参数限流
 * https://github.com/alibaba/Sentinel/wiki/%E7%83%AD%E7%82%B9%E5%8F%82%E6%95%B0%E9%99%90%E6%B5%81
 */

@RestController
@RequestMapping(value = "/param")
public class ParamFlowController {

    @Autowired
    private ParamFlowService paramFlowService;

    /**
     * 热点参数进行限流
     *
     * @return
     */
    @GetMapping("/hello")
    public String hello() {
        paramFlowService.paramFlowA(12345, "hello " + new Date());
        return "didispace.com";
    }

    /**
     * 热点参数进行限流
     * 在控制层配置失效
     *
     * @return
     */
    @SentinelResource(value = "uidParamFlowB", blockHandler = "uidParamFlowExceptionHandlerB")
    @GetMapping("/hi")
    public String hi() {
        paramFlowService.paramFlowC(12345, "hello " + new Date());
        return "didispace.com";
    }

    public void uidParamFlowExceptionHandlerB(int uid, String str, BlockException ex) {
        System.out.println(ex);
    }

    /**
     * 热点参数进行限流
     * 在控制层配置失效
     *
     * @return
     */
    @GetMapping("/ok")
    public String ok() {
        paramFlowService.paramFlowD(12345, "hello " + new Date());
        return "didispace.com";
    }

}
