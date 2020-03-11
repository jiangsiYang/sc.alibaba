package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * qps是以秒为单位
 * 阈值为0 则拒绝一切访问
 */
@Slf4j
@Service
public class ParamFlowService {

    /**
     * 热点参数限流A调用B，如何限流规则在A，会起作用，但是限流规则在B，则会失效,很奇怪
     *
     * @param str
     */
    @SentinelResource(value = "uidParamFlow", blockHandler = "uidParamFlowExceptionHandler")
    public void paramFlowA(int uid, String str) {
        System.out.println("1111");
        paramFlowB(uid, str);
    }

    /**
     * 配置这个函数是按uid热点参数限流
     *
     * @param str
     */
    public void paramFlowB(int uid, String str) {
        System.out.println("2222");
    }

    /**
     * 限流与阻塞处理
     *
     * @param str
     * @param ex
     */
    public void uidParamFlowExceptionHandler(int uid, String str, BlockException ex) {
        System.out.println(ex);
    }

    public void paramFlowC(int uid, String str) {
        System.out.println("2222");
    }


    /**
     * 测试不是int，而是Integer
     * 结论：单机可以，集群没试过
     * 另注意：blockHandler 不能和paramFlowA 共用uidParamFlowExceptionHandler（好像是因为一个int，一个Integer？），否则会报个内部错误
     *
     * @param uid
     * @param str
     */
    @SentinelResource(value = "uidParamFlowC", blockHandler = "uidParamFlowExceptionHandlerC")
    public void paramFlowD(Integer uid, String str) {
        System.out.println("ok");
    }

    /**
     * 限流与阻塞处理
     *
     * @param str
     * @param ex
     */
    public void uidParamFlowExceptionHandlerC(Integer uid, String str, BlockException ex) {
        System.out.println(ex);
    }
}
