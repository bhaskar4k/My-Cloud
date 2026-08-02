package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import com.mycloud.common_models.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_folder_master", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_parent_folder_id", columnList = "parent_folder_id"),
        @Index(name = "idx_user_id_and_parent_folder_id", columnList = "user_id, parent_folder_id"),
        @Index(name = "idx_path", columnList = "path")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TFolderMaster extends BaseEntity {

    @Column(name = "parent_folder_id")
    private Long parentFolderId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "deleted", updatable = true)
    private Boolean deleted = false;

    @Column(name = "deleted_at", nullable = true, updatable = true)
    private LocalDateTime deletedAt;
}