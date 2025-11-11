package com.ord.tutorial.service.impl;

import com.ord.core.exception.OrdBusinessException;
import com.ord.core.security.jwt.JwtClaimNames;
import com.ord.core.security.jwt.JwtService;
import com.ord.core.util.StringUtils;
import com.ord.tutorial.dto.auth.RegisterDto;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.enums.Role;
import com.ord.tutorial.repository.RolePermissionRepository;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.repository.UserRoleRepository;
import com.ord.tutorial.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final RoleServiceImpl roleService;

    public String login(String username, String password) {
        var user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throwUserInvalid();
        }
        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throwUserInvalid();
        }
        if (!user.isEnabled()) {
            throw new OrdBusinessException("auth.inactive");
        }
        List<Integer> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        List<String> permissions = rolePermissionRepository.findByRoleIds(roleIds);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtClaimNames.USER_ID, user.getId());
        extraClaims.put(JwtClaimNames.PERMISSION, permissions);
        if (!StringUtils.isNullOrBlank(user.getEmail())) {
            extraClaims.put(JwtClaimNames.EMAIL, user.getEmail());
        }
        return jwtService.generateToken(extraClaims, user);
    }

    @Override
    @Transactional
    public void register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new OrdBusinessException("username.exists");
        }
        var user = new com.ord.tutorial.entity.User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(Boolean.TRUE);
        User newUser = userRepository.save(user);
        Integer roleId = roleRepository.findIdByName(Role.USER.name());
        roleService.assignRoleToUser(newUser.getId(), List.of(roleId));
    }

    private void throwUserInvalid() {
        throw new OrdBusinessException("auth.invalid");
    }
}
