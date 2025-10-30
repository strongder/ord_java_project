package com.ord.core.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private String entityName;
    private String id;

    public NotFoundException() {
        super();
    }

    public NotFoundException withEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public NotFoundException withId(String id) {
        this.id = id;
        return this;
    }
}