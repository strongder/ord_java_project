package com.ord.core.security;

public interface IdCodec {
    String encode(Object id);

    <T> T decode(String encodedId, Class<T> targetType);
}
