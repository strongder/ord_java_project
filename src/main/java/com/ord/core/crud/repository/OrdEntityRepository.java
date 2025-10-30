package com.ord.core.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrdEntityRepository<TEntity, TKey> extends
        JpaRepository<TEntity, TKey>,
        JpaSpecificationExecutor<TEntity>,
        CrudRepository<TEntity, TKey> {
}
