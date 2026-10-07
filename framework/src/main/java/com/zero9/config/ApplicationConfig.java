package com.zero9.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@MapperScan("com.zero9.mapper")
public class ApplicationConfig {
    // 时区配置bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder ->
                // 设置时区为系统默认时区
                jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }
}
