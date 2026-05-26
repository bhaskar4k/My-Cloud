package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_role_menu_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TRoleMenuMapping extends BaseEntity {
    @Column(name = "menu_master_id", nullable = false)
    private Long menuMasterId;

    @Column(name = "role_master_id", nullable = false)
    private Long roleMasterId;
}