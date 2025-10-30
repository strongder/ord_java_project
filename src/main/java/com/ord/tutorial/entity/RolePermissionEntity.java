package com.ord.tutorial.entity;

import com.ord.core.crud.entity.BaseEntity;
import com.ord.tutorial.enums.PermissionValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity implements BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "permission_name", nullable = false)
    private String permissionName; // ví dụ: "province.create"
}
