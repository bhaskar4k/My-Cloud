package com.mycloud.common_models.common_entities;

import com.mycloud.common_models.enums.UploadStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FileInformationEntity {
    private int id;
    private String fileId;
    private String originalName;
    private String fileExtension;
    private Long fileSize;
    private String createdAt;
}
