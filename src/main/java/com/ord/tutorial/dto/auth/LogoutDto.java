package com.ord.tutorial.dto.auth;

import lombok.Data;

@Data
public class LogoutDto {
    private String refreshToken;
    private String deviceId;
    private boolean isAll;
}
