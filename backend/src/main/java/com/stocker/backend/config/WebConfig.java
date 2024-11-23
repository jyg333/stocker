package com.stocker.backend.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*") // 허용할 출처 : 특정 도메인만 받을 수 있음
                .allowedMethods("GET", "POST","PUT","DELETE") // 허용할 HTTP method
                .allowCredentials(true);
    }
}
