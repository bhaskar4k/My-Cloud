package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TFileMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TFileMasterRepository extends JpaRepository<TFileMaster, Long> {
    Optional<TFileMaster> findByFileId(String fileId);

    List<TFileMaster> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);
}