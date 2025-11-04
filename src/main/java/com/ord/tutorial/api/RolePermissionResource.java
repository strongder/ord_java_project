package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dto.role_per.AssignPermissionDto;
import com.ord.tutorial.dto.role_per.RoleDto;
import com.ord.tutorial.entity.ProvinceEntity;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.enums.PermissionValue;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
        hasPermission(PermissionValue.ASSIGN_PERMISSION_TO_ROLE.getValue());
        roleService.assignPermissionsToRole(dto);
        return CommonResultDto.success("success.operation");
    }

    @GetMapping("/get-permissions/{roleEnCodeId}")
    public CommonResultDto<List<String>> getRolePermissions(@PathVariable String roleEnCodeId) {
        var roleDto = getEntityDtoByEncodedId(roleEnCodeId);
        var permissions = roleService.getRolePermissions(roleDto.getId());
        return CommonResultDto.success(permissions);
    }

    @Override
    protected Specification<RoleEntity> buildSpecificationForPaging(PagedResultRequestDto input) {
        return SpecificationBuilder.<RoleEntity>builder()
                .build();
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
    protected String getGetPagedListPolicy() {
        return PermissionValue.ROLE_GET_PAGE.getValue();
    }

    @Override
    protected String getCreatePolicy() {
        return PermissionValue.ROLE_CREATE.getValue();
    }

    @Override
    protected String getUpdatePolicy() {
        return PermissionValue.ROLE_UPDATE.getValue();
    }

    @Override
    protected String getRemovePolicy() {
        return PermissionValue.ROLE_DELETE.getValue();
    }


    @Override
    protected String getEntityName() {
        return "Role";
    }
}
