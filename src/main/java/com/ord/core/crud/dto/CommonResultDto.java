package com.ord.core.crud.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ord.core.crud.enums.CommonResultCode;
import lombok.*;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
