package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CircuitBreakingService {

    /**
     * 添加@SentinelResource注解，指定value来配置资源点，这个资源点就可以实现流控和降级策略，这里是熔断降级的test
     * 用了这个注解，可以精确到某个方法而不是整个接口
     *
     * @param str
     */
    @SentinelResource(value = "circuitBreaking", fallback = "fallbackHandler")
    public void circuitBreaking(String str) {
        System.out.println(str);
        throw new RuntimeException("发生异常");
    }

    /**
     * 限流与阻塞处理
     *
     * @param str
     */
    public void fallbackHandler(String str) {
        System.out.println("触发熔断降级了哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
    }

}
