package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.enums.Role;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends OrdEntityRepository<RoleEntity, Integer> {
    @Query("SELECT r.id FROM RoleEntity r WHERE r.roleName = :role")
    Integer findIdByName(String role);

    @Query("SELECT r.roleName FROM RoleEntity r join UserRoleEntity ur On r.id = ur.roleId WHERE ur.userId = :userId")
    List<String> findRoleNamesByUserId(Long userId);
}
