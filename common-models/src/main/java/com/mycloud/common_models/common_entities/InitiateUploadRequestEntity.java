package com.mycloud.common_models.common_entities;

import lombok.Data;

@Data
public class InitiateUploadRequestEntity {
    private String fileName;
    private long fileSize;
    private String contentType;
}
