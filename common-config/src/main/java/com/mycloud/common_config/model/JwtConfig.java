package com.mycloud.common_config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {

    private String secret;

    private long expiration;

    private String[] allowedEndpoints;
}