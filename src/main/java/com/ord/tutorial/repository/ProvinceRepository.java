package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.ProvinceEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends OrdEntityRepository<ProvinceEntity, Integer> {
    boolean existsByCode(String code);

    // Kiểm tra khi update (loại trừ ID hiện tại)
    boolean existsByCodeAndIdNot(String code, Integer id);

    Optional<ProvinceEntity> findByCode(String code);
}
