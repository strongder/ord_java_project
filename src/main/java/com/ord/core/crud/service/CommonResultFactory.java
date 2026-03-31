package com.ord.core.crud.service;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.util.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonResultFactory {

    private final Translator translator;

    public <T> CommonResultDto<T> success(T data) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message(resolveMessage("success.operation"))
                .data(data)
                .build();
    }

    public <T> CommonResultDto<T> success(T data, String messageKey) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message(resolveMessage(messageKey))
                .data(data)
                .build();
    }

    public <T> CommonResultDto<T> success(String messageKey) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message(resolveMessage(messageKey))
                .build();
    }

    public <T> CommonResultDto<T> fail(String messageKey) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.ERR_SERVER.toString())
                .message(resolveMessage(messageKey))
                .build();
    }

    public <T> CommonResultDto<T> fail() {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.ERR_SERVER.toString())
                .message(resolveMessage("error.processing"))
                .build();
    }

    public <T> CommonResultDto<T> fail(String code, String messageKey) {
        return CommonResultDto.<T>builder()
                .code(code)
                .message(resolveMessage(messageKey))
                .build();
    }

    public <T> CommonResultDto<T> fail(CommonResultCode resultCode, String messageKey) {
        return fail(resultCode.toString(), messageKey);
    }

    private String resolveMessage(String messageKey) {
        if (messageKey == null) {
            return null;
        }
        return translator.get(messageKey);
    }
}
