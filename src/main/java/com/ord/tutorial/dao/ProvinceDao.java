package com.ord.tutorial.dao;

import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.tutorial.dto.province.ProvinceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProvinceDao {
    Integer getPageCount(PagedResultRequestDto input);

    List<ProvinceDto> getPageItems(PagedResultRequestDto input);
}
