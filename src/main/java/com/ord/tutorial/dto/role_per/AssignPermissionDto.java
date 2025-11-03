package com.ord.tutorial.dto.role_per;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionDto {
    private Integer roleId;
    private List<String> permissionNames;

}
