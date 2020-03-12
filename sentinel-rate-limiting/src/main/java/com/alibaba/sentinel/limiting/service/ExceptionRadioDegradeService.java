package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.sentinel.limiting.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 异常比例 (DEGRADE_GRADE_EXCEPTION_RATIO)：当资源的每秒请求量 >= 5，并且每秒异常总数占通过量的比值超过阈值（DegradeRule 中的 count）之后，
 * 资源进入降级状态，即在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地返回。异常比率的阈值范围是 [0.0, 1.0]，
 * 代表 0% - 100%。
 */
@Slf4j
@Service
public class ExceptionRadioDegradeService {

    /**
     * 方法参数可以使用对象类型，只要保证跟降级方法抱持一致即可
     *
     * @param user
     */
    @SentinelResource(value = "exRadioA", fallback = "rtFallbackHandlerA")
    public void rtA(User user) {
        System.out.println("A执行了");
        rtB(user);
    }

    /**
     * error:为什么这里rtB不能熔断降级呢？因为是被调用的方法吗？
     * 很奇怪，如果A调用B，B有@SentinelResource注解，熔断失效，但是如果用手写的SphU.entry("rtB") 又能生效，说明@SentinelResource 在这种场景下有问题
     */
//    @SentinelResource(value = "exRadioB", fallback = "rtFallbackHandlerB")
    public void rtB(User user) {
        Entry entry = null;
        try {
            System.out.println("执行B");
            entry = SphU.entry("exRadioB");
            try {
                rtC(user);
            } catch (RuntimeException e) {
                System.out.println("C 异常了");
            }
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
        System.out.println("执行C");
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
