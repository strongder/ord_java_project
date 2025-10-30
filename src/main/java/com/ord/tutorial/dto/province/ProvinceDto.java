package com.ord.tutorial.dto.province;

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
public class ProvinceDto extends EncodedIdDto<Integer> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @NotBlank
    @Size(max = 20)
    private String code;
    @NotBlank
    @Size(max = 200)
    private String name;

}
