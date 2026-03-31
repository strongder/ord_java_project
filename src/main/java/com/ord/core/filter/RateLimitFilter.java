package com.ord.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.crud.service.CommonResultFactory;
import com.ord.core.service.RateLimitService;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final CommonResultFactory commonResultFactory;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // có thể lấy theo userId
        String ip = request.getRemoteAddr();  // dia chi ip
        String uri = request.getRequestURI();

        String key = "rate_limit:ip:" + ip + ":" + uri;
        boolean allowed = rateLimitService.allowRequest(key);
        if (allowed) {
            filterChain.doFilter(request, response);
        } else {

            response.setStatus(429);
            CommonResultDto<?> result = commonResultFactory.fail(CommonResultCode.TOO_MANY_REQUEST,
                    "too_many_request");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }


    //region su dung bucket4j
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // ===== 1. Tạo key =====
//        String ip = request.getRemoteAddr();
//        String uri = request.getRequestURI();
//
//        String key = "rate_limit:ip:" + ip + ":" + uri;
//
//        // ===== 2. Consume =====
//        ConsumptionProbe probe = rateLimitService.consume(key);
//
//        // ===== 3. Set header chuẩn =====
//        response.setHeader("X-Rate-Limit-Remaining",
//                String.valueOf(probe.getRemainingTokens()));
//
//        response.setHeader("X-Rate-Limit-Retry-After",
//                String.valueOf(TimeUnit.NANOSECONDS
//                        .toSeconds(probe.getNanosToWaitForRefill())));
//
//        // ===== 4. Check =====
//        if (!probe.isConsumed()) {
//
//            response.setStatus(429);
//            response.setContentType("application/json;charset=UTF-8");
//
//            CommonResultDto<?> result = commonResultFactory.fail(
//                    CommonResultCode.TOO_MANY_REQUEST,
//                    "too_many_request"
//            );
//
//            response.getWriter().write(objectMapper.writeValueAsString(result));
//            return;
//        }
//
//        // ===== 5. Cho request đi tiếp =====
//        filterChain.doFilter(request, response);
//    }
    //endregion

}
