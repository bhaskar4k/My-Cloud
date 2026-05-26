package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TRoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TRoleMasterRepository extends JpaRepository<TRoleMaster, Long> {

}