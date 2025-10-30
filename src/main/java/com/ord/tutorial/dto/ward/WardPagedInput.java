package com.ord.tutorial.dto.ward;

import com.ord.core.crud.dto.PagedResultRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardPagedInput extends PagedResultRequestDto {
    private String provinceCode;
    private String provinceName;
}
