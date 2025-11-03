package com.ord.tutorial.service;

import com.ord.tutorial.dto.auth.RegisterDto;

public interface AuthService {
    String login(String username, String password);
    void register(RegisterDto dto);
}
