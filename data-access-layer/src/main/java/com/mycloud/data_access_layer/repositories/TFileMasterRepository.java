package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TFileMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TFileMasterRepository extends JpaRepository<TFileMaster, Long> {
    // Custom query method to locate records via your UUID fileId token
    Optional<TFileMaster> findByFileId(String fileId);
}