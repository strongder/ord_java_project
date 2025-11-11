package com.ord.tutorial.service.impl;

import com.ord.tutorial.dto.role_per.AssignPermissionDto;

import com.ord.tutorial.dto.role_per.RoleDto;
import com.ord.tutorial.entity.RolePermissionEntity;
import com.ord.tutorial.entity.UserRoleEntity;
import com.ord.tutorial.repository.RolePermissionRepository;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRoleRepository;
import com.ord.tutorial.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl  implements
        RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final ModelMapper objectMapper;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    @Override
    public void assignPermissionsToRole(AssignPermissionDto dto) {
        roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        List<String> existingPermissions =
                rolePermissionRepository.findByRoleIds(List.of((dto.getRoleId())));
        Set<String> newPermissions = new HashSet<>(dto.getPermissionNames());
        // Quyền cần thêm
        Set<String> permissionsToAdd = new HashSet<>(newPermissions);
        permissionsToAdd.removeAll(existingPermissions);
        // Quyền cần xóa
        Set<String> permissionsToRemove = new HashSet<>(existingPermissions);
        permissionsToRemove.removeAll(newPermissions);
        // Delete
        if (!permissionsToRemove.isEmpty()) {
            rolePermissionRepository.deleteByRoleIdAndPermissionNames(dto.getRoleId(), permissionsToRemove);
        }
        // Insert
        for (String perm : permissionsToAdd) {
            rolePermissionRepository.save(new RolePermissionEntity(null, dto.getRoleId(), perm));
        }
    }

    public void assignRoleToUser(Long userId, List<Integer> roleId) {
        userRoleRepository.deleteByUserId(userId);
        for (Integer rId : roleId) {
            userRoleRepository.save(
                    new UserRoleEntity(
                            null,
                            userId,
                            rId
                    )
            );
        }
    }

    @Override
    public List<String> getRolePermissions(Integer roleId) {
        return rolePermissionRepository.findByRoleIds(List.of(roleId));
    }
    public List<String> getRoleNameByUserId(Long userId) {
        return roleRepository.findRoleNamesByUserId(userId);
    }

    @Override
    public List<Integer> getRoleIdsByUserId(Long userId) {
        return userRoleRepository.findRoleIdsByUserId(userId);
    }


    public List<String> getPermissionByUserId(Long userId) {
        return rolePermissionRepository.getRolePermissionEntitiesByUserId(userId);
    }
    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(entity -> objectMapper.map(entity, RoleDto.class))
                .toList();
    }

}
