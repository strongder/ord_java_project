package com.ord.tutorial.service;

import com.ord.tutorial.dto.auth.LoginOutputDto;
import com.ord.tutorial.dto.auth.LogoutDto;
import com.ord.tutorial.dto.auth.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;

public interface AuthService {
    LoginOutputDto login(String username, String password);
    void register(RegisterDto dto);

    void logout(HttpServletRequest request, LogoutDto dto);

    LoginOutputDto refreshToken(String refreshToken);

    @Scheduled(cron = "0 0 */6 * * *")
    void deleteRefreshToken();
}
