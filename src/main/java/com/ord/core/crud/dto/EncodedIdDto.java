package com.ord.core.crud.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class  EncodedIdDto<TKey> implements Serializable {
    private String encodedId;
    private TKey id;
}
