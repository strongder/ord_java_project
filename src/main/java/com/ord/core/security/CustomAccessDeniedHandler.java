package com.ord.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.crud.service.CommonResultFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final CommonResultFactory commonResultFactory;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        CommonResultDto<?> result = commonResultFactory.fail(CommonResultCode.UNAUTHORIZED,
                "forbidden");

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
