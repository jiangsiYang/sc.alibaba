package com.alibaba.nacos.controller;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 这个service-provider是对应的服务端的application.name配置
 * 这个接口其实就是对应请求服务端对应接口的配置，比如方法名称、REST、参数是啥、返回是啥，统一配置在这里方便修改，调用方使用起来
 * 也会和调用本地方法一样的手感
 */
@FeignClient("service-provider")
public interface FeignClientBean {

    //对应服务端接口的配置
    @GetMapping("/hello")
    String hello(@RequestParam(name = "name") String name);
}
