package com.alibaba.sentinel.limiting;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
