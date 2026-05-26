package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_role_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TRoleMaster extends BaseEntity {

    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}