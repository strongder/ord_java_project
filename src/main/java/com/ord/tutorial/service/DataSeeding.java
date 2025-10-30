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
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeding implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RolePermissionRepository rpRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) seedDataRole();
        if (rpRepo.count() == 0) seedDataPermission(); // cần roleId từ DB
        if (userRepository.count() == 0) seedDataUser(); // user
    }

    @Transactional
    public void seedDataRole() {
        roleRepository.saveAll(List.of(
                        new RoleEntity(null, Role.ADMIN),
                        new RoleEntity(null, Role.USER),
                        new RoleEntity(null, Role.SA),
                        new RoleEntity(null, Role.ANONYMOUS)
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

        Integer roleId = roleRepository.findIdByName(Role.ADMIN);
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
                new RolePermissionEntity(null, 1, PermissionValue.WARD_DELETE.getValue())
        );
        rpRepo.saveAll(rolePermissions);
    }


}
