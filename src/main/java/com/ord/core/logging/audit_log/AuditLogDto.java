package com.ord.core.logging.audit_log;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogDto {
    private String method;
    private String uri;
    private String clientIp;
    private String username;
    private int status;
    private long durationMs;
    private String requestBody;
    private String responseBody;
    private Instant timestamp;
}