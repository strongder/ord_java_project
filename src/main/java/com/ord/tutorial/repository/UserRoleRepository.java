package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository extends OrdEntityRepository<UserRoleEntity, Long> {

    @Query("SELECT ur.roleId FROM UserRoleEntity ur WHERE ur.userId = :id")
    List<Integer> findRoleIdsByUserId(Long id);
}
