package com.ord.tutorial.entity;

import com.ord.core.crud.entity.AuditTableEntity;
import com.ord.core.crud.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "provinces")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceEntity extends AuditTableEntity implements Serializable, BaseEntity<Integer> {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
}