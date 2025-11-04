package com.ord.tutorial.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    private String oldPassword;
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 32 ký tự")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số"
    )
    private String newPassword;
}
