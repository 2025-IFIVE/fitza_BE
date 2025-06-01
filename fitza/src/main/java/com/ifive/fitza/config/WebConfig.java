package com.ifive.fitza.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseDir = "file:" + System.getProperty("user.dir") + "/uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(baseDir);  // 하위 폴더 포함

        // 아래는 옵션이지만 혹시 몰라 명시적으로 추가
        registry.addResourceHandler("/uploads/original/**")
                .addResourceLocations(baseDir + "original/");

        registry.addResourceHandler("/uploads/cropped/**")
                .addResourceLocations(baseDir + "cropped/");

        registry.addResourceHandler("/profileimages/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/profileimages/");
    }
}

