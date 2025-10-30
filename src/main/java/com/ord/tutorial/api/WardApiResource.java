package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dao.WardDao;
import com.ord.tutorial.dto.province.ProvinceDto;
import com.ord.tutorial.dto.ward.WardDto;
import com.ord.tutorial.dto.ward.WardPagedInput;
import com.ord.tutorial.entity.WardEntity;
import com.ord.tutorial.repository.ProvinceRepository;
import com.ord.tutorial.repository.WardRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/wards")
public class WardApiResource extends SimpleCrudAppService<
        WardEntity,
        Integer,
        WardDto,
        WardDto,
        WardPagedInput> {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final WardDao wardDao;

    @Override
    protected OrdEntityRepository<WardEntity, Integer> getRepository() {
        return wardRepository;
    }

//    @Override
//    protected Specification<WardEntity> buildSpecificationForPaging(WardPagedInput input) {
//        return SpecificationBuilder.<WardEntity>builder()
//                .withEqIfNotNull("provinceCode", input.getProvinceCode())
//                .withLikeFts(input.getFts(), "code", "name")
//                .build();
//    }


    @Override
    protected List<WardDto> fetchPagedItems(WardPagedInput wardPagedInput) {
        return wardDao.getPageItems(wardPagedInput);
    }

    @Override
    protected Integer getTotalCount(WardPagedInput wardPagedInput) {
        return wardDao.getPageCount(wardPagedInput);
    }

    @Override
    protected void validationBeforeCreate(WardDto wardDto) {
        checkProvinceCode(wardDto.getProvinceCode());
        if (wardRepository.existsByCode(wardDto.getCode())) {
            throwBusiness("Mã xã đã tồn tại");
        }
        super.validationBeforeCreate(wardDto);
    }

    @Override
    protected void validationBeforeUpdate(WardDto wardDto, WardEntity entityToUpdate) {
        checkProvinceCode(wardDto.getProvinceCode());
        if (wardRepository.existsByCodeAndIdNot(wardDto.getCode(), entityToUpdate.getId())) {
            throwBusiness("Mã xã đã tồn tại trong hệ thống");
        }
        super.validationBeforeUpdate(wardDto, entityToUpdate);
    }

    private void checkProvinceCode(String provinceCode) {
        if (!provinceRepository.existsByCode(provinceCode)) {
            throwBusiness("Mã tỉnh không tồn tại");
        }
    }

    @Override
    protected void validationBeforeRemove(WardEntity entityToRemove) {
        // super.validationBeforeRemove(entityToRemove);
    }

    @Override
    protected String getEntityName() {
        return "ward";
    }
}
