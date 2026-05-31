package com.mycloud.common_config.config;

import com.mycloud.common_config.builder.DatabaseUrlBuilder;
import com.mycloud.common_config.loader.JsonConfigLoader;
import com.mycloud.common_config.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeans {

    @Bean
    public GatewayConfig gatewayConfig() {
        return JsonConfigLoader.load(
                "gateway-config.json",
                GatewayConfig.class
        );
    }

    @Bean
    public HttpConfig httpConfig() {
        return JsonConfigLoader.load(
                "http-config.json",
                HttpConfig.class
        );
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        DatabaseConfig config = JsonConfigLoader.load(
                "database-config.json",
                DatabaseConfig.class
        );

        config.setUrl(DatabaseUrlBuilder.build(config));

        return config;
    }

    @Bean
    public JwtConfig jwtConfig() {
        return JsonConfigLoader.load(
                "jwt-config.json",
                JwtConfig.class
        );
    }

    @Bean
    public StorageConfig storageConfig() {
        return JsonConfigLoader.load(
                "storage-config.json",
                StorageConfig.class
        );
    }
}