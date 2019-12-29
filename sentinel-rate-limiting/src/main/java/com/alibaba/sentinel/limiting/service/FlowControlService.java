package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlowControlService {

    /**
     * 添加@SentinelResource注解，指定value来配置资源点，这个资源点就可以实现流控和降级策略，这里是流控的test
     * 用了这个注解，可以精确到某个方法而不是整个接口
     *
     * @param str
     */
    @SentinelResource(value = "flowControl", blockHandler = "flowControlExceptionHandler")
    public void flowControl(String str) {
        System.out.println(str);
    }

    /**
     * 限流与阻塞处理
     *
     * @param str
     * @param ex
     */
    public void flowControlExceptionHandler(String str, BlockException ex) {
        System.out.println(str);
        System.out.println(ex);
    }

    public void notAddFlowControl(String str) {
        System.out.println(str);
    }
}
