package com.ord.tutorial.entity;

import com.ord.core.crud.entity.BaseEntity;
import com.ord.tutorial.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "roles")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity implements BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Role roleName;
}
