package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import com.ord.core.crud.service.CrudAppService;
import com.ord.core.util.StringUtils;
import com.ord.tutorial.dto.user.*;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.enums.PermissionValue;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.service.RoleService;
import com.ord.tutorial.service.UserService;
import com.ord.tutorial.service.impl.RoleServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserApiResource extends CrudAppService<
        User,
        Long,
        AdminUserDto,
        UserPageRequest,
        AdminUserDto,
        UserCreateDto,
        UserUpdateDto> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    protected Specification<User> buildSpecificationForPaging(UserPageRequest pageRequest) {
        return SpecificationBuilder.<User>builder()
                .withLikeFts(pageRequest.getFts(), "username", "email", "fullName")
                .withEqIfNotNull("enabled", pageRequest.getEnabled())
                .withRange("createdDate", pageRequest.getCreatedDate())
                .build();
    }

    @Override
    protected void validationBeforeCreate(UserCreateDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throwBusiness("username.exists");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throwBusiness("email.exists");
        }
    }

    @Override
    protected User convertCreateInputToEntity(UserCreateDto userCreateDto) {
        var user = super.convertCreateInputToEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setEnabled(Boolean.TRUE);
        return user;
    }

    @Override
    protected String getGetPagedListPolicy() {
        return PermissionValue.ADMIN_USER_GET_PAGED.getValue();
    }

    @Override
    protected String getCreatePolicy() {
        return PermissionValue.ADMIN_USER_CREATE.getValue();
    }

    @Override
    protected String getUpdatePolicy() {
        return PermissionValue.ADMIN_USER_UPDATE.getValue();
    }

    @Override
    protected String getRemovePolicy() {
        return PermissionValue.ADMIN_USER_DELETE.getValue();
    }

    @Override
    protected void validationBeforeUpdate(UserUpdateDto userDto, User entityToUpdate) {
        if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), entityToUpdate.getId())) {
            throwBusiness("email.exists");
        }
    }

    @Override
    public CommonResultDto<AdminUserDto> getByEncodedId(String encodedId) {
        return super.getByEncodedId(encodedId);
    }

    @Override
    protected AdminUserDto convertEntityToDto(User entity) {
        var adminUserDto = super.convertEntityToDto(entity);
        adminUserDto.setRoles(roleService.getRoleNameByUserId(entity.getId()));
        adminUserDto.setPermissions(roleService.getPermissionByUserId(entity.getId()));
        return adminUserDto;
    }

    @Override
    protected OrdEntityRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected String getEntityName() {
        return "user";
    }

}
