package com.ord.tutorial.enums;

import lombok.Getter;

@Getter
public enum PermissionValue//0
{
    PROVINCE_CREATE("province.create"),
    PROVINCE_GET_PAGED("province.get-paged"),
    PROVINCE_UPDATE("province.update"),
    PROVINCE_DELETE("province.delete"),

    WARD_CREATE("ward.create"),
    WARD_GET_PAGED("ward.get-paged"),
    WARD_UPDATE("ward.update"),
    WARD_DELETE("ward.delete"),

//    user
    ADMIN_USER_CREATE("user.create"),
    ADMIN_USER_GET_PAGED("user.get-paged"),
    ADMIN_USER_UPDATE("user.update"),
    ADMIN_USER_DELETE("user.delete"),
    USER_UPDATE_SELF("user.update-self"),

//    role
    ROLE_CREATE("role.create"),
    ROLE_GET_PAGE("role.get-paged"),
    ROLE_UPDATE("role.update"),
    ROLE_DELETE("role.delete"),
    ASSIGN_PERMISSION_TO_ROLE("role.assign-permission-to-role");

    private final String value;
    PermissionValue(String value) {
        this.value = value;
    }
}
