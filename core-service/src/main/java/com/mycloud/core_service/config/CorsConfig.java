package com.mycloud.core_service.config;

import com.mycloud.common_config.model.HttpConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final HttpConfig httpConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = httpConfig.getAllowedOrigins().split(",");

        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}