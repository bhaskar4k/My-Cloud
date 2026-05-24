package com.mycloud.data_access_layer.configs;

import com.mycloud.common_config.model.DatabaseConfig;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final DatabaseConfig databaseConfig;

    // Spring constructor injection automatically brings in the configuration bean loaded from JSON
    public DataSourceConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(databaseConfig.getUrl())
                .username(databaseConfig.getUsername())
                .password(databaseConfig.getPassword())
                .driverClassName(databaseConfig.getDriverClassName())
                .build();
    }
}