package com.ord.core.crud.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ord.core.crud.enums.CommonResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResultDto<TResult> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private TResult data;
    private String code;
    private String message;
    private Map<String, Object> additionalProperties;

    @JsonIgnore
    public CommonResultDto<TResult> addAdditionalProperty(String key, Object value) {
        if (this.additionalProperties == null) {
            this.additionalProperties = new TreeMap<>();
        }
        this.additionalProperties.put(key, value);
        return this;
    }

    public Boolean getIsSuccessful() {
        return CommonResultCode.SUCCESS.toString().equals(code);
    }

    // ---------- Static helper methods ----------

    public static <T> CommonResultDto<T> success() {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message("Success")
                .build();
    }

    public static <T> CommonResultDto<T> success(T data) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> CommonResultDto<T> success(T data, String message) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.SUCCESS.toString())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResultDto<T> fail(String message) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.ERR_SERVER.toString())
                .message(message)
                .build();
    }

    public static <T> CommonResultDto<T> fail(Exception ex) {
        return CommonResultDto.<T>builder()
                .code(CommonResultCode.ERR_SERVER.toString())
                .message("Có lỗi trong quá trình xử lý")
                .build();
    }

    public static <T> CommonResultDto<T> fail(String code, String message) {
        return CommonResultDto.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> CommonResultDto<T> fail(CommonResultCode resultCode, String message) {
        return fail(resultCode.toString(), message);
    }
}
