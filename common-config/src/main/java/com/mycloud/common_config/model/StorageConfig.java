package com.mycloud.common_config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfig {

    private String rootDirectory;

    private String tempDirectory;

    private String finalDirectory;

    private long maxFileSize;
}