package com.ord.tutorial.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto extends UserDtoBase {
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 32 ký tự")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số"
    )
    private String password;
}
