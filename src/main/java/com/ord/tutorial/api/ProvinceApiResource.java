package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dao.ProvinceDao;
import com.ord.tutorial.dto.province.ProvinceCreateDto;
import com.ord.tutorial.dto.province.ProvinceDto;
import com.ord.tutorial.entity.ProvinceEntity;
import com.ord.tutorial.repository.ProvinceRepository;
import com.ord.tutorial.repository.WardRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/provinces")
public class ProvinceApiResource extends SimpleCrudAppService<
        ProvinceEntity,
        Integer,
        ProvinceDto,
        ProvinceCreateDto,
        PagedResultRequestDto
        > {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final ProvinceDao provinceDao;

    @GetMapping("/dropdown")
    @Cacheable(value = "provinces_dropdown", key = "'all'")
    public List<ProvinceDto> getAllProvinces() {
        var provinces  = provinceRepository.findAll();
        // map thẳng ra ArrayList, không dùng TypeToken
        return provinces.stream()
                .map(entity -> objectMapper.map(entity, ProvinceDto.class))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "provinces_dropdown", allEntries = true)
    @Override
    public CommonResultDto<ProvinceDto> create(ProvinceCreateDto provinceDto) {
        return super.create(provinceDto);
    }

    @CacheEvict(value = "provinces_dropdown", allEntries = true)
    @Override
    public CommonResultDto<ProvinceDto> update(String encodedId, ProvinceDto provinceDto) {
        return super.update(encodedId, provinceDto);
    }

    @CacheEvict(value = "provinces_dropdown", allEntries = true)
    @Override
    public CommonResultDto<ProvinceDto> remove(String encodedId) {
        return super.remove(encodedId);
    }

    @Override
    protected Integer getTotalCount(PagedResultRequestDto pagedResultRequestDto) {
        return provinceDao.getPageCount(pagedResultRequestDto);
    }

    @Override
    protected List<ProvinceDto> fetchPagedItems(PagedResultRequestDto pagedResultRequestDto) {
        return provinceDao.getPageItems(pagedResultRequestDto);
    }

    @Override
    protected void validationBeforeCreate(ProvinceCreateDto provinceDto) {
        if (provinceRepository.existsByCode(provinceDto.getCode())) {
            throwBusiness("Mã tỉnh đã tồn tại");
        }
    }

    @Override
    protected void validationBeforeUpdate(ProvinceDto provinceDto, ProvinceEntity entityToUpdate) {
        if (provinceRepository.existsByCodeAndIdNot(provinceDto.getCode(), entityToUpdate.getId())) {
            throwBusiness("Mã tỉnh đã tồn tại trong hệ thống");
        }
    }

    @Override
    protected void validationBeforeRemove(ProvinceEntity entityToRemove) {
        if (wardRepository.existsByProvinceCode(entityToRemove.getCode())) {
            throwBusiness("Tỉnh đã được sử dụng, không thể xóa");
        }
    }

    @Override
    protected OrdEntityRepository<ProvinceEntity, Integer> getRepository() {
        return provinceRepository;
    }


    @Override
    protected String getEntityName() {
        return "province";
    }
}
