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
public class AuthApiResource extends CommonResultFactory{
    private final AuthService authService;

    @PostMapping(path = "/login")
    public CommonResultDto<LoginOutputDto> login(@Valid @RequestBody LoginInputDto input) {
        var token = authService.login(input.getUsername(), input.getPassword());
        return success(LoginOutputDto.builder().token(token).build());
    }

    @PostMapping(path = "/register")
    public CommonResultDto<String> register(@Valid @RequestBody RegisterDto input) {
        authService.register(input);
        return success("register.success");
    }
}
