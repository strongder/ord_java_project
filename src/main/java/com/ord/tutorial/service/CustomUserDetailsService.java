package com.ord.tutorial.service;

import com.ord.tutorial.enums.Role;
import com.ord.tutorial.repository.RolePermissionRepository;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDetail = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy roleId từ user_roles
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userDetail.getId());

        // Lấy permission từ role_permissions
        List<String> permissions = rolePermissionRepository.findByRoleIds(roleIds);

        List<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> (GrantedAuthority) () -> permission)
                .toList();

        userDetail.setAuthorities(authorities);
        // Trả về user với authorities
        return  userDetail;
    }
}
