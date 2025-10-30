package com.ord.core.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Slf4j
public class OrdBusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<String, Object> additionalProperties = new TreeMap<>();

    public OrdBusinessException(String message) {
        super(message);
    }

    public OrdBusinessException withProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
        return this;
    }
}
