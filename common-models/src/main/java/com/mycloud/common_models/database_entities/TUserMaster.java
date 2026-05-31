package com.mycloud.common_models.database_entities;

import com.mycloud.common_models.base_entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_user_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TUserMaster extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "phone", nullable = false)
    private Integer phone;

    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}