package com.ord.tutorial.dao;

import com.ord.tutorial.dto.ward.WardDto;
import com.ord.tutorial.dto.ward.WardPagedInput;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WardDao{
    Integer getPageCount(WardPagedInput input);
    List<WardDto> getPageItems(WardPagedInput input);
}
