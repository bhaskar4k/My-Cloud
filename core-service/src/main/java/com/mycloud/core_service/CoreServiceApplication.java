package com.mycloud.core_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.mycloud.core_service",
		"com.mycloud.common_config",
		"com.mycloud.data_access_layer"
})
@EntityScan(basePackages = "com.mycloud.common_models")
@EnableJpaRepositories(basePackages = "com.mycloud.data_access_layer")
public class CoreServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoreServiceApplication.class, args);
	}
}
