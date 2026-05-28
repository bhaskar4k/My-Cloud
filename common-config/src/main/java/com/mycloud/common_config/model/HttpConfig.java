package com.mycloud.common_config.model;

import com.mycloud.common_config.enums.DatabaseType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpConfig {
    private String allowedOrigins;
}