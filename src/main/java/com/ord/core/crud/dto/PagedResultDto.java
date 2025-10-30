package com.ord.core.crud.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class PagedResultDto<T> {
    private long totalCount;
    private List<T> items;

    public PagedResultDto() {
    }

    public PagedResultDto(long totalCount, List<T> items) {
        this.totalCount = totalCount;
        this.items = items;
    }

    public PagedResultDto(int totalCount, List<T> items) {
        this.totalCount = totalCount;
        this.items = items;
    }

    public static <T> PagedResultDto<T> empty() {
        return new PagedResultDto<>(0, Collections.emptyList());
    }
}
