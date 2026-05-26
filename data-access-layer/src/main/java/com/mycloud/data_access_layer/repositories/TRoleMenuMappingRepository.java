package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TRoleMenuMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TRoleMenuMappingRepository extends JpaRepository<TRoleMenuMapping, Long> {

}