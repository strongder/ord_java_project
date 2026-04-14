package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.service.CommonResultFactory;
import com.ord.tutorial.dto.auth.*;
import com.ord.tutorial.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiResource extends CommonResultFactory{
    private final AuthService authService;

    @PostMapping(path = "/login")
    public CommonResultDto<LoginOutputDto> login(@Valid @RequestBody LoginInputDto input) {
        return success(authService.login(input.getUsername(), input.getPassword()));
    }

    @PostMapping(path = "/register")
    public CommonResultDto<String> register(@Valid @RequestBody RegisterDto input) {
        authService.register(input);
        return success("register.success");
    }
    @PostMapping("/logout")
    public CommonResultDto<?> logout(
            HttpServletRequest request,
            @RequestBody LogoutDto dto
    ) {
        authService.logout(request, dto);
        return success("logout success");
    }

    @PostMapping(path = "/refresh-token")
    public CommonResultDto<LoginOutputDto> refreshToken(@RequestBody RefreshTokenInputDto input) {
        return success(authService.refreshToken(input.getRefreshToken()));
    }
}
