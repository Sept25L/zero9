package com.zero9.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import java.util.Collections;


// 跨域配置类
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        // 允许哪些来源访问（比如localhost）
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许携带哪些请求头（token...）
        config.setAllowedHeaders(Collections.singletonList("*"));
        // 允许哪些http方法(get,post,put,delete)
        config.setAllowedMethods(Collections.singletonList("*"));
        // 是否允许发送凭证
        config.setAllowCredentials(true);
        // 预检请求缓存时间
        config.setMaxAge(1800L);
        //创建机遇URL的跨域配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册配置：对所有路径应用上面的配置规则
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}