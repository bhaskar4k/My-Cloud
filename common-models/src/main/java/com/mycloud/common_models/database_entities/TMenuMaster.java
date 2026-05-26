package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_menu_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TMenuMaster extends BaseEntity {
    @Column(name = "parent_id", nullable = true)
    private int parentId;

    @Column(name = "label", nullable = false, unique = true, length = 200)
    private String label;

    @Column(name = "icon", nullable = false, length = 100)
    private String icon;

    @Column(name = "route", nullable = true, unique = true, length = 500)
    private String route;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}