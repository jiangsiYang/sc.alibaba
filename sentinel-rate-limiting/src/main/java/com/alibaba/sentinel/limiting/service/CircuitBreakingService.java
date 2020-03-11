package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.sentinel.limiting.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CircuitBreakingService {

    @SentinelResource(value = "rtA", fallback = "rtFallbackHandlerA")
    public void rtA(String str) {
        System.out.println("A执行了");
        User user = new User();
        user.setName("天天");
        rtB(user);
    }

    /**
     * 添加@SentinelResource注解，指定value来配置资源点，这个资源点就可以实现流控和降级策略，这里是熔断降级的test
     * 用了这个注解，可以精确到某个方法而不是整个接口
     */
    @SentinelResource(value = "rtB", fallback = "rtFallbackHandlerB")
    public void rtB(User user) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(user.getName());
    }

    /**
     * 限流与阻塞处理
     *
     * @param str
     */
    public void rtFallbackHandlerA(String str) {
        System.out.println("触发熔断降级了哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
    }

    /**
     * 限流与阻塞处理
     *
     * @param user
     */
    public void rtFallbackHandlerB(User user) {
        System.out.println("触发熔断降级了哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
    }

}
