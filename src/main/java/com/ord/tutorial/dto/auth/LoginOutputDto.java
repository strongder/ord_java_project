package com.ord.tutorial.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginOutputDto {
    private String token;
    private String type = "jwt";
}
