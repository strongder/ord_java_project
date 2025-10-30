package com.ord.tutorial.dao;

import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.dto.user.UserPageRequest;
import com.ord.tutorial.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    User findById(Long id);

    Long getPageCount(UserPageRequest input);

    List<UserDto> getPageItems(UserPageRequest input);
}
