package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface RolePermissionRepository extends OrdEntityRepository<RolePermissionEntity, Long> {

    @Query("SELECT rp.permissionName FROM RolePermissionEntity rp WHERE rp.roleId IN :roleIds")
    List<String> findByRoleIds(List<Integer> roleIds);


    @Query("SELECT rp.permissionName FROM RolePermissionEntity rp JOIN UserRoleEntity ur ON rp.roleId = ur.roleId " +
            "WHERE ur.userId = :userId")
    List<String> getRolePermissionEntitiesByUserId(Long userId);

    @Query("DELETE FROM RolePermissionEntity rp WHERE rp.roleId = :roleId AND rp.permissionName IN :permissionsToRemove")
    @Modifying
    void deleteByRoleIdAndPermissionNames(Integer roleId, Set<String> permissionsToRemove);
}
