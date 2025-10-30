package com.ord.core.security.jwt;

public final class JwtClaimNames {

    private JwtClaimNames() {
        // private constructor to prevent instantiation
    }

    public static final String USER_ID = "userId";
    public static final String ROLE = "role";
    public static final String SHOP_ID = "shopId";
    public static final String TENANT_CODE = "tenantCode";
    public static final String FULL_NAME = "fullName";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
}
