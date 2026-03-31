package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.service.CommonResultFactory;
import com.ord.tutorial.dto.auth.LoginInputDto;
import com.ord.tutorial.dto.auth.LoginOutputDto;
import com.ord.tutorial.dto.auth.RegisterDto;
import com.ord.tutorial.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiResource {
    private final AuthService authService;
    private final CommonResultFactory commonResultFactory;

    @PostMapping(path = "/login")
    public CommonResultDto<LoginOutputDto> login(@Valid @RequestBody LoginInputDto input) {
        var token = authService.login(input.getUsername(), input.getPassword());
        return commonResultFactory.success(LoginOutputDto.builder().token(token).build());
    }

    @PostMapping(path = "/register")
    public CommonResultDto<String> register(@Valid @RequestBody RegisterDto input) {
        authService.register(input);
        return commonResultFactory.success("register.success");
    }
}
