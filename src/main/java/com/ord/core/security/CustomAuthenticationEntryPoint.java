package com.ord.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.enums.CommonResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CommonResultDto<?> result = CommonResultDto.fail(CommonResultCode.UNAUTHORIZED,
                "Bạn chưa đăng nhập");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
