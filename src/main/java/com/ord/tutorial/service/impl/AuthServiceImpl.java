package com.ord.tutorial.service.impl;

import com.ord.core.exception.OrdBusinessException;
import com.ord.core.security.jwt.JwtClaimNames;
import com.ord.core.security.jwt.JwtService;
import com.ord.core.util.StringUtils;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(String username, String password) {
        var user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throwUserInvalid();
        }
        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throwUserInvalid();
        }
        if (!user.getEnabled()) {
            throw new OrdBusinessException("Tài khoản không được kích hoạt");
        }
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtClaimNames.USER_ID, user.getId());
        if (!StringUtils.isNullOrBlank(user.getEmail())) {
            extraClaims.put(JwtClaimNames.EMAIL, user.getEmail());
        }
        return jwtService.generateToken(extraClaims, user);
    }

    private void throwUserInvalid() {
        throw new OrdBusinessException("Tên đăng nhập hoặc mật khẩu không chính xác");
    }
}
