package com.alibaba.sentinel.limiting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 这个配置与Sentinel 无关,只是用来测试一下怎么通过application.properties 配置到这个类里的成员变量
 */

@ConfigurationProperties(
        prefix = "com.yjs.test"
)
@Component
@Data
public class SimulateProperties {
    private String shuaiqi;

    public String getShuaiqi() {
        return shuaiqi;
    }

    public void setShuaiqi(String shuaiqi) {
        this.shuaiqi = shuaiqi;
    }
}
