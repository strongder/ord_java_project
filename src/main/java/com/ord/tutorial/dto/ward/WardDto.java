package com.ord.tutorial.dto.ward;

import com.ord.core.crud.dto.EncodedIdDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WardDto extends EncodedIdDto<Integer> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @NotBlank
    @Size(max = 20)
    private String code;
    @NotBlank
    @Size(max = 200)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String provinceCode;
    private String provinceName;
}
