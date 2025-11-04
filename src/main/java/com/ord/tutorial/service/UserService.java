package com.ord.tutorial.service;

import com.ord.tutorial.dto.user.ChangePasswordDto;
import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.dto.user.UserUpdateDto;
import com.ord.tutorial.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    User getCurrentUser();
    User getUserById(Integer userId);
    User getUserByUsername(String username);
    User getUserByEmail(String email);

    void ChangePassword(ChangePasswordDto dto);

    @Transactional
    UserDto updateProfile(UserUpdateDto dto);
}
