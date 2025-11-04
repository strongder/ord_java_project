package com.ord.tutorial.dto.role_per;

import com.ord.core.crud.dto.EncodedIdDto;
import lombok.Data;

@Data
public class RoleDto extends EncodedIdDto<Integer> {
    private String roleName;
}
