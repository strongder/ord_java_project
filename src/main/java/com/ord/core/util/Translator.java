package com.ord.core.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Translator {

    private final MessageSource messageSource;

    public Translator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Lấy message theo locale hiện tại
     */
    public String get(String key) {
        return get(key, null, LocaleContextHolder.getLocale());
    }

    /**
     * Lấy message có args
     */
    public String get(String key, Object... args) {
        return get(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Lấy message với locale custom
     */
    public String get(String key, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception ex) {
            // log nếu cần
            return key; // fallback
        }
    }
}
