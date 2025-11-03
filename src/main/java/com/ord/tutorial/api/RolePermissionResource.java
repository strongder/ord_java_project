package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dto.role_per.AssignPermissionDto;
import com.ord.tutorial.dto.role_per.RoleDto;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role-permissions")
public class RolePermissionResource extends SimpleCrudAppService<RoleEntity, Integer, RoleDto, RoleDto, PagedResultRequestDto> {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @PostMapping("/assign-permissions")
    public CommonResultDto<?> assignPermissionsToRole(@RequestBody AssignPermissionDto dto) {
        roleService.assignPermissionsToRole(dto);
        return CommonResultDto.success("success.operation");
    }

    @GetMapping("/get-permissions/{roleId}")
    public CommonResultDto<List<String>> getRolePermissions(@PathVariable Integer roleId) {
        var permissions = roleService.getRolePermissions(roleId);
        return CommonResultDto.success(permissions);
    }

    @GetMapping("/get-all")
    public CommonResultDto<List<RoleDto>> getAllRole() {
        var permissions = roleService.getAllRoles();
        return CommonResultDto.success(permissions);
    }
    @Override
    protected OrdEntityRepository<RoleEntity, Integer> getRepository() {
        return roleRepository;
    }

    @Override
    protected String getEntityName() {
        return "Role";
    }
}
