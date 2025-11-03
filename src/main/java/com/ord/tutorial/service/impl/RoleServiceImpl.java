package com.ord.tutorial.service.impl;

import com.ord.tutorial.dto.role_per.AssignPermissionDto;

import com.ord.tutorial.dto.role_per.RoleDto;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.entity.RolePermissionEntity;
import com.ord.tutorial.entity.UserRoleEntity;
import com.ord.tutorial.repository.RolePermissionRepository;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRoleRepository;
import com.ord.tutorial.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl  implements
        RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final ModelMapper objectMapper;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void assignPermissionsToRole(AssignPermissionDto dto) {
        RoleEntity role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + dto.getRoleId()));
        rolePermissionRepository.deleteByRoleId(dto.getRoleId());
        for (String permissionName : dto.getPermissionNames()) {
            rolePermissionRepository.save(
                    new RolePermissionEntity(
                            null,
                            dto.getRoleId(),
                            permissionName
                    )
            );
        }
    }
    public void assignRoleToUser(Long userId, Integer roleId) {
        userRoleRepository.save(
                new UserRoleEntity(
                        null,
                        userId,
                        roleId
                )
        );
    }

    @Override
    public List<String> getRolePermissions(Integer roleId) {
        return rolePermissionRepository.findByRoleIds(List.of(roleId));
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(entity -> objectMapper.map(entity, RoleDto.class))
                .toList();
    }

}
