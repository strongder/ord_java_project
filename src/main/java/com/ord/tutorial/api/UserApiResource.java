package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.service.CommonResultFactory;
import com.ord.tutorial.dto.user.ChangePasswordDto;
import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.dto.user.UserUpdateDto;
import com.ord.tutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiResource {

    private final UserService userService;
    private final ModelMapper objectMapper;
    private final CommonResultFactory commonResultFactory;
    @PostMapping(path = "/update-profile")
    public CommonResultDto<UserDto> updateProfile(@RequestBody UserUpdateDto input) {
        var updateDto = userService.updateProfile(input);
        return commonResultFactory.success(updateDto);
    }

    @GetMapping(path = "/me")
    public CommonResultDto<UserDto> getCurrentUser() {
        UserDto userDto = objectMapper.map(userService.getCurrentUser(), UserDto.class);
        return commonResultFactory.success(userDto);
    }

    @PostMapping(path = "/change-password")
    public CommonResultDto<Void> changePassword(@RequestBody ChangePasswordDto input) {
        userService.changePassword(input);
        return commonResultFactory.success("success.operation");
    }
}
