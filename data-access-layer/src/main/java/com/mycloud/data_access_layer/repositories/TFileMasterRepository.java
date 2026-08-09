package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.enums.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TFileMasterRepository extends JpaRepository<TFileMaster, Long> {
    Optional<TFileMaster> findByFileIdAndUserIdAndDeleted(
            String fileId,
            Long userId,
            Boolean deleted
    );


    List<TFileMaster> findByUserIdAndParentFolderIdAndDeletedAndStatusOrderByCreatedAtDesc(
            Long userId,
            Long parentFolderId,
            Boolean deleted,
            UploadStatus status
    );

    Optional<TFileMaster> findByIdAndUserIdAndDeletedFalseAndStatus(
            Long id,
            Long userId,
            UploadStatus status
    );
}