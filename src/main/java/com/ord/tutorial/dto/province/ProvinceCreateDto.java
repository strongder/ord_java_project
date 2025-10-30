package com.ord.tutorial.dto.province;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceCreateDto {

    @NotBlank
    @Size(max = 20)
    private String code;
    @NotBlank
    @Size(max = 200)
    private String name;
}
