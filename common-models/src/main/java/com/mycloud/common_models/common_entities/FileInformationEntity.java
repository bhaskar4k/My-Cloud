package com.mycloud.common_models.common_entities;

import com.mycloud.common_models.enums.UploadStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FileInformationEntity {
    private String FileId;
    private String OriginalName;
    private String FileExtension;
    private Long FileSize;
    private String CreatedAt;
    private String UploadedAgo;
}
