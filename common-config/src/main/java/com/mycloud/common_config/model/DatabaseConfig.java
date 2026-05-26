package com.mycloud.common_config.model;

import com.mycloud.common_config.enums.DatabaseType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {

    private DatabaseType databaseType;

    private boolean production;

    private String serverIp;

    private int port;

    private String databaseName;

    private String username;

    private String password;

    private String driverClassName;

    private String url;


    // =========================
    // RESOLVED HOST
    // =========================
    public String getResolvedHost() {
        if (!production && ("localhost".equalsIgnoreCase(serverIp) || "127.0.0.1".equals(serverIp))) {
            return "localhost";
        }

        return serverIp;
    }
}