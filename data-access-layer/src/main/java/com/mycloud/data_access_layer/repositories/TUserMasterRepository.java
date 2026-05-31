package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TUserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TUserMasterRepository extends JpaRepository<TUserMaster, Long> {

    Optional<TUserMaster> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<TUserMaster> findByDeletedFalse(Pageable pageable);

}