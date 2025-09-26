package com.musinsa.freepoint.config;


import com.musinsa.freepoint.common.ApiHeaderValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiHeaderValidationInterceptor apiHeaderValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiHeaderValidationInterceptor)
                .addPathPatterns("/api/**"); // 적용 경로 지정
    }
}