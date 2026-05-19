package com.mycloud.common_config.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class JsonConfigLoader {

    private static final ObjectMapper mapper =
            new ObjectMapper();

    private JsonConfigLoader() {
    }

    public static <T> T load(
            String fileName,
            Class<T> clazz
    ) {

        try {

            InputStream inputStream =
                    JsonConfigLoader.class
                            .getClassLoader()
                            .getResourceAsStream(
                                    "config/" + fileName
                            );

            if (inputStream == null) {

                throw new RuntimeException(
                        "Config file not found: " + fileName
                );
            }

            return mapper.readValue(
                    inputStream,
                    clazz
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to load config: " + fileName,
                    ex
            );
        }
    }
}