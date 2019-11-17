package com.alibaba.nacos.controller;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient("service-provider")
public interface FeignClient {
    @GetMapping("/hello")
    String hello(@RequestParam(name = "name") String name);
}
