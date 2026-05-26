package com.mycloud.data_access_layer.repositories;

import com.mycloud.common_models.database_entities.TMenuMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TMenuMasterRepository extends JpaRepository<TMenuMaster, Long> {

    @Query("""
        SELECT m
        FROM TMenuMaster m
        WHERE m.id IN (
            SELECT rmm.menuMasterId
            FROM TRoleMenuMapping rmm
            WHERE rmm.roleMasterId = :roleId
        )
        AND m.active = true
        ORDER BY m.displayOrder ASC
    """)
    List<TMenuMaster> findMenusByRoleId(@Param("roleId") Long roleId);
}