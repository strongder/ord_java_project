package com.ord.tutorial.service;

import com.ord.tutorial.dto.role_per.AssignPermissionDto;
import com.ord.tutorial.dto.role_per.RoleDto;

import java.util.List;

public interface RoleService {
    void assignPermissionsToRole(AssignPermissionDto dto);
    List<String> getRolePermissions(Integer roleId);
    List<RoleDto> getAllRoles();

    List<String> getPermissionByUserId(Long id);

    List<String> getRoleNameByUserId(Long id);
}
