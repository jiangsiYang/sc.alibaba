package com.dubbo;

import org.apache.dubbo.config.annotation.Service;

/**
 * 注意：这里的@Service注解不是Spring的，而是org.apache.dubbo.config.annotation.Service注解
 * 然后client模块就能通过@Reference注解引用到这里了
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
