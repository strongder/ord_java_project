package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.WardEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends OrdEntityRepository<WardEntity, Integer> {
    boolean existsByProvinceCode(String provinceCode);

    boolean existsByCode(String code);

    Optional<WardEntity> findByCode(String code);

    long countByProvinceCode(String provinceCode);

    // Kiểm tra khi update (loại trừ ID hiện tại)
    boolean existsByCodeAndIdNot(String code, Integer id);
}
