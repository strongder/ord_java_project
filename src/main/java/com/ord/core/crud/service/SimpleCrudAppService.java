package com.ord.core.crud.service;

import com.ord.core.crud.dto.EncodedIdDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public abstract class SimpleCrudAppService<
        TEntity extends BaseEntity<TKey>,
        TKey,
        TEntityDto extends EncodedIdDto<TKey>,
        TCreateDto,
        TGetListInput extends PagedResultRequestDto>
        extends CrudAppService<
        TEntity,
        TKey,
        TEntityDto,
        TGetListInput,
        TEntityDto,
        TCreateDto,
        TEntityDto> {

}
