package com.ord.tutorial.dto.user;

import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.dto.DateRangeFilterDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPageRequest extends PagedResultRequestDto {
    private Boolean isActive;
    private DateRangeFilterDto createdDate;
}
