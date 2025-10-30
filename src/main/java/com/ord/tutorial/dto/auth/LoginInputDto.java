package com.ord.tutorial.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInputDto {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_.]*$",
            message = "Username phải bắt đầu bằng chữ cái và chỉ chứa chữ cái, số, dấu _ hoặc ."
    )
    private String username;
    private String password;
}
