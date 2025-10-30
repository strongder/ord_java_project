package com.ord.core.crud.enums;

public enum CommonResultCode {
    SUCCESS("00"),
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    FORBIDDEN("403"),
    NOT_FOUND("404"),
    ERR_SERVER("500");
    private final String text;

    CommonResultCode(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}