package com.ord.tutorial.enums;

public enum PermissionValue
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
    USER_CREATE("user.create"),
    USER_GET_PAGED("user.get-paged"),
    USER_UPDATE("user.update"),
    USER_DELETE("user.delete"),
    USER_UPDATE_SELF("user.update-self");

    private final String value;
    PermissionValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
