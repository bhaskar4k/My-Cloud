package com.mycloud.common_config.config;

import com.mycloud.common_config.loader.JsonConfigLoader;
import com.mycloud.common_config.model.DatabaseConfig;
import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_config.model.StorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeans {

    @Bean
    public DatabaseConfig databaseConfig() {
        return JsonConfigLoader.load(
                "database-config.json",
                DatabaseConfig.class
        );
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