package com.ord.tutorial.config;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.service.I18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class I18nServiceInjector {

    @Autowired
    public I18nServiceInjector(I18nService i18nService) {
        CommonResultDto.setI18nService(i18nService);
    }
}
