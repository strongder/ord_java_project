package com.ord.core.security;

import com.ord.core.exception.OrdBusinessException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SimpleIdCodec implements IdCodec {

    private static final String SALT = "ord-secure";

    @Override
    public String encode(Object id) {
        String raw = SALT + ":" + id;
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(String encodedId, Class<T> targetType) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encodedId), StandardCharsets.UTF_8);
            String realId = decoded.replace(SALT + ":", "");
            if (targetType == Long.class) {
                return (T) Long.valueOf(realId);
            } else if (targetType == Integer.class) {
                return (T) Integer.valueOf(realId);
            } else if (targetType == String.class) {
                return (T) realId;
            }
            throw new OrdBusinessException("Unsupported ID type: " + targetType);
        } catch (Exception e) {
            throw new OrdBusinessException("Invalid ID format");
        }
    }
}
