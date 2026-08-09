package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import com.mycloud.common_models.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_master", indexes = {
        // Globally unique file identifier
        @Index(
                name = "idx_file_id",
                columnList = "file_id",
                unique = true
        ),

        // File/folder listing
        @Index(
                name = "idx_user_id_parent_folder_id_deleted_status_created_at",
                columnList = "user_id, parent_folder_id, deleted, status, created_at"
        ),

        // Recycle-bin automatic cleanup
        @Index(
                name = "idx_auto_delete_at",
                columnList = "auto_delete_at"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TFileMaster extends BaseEntity {
    @Column(name = "parent_folder_id", nullable = false)
    private Long parentFolderId;

    @Column(name = "file_id", nullable = false, unique = true, length = 36)
    private String fileId;

    @Column(name = "original_name", nullable = false, length = 400)
    private String originalName;

    @Column(name = "file_extension", nullable = false, length = 10)
    private String fileExtension;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "total_chunks", nullable = false)
    private Integer totalChunks;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "favourite", nullable = false)
    private Boolean favourite = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UploadStatus status;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "auto_delete_at")
    private Long autoDeleteAt;
}