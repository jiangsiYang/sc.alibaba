package com.alibaba.nacos.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/feign")
public class FeignController {

//    @Autowired
//    private FeignClient client;

//    @GetMapping("/test")
//    public String test() {
//        String result = client.hello("didi");
//        return "Return : " + result;
//    }
}
