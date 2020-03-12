package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.sentinel.limiting.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 异常数 (DEGRADE_GRADE_EXCEPTION_COUNT)：当资源近 1 分钟的异常数目超过阈值之后会进行熔断。注意由于统计时间窗口是分钟级别的，
 * 若 timeWindow 小于 60s，则结束熔断状态后仍可能再进入熔断状态。
 */
@Slf4j
@Service
public class ExceptionCountDegradeService {

    /**
     * 方法参数可以使用对象类型，只要保证跟降级方法抱持一致即可
     *
     * @param user
     */
    @SentinelResource(value = "exCountA", fallback = "rtFallbackHandlerA")
    public void rtA(User user) {
        System.out.println("A执行了");
        rtB(user);
    }

    /**
     * error:为什么这里rtB不能熔断降级呢？因为是被调用的方法吗？
     * 很奇怪，如果A调用B，B有@SentinelResource注解，熔断失效，但是如果用手写的SphU.entry("rtB") 又能生效，说明@SentinelResource 在这种场景下有问题
     */
//    @SentinelResource(value = "exCountB", fallback = "rtFallbackHandlerB")
    public void rtB(User user) {
//        rtC(user);

        Entry entry = null;
        try {
            entry = SphU.entry("exCountB");
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
            if (System.currentTimeMillis() % 2 == 0) {
                Thread.sleep(2000);
                throw new RuntimeException("hhh");
            }
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
