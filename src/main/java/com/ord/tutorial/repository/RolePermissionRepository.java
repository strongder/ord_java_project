package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolePermissionRepository extends OrdEntityRepository<RolePermissionEntity, Long> {

    @Query("SELECT rp.permissionName FROM RolePermissionEntity rp WHERE rp.roleId IN :roleIds")
    List<String> findByRoleIds(List<Integer> roleIds);

    void deleteByRoleId(Integer roleId);
}
