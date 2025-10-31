package com.ord.core.logging.audit_log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuditLogFilter extends OncePerRequestFilter {

    private final AuditLogProducer auditLogProducer;

    public AuditLogFilter(AuditLogProducer auditLogProducer) {
        this.auditLogProducer = auditLogProducer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String traceId = request.getHeader("X-Trace-Id");
        if(traceId == null || traceId.isEmpty()) {
            traceId = java.util.UUID.randomUUID().toString();
        }
        filterChain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;
        AuditLogDto log = AuditLogDto.builder()
                .method(request.getMethod())
                .uri(request.getRequestURI())
                .clientIp(request.getRemoteAddr())
                .username(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous")
                .status(response.getStatus())
                .durationMs(duration)
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();

        auditLogProducer.send(log);
    }
}
