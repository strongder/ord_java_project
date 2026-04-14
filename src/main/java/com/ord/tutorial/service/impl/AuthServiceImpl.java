package com.ord.tutorial.service.impl;

import com.ord.core.exception.OrdBusinessException;
import com.ord.core.security.jwt.JwtClaimNames;
import com.ord.core.security.jwt.JwtService;
import com.ord.core.util.StringUtils;
import com.ord.tutorial.dto.auth.LoginOutputDto;
import com.ord.tutorial.dto.auth.LogoutDto;
import com.ord.tutorial.dto.auth.RegisterDto;
import com.ord.tutorial.entity.RefreshToken;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.enums.Role;
import com.ord.tutorial.repository.*;
import com.ord.tutorial.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;


    private static final Long EXPIRED = 7 * 6 * 24L;
    public LoginOutputDto login(String username, String password) {
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
        return genToken(user);
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

    @Override
    public void logout(HttpServletRequest request, LogoutDto dto) {
        String token = resolveToken(request);
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (token != null && jwtService.isTokenValid(token, username)) {
            String jti = jwtService.extractJti(token);
            Date exp = jwtService.extractExpiration(token);
            long ttl = exp.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        "blacklist:" + jti,
                        "1",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        }
        if (dto.getRefreshToken() != null) {
            refreshTokenRepository.revokeToken(dto.getRefreshToken());
        }
    }


    @Override
    public LoginOutputDto refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);
        if (token == null) {
            throw new OrdBusinessException("refresh token not found or revoked");
        }
        User user = userRepository.findById(token.getUserId()).orElseThrow(
                () -> new OrdBusinessException("user.not.found")
        );
        refreshTokenRepository.revokeToken(refreshToken);
        return genToken(user);
    }

    @Override
    public void deleteRefreshToken() {
        refreshTokenRepository.deleteExpiredAndRevoked();
    }

    private LoginOutputDto genToken(User user)
    {
        List<Integer> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        List<String> permissions = rolePermissionRepository.findByRoleIds(roleIds);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtClaimNames.USER_ID, user.getId());
        extraClaims.put(JwtClaimNames.PERMISSION, permissions);
        if (!StringUtils.isNullOrBlank(user.getEmail())) {
            extraClaims.put(JwtClaimNames.EMAIL, user.getEmail());
        }
        String token = jwtService.generateToken(extraClaims, user);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .isRevoked(false)
                .expiredAt(Instant.now().plusSeconds(EXPIRED))
                .build());
        return LoginOutputDto.builder().
                token(token).
                refreshToken(refreshToken)
                .type("jwt")
                .build();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void throwUserInvalid() {
        throw new OrdBusinessException("auth.invalid");
    }
}
