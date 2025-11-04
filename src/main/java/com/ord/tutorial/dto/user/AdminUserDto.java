package com.ord.tutorial.dto.user;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class AdminUserDto extends UserDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> roles;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> permissions;
}
