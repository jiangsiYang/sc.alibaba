package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.sentinel.limiting.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 平均响应时间 (DEGRADE_GRADE_RT)：当 1s 内持续进入 5 个请求，对应时刻的平均响应时间（秒级）均超过阈值（count，以 ms 为单位），
 * 那么在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地熔断（抛出 DegradeException）。
 * 注意 Sentinel 默认统计的 RT 上限是 4900 ms，超出此阈值的都会算作 4900 ms，若需要变更此上限可以通过启动配置项
 * -Dcsp.sentinel.statistic.max.rt=xxx 来配置。
 */
@Slf4j
@Service
public class RtCircuitBreakingService {

    /**
     * 方法参数可以使用对象类型，只要保证跟降级方法抱持一致即可
     *
     * @param user
     */
    @SentinelResource(value = "rtA", fallback = "rtFallbackHandlerA")
    public void rtA(User user) {
        System.out.println("A执行了");
        rtB(user);
    }

    /**
     * error:为什么这里rtB不能熔断降级呢？因为是被调用的方法吗？
     * 很奇怪，如果A调用B，B有@SentinelResource注解，熔断失效，但是如果用手写的SphU.entry("rtB") 又能生效，说明@SentinelResource 在这种场景下有问题
     */
//    @SentinelResource(value = "rtB", fallback = "rtFallbackHandlerB")
    public void rtB(User user) {
//        rtC(user);

        Entry entry = null;
        try {
            entry = SphU.entry("rtB");
            rtC(user);
            System.out.println(user.getName());
        } catch (Exception e) {
            System.out.println("熔断了哈哈哈");
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }

    }

    public void rtC(User user) {
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
     * @param user
     */
    public void rtFallbackHandlerA(User user) {
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
