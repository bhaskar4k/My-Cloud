package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TFolderMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TFolderMasterRepository extends JpaRepository<TFolderMaster, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);
}