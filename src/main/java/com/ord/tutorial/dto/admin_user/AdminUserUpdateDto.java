package com.ord.tutorial.dto.admin_user;

import com.ord.tutorial.dto.user.UserUpdateDto;
import lombok.Data;

import java.util.List;

@Data
public class AdminUserUpdateDto extends UserUpdateDto {
    public List<Integer> roleIds;
}
