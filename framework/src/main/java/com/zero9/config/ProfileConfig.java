package com.zero9.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zero9")
public class ProfileConfig {

    // 文件上传基本路径
    @Getter
    private static String profile;

    public void setProfile(String profile) {
        ProfileConfig.profile = profile;
    }
}
