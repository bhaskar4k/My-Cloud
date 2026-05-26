package com.mycloud.common_config.builder;

import com.mycloud.common_config.enums.DatabaseType;
import com.mycloud.common_config.model.DatabaseConfig;

public class DatabaseUrlBuilder {
    public static String build(DatabaseConfig config) {
        String host = config.getResolvedHost();
        DatabaseType type = config.getDatabaseType();

        return switch (type) {
            case MYSQL ->
                    "jdbc:mysql://" + host + ":" + config.getPort()
                            + "/" + config.getDatabaseName()
                            + "?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";

            case SQL_SERVER ->
                    "jdbc:sqlserver://" + host + ":" + config.getPort()
                            + ";databaseName=" + config.getDatabaseName()
                            + ";encrypt=false";

            case ORACLE ->
                    "jdbc:oracle:thin:@"
                            + host + ":" + config.getPort()
                            + ":" + config.getDatabaseName();

            case POSTGRESQL ->
                    "jdbc:postgresql://" + host + ":" + config.getPort()
                            + "/" + config.getDatabaseName();

            case MARIADB ->
                    "jdbc:mariadb://" + host + ":" + config.getPort()
                            + "/" + config.getDatabaseName();

            case IBM_DB2 ->
                    "jdbc:db2://" + host + ":" + config.getPort()
                            + "/" + config.getDatabaseName();
        };
    }
}
