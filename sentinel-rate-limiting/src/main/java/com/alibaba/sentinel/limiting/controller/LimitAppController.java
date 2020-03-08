package com.alibaba.sentinel.limiting.controller;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.sentinel.limiting.service.FlowControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 测试下基于调用关系的流量控制
 * 参考https://github.com/alibaba/Sentinel/wiki/FAQ#q-%E6%80%8E%E4%B9%88%E9%92%88%E5%AF%B9%E7%89%B9%E5%AE%9A%E8%B0%83%E7%94%A8%E7%AB%AF%E9%99%90%E6%B5%81%E6%AF%94%E5%A6%82%E6%88%91%E6%83%B3%E9%92%88%E5%AF%B9%E6%9F%90%E4%B8%AA-ip-%E6%88%96%E8%80%85%E6%9D%A5%E6%BA%90%E5%BA%94%E7%94%A8%E8%BF%9B%E8%A1%8C%E9%99%90%E6%B5%81%E8%A7%84%E5%88%99%E9%87%8C%E9%9D%A2-limitapp%E6%B5%81%E6%8E%A7%E5%BA%94%E7%94%A8%E7%9A%84%E4%BD%9C%E7%94%A8
 */

@RestController
@RequestMapping(value = "/limit")
public class LimitAppController {

    @Autowired
    private FlowControlService flowControlService;

    /**
     * 根据调用来源进行限流
     *
     * @return
     */
    @GetMapping("/hello")
    public String hello() {
        //不生效，官网说注意：ContextUtil.enter(xxx) 方法仅在调用链路入口处生效，即仅在当前线程的初次调用生效，后面再调用不会覆盖当前线程的调用链路，直到 exit。
        //要设置来源是通过实现RequestOriginParser接口，参考MyRequestOriginParse
//        ContextUtil.enter("aaa", "app1");
        flowControlService.flowControl("hello " + new Date());
        return "didispace.com";
    }


    /**
     * 根据调用链路进行限流
     *
     * @return
     */
    @GetMapping("/entrance")
    public String entrance() {
        //不生效，官网说注意：ContextUtil.enter(xxx) 方法仅在调用链路入口处生效，即仅在当前线程的初次调用生效，后面再调用不会覆盖当前线程的调用链路，直到 exit。
        //设置链路入口也不生效
        ContextUtil.enter("aaa");
        flowControlService.flowControl("hello " + new Date());
        return "didispace.com";
    }


}
