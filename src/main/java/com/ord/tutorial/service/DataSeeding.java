package com.ord.tutorial.service;

import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.entity.RolePermissionEntity;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.entity.UserRoleEntity;
import com.ord.tutorial.enums.PermissionValue;
import com.ord.tutorial.enums.Role;
import com.ord.tutorial.repository.RolePermissionRepository;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeding implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RolePermissionRepository rpRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.count() == 0) this.seedDataRole();
        if (rpRepo.count() == 0) seedDataPermission();
        if (userRepository.count() == 0) seedDataUser();
    }

    @Transactional
    public void seedDataRole() {
        roleRepository.saveAll(List.of(
                        new RoleEntity(null, Role.ADMIN.name()),
                        new RoleEntity(null, Role.USER.name()),
                        new RoleEntity(null, Role.SA.name()),
                        new RoleEntity(null, Role.ANONYMOUS.name())
                )
        );
    }

    @Transactional
    public void seedDataUser() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin@123"));
        adminUser.setEmail("admin@gmail.com");
        adminUser.setFullName("Administrator");
        adminUser.setEnabled(true);
        adminUser =  userRepository.saveAndFlush(adminUser);

        Integer roleId = roleRepository.findIdByName(Role.ADMIN.name());
        userRoleRepository.save(new UserRoleEntity(null,  adminUser.getId(), roleId) );
    }

    @Transactional
    public void seedDataPermission() {
        List<RolePermissionEntity> rolePermissions = List.of(
                new RolePermissionEntity(null, 1, PermissionValue.PROVINCE_GET_PAGED.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.PROVINCE_CREATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.PROVINCE_UPDATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.PROVINCE_DELETE.getValue()),

                new RolePermissionEntity(null, 1, PermissionValue.WARD_GET_PAGED.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.WARD_CREATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.WARD_UPDATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.WARD_DELETE.getValue()),

                new RolePermissionEntity(null, 1, PermissionValue.ADMIN_USER_GET_PAGED.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ADMIN_USER_CREATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ADMIN_USER_UPDATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ADMIN_USER_DELETE.getValue()),

                new RolePermissionEntity(null, 1, PermissionValue.ROLE_GET_PAGE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ROLE_CREATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ROLE_UPDATE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ROLE_DELETE.getValue()),
                new RolePermissionEntity(null, 1, PermissionValue.ASSIGN_PERMISSION_TO_ROLE.getValue())
        );
        rpRepo.saveAll(rolePermissions);
    }
}
