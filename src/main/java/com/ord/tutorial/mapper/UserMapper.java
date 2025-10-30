package com.ord.tutorial.mapper;

import com.ord.core.crud.mapper.BaseMapper;
import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {

}

