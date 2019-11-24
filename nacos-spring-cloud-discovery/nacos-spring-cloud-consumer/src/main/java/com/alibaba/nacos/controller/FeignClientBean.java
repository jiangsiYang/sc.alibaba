package com.alibaba.nacos.controller;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("service-provider")
public interface FeignClientBean {
    @GetMapping("/hello")
    String hello(@RequestParam(name = "name") String name);
}
