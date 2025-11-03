package com.ord.core.crud.service;

import com.ord.core.crud.dto.CommonResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class I18nServiceInjector {

    @Autowired
    public I18nServiceInjector(I18nService i18nService) {
        CommonResultDto.setI18nService(i18nService);
    }
}
