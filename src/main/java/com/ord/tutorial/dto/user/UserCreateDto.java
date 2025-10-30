package com.ord.tutorial.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto extends UserDtoBase {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_.]*$",
            message = "Username phải bắt đầu bằng chữ cái và chỉ chứa chữ cái, số, dấu _ hoặc ."
    )
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 32 ký tự")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số"
    )
    private String password;
}
