package com.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class DubboClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboClientApplication.class, args);
    }

    @Slf4j
    @RestController
    static class TestController {

        /**
         * 注意,这里的Reference是org.apache.dubbo.config.annotation.Reference
         * 然后很神奇的就能直接引用server模块的service了
         */
        @Reference
        HelloService helloService;

        @GetMapping("/test")
        public String test() {
            return helloService.hello("didispace.com");
        }
    }
}
