package com.ord.tutorial.service.impl;

import com.ord.core.exception.OrdBusinessException;
import com.ord.tutorial.dto.user.ChangePasswordDto;
import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.dto.user.UserUpdateDto;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.repository.UserRepository;
import com.ord.tutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new OrdBusinessException("user.notfound");
        }
        return user.get();
    }
    @Override
    public User getUserById(Integer userId) {
        var user = userRepository.findById(Long.valueOf(userId));
        if (user.isEmpty()) {
            throw new OrdBusinessException("user.notfound");
        }
        return user.get();
    }

    @Override
    public User getUserByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new OrdBusinessException("user.notfound");
        }
        return user.get();
    }

    @Override
    public User getUserByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new OrdBusinessException("user.notfound");
        }
        return user.get();
    }

    @Override
    public void ChangePassword(ChangePasswordDto dto) {
        var user = getCurrentUser();
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new OrdBusinessException("password.password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserDto updateProfile(UserUpdateDto dto) {
        var currentUser = getCurrentUser();
        if (dto.getEmail() != null &&
                userRepository.existsByEmailAndIdNot(dto.getEmail(), currentUser.getId())) {
            throw new OrdBusinessException("email.exists");
        }
        modelMapper.map(dto, currentUser);
        userRepository.save(currentUser);
        return modelMapper.map(currentUser, UserDto.class);
    }
}
