package com.ord.core.crud.repository.spec;

import com.ord.core.crud.dto.DateRangeFilterDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpecificationBuilder<T> {
    private final List<Specification<T>> specifications = new ArrayList<>();

    public static <T> SpecificationBuilder<T> builder() {
        return new SpecificationBuilder<>();
    }

    // ========== EQUALITY ==========

    public SpecificationBuilder<T> withEqIfNotNull(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.equal(getPath(root, field), value));
        }
        return this;
    }

    public SpecificationBuilder<T> withEq(String field, Object value) {
        specifications.add((root, query, cb) ->
                cb.equal(getPath(root, field), value));
        return this;
    }

    public SpecificationBuilder<T> withNotEq(String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.notEqual(getPath(root, field), value));
        }
        return this;
    }

    // ========== NULL CHECKS ==========

    public SpecificationBuilder<T> withIsNull(String field) {
        specifications.add((root, query, cb) ->
                getPath(root, field).isNull());
        return this;
    }

    public SpecificationBuilder<T> withIsNotNull(String field) {
        specifications.add((root, query, cb) ->
                getPath(root, field).isNotNull());
        return this;
    }

    // ========== COMPARISON ==========

    public <V extends Comparable<? super V>> SpecificationBuilder<T> withGreaterThan(String field, V value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.greaterThan(getPath(root, field), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> withGreaterThanOrEqualTo(String field, V value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(getPath(root, field), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> withLessThan(String field, V value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.lessThan(getPath(root, field), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> withLessThanOrEqualTo(String field, V value) {
        if (value != null) {
            specifications.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(getPath(root, field), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> withBetween(String field, V from, V to) {
        if (from != null && to != null) {
            specifications.add((root, query, cb) ->
                    cb.between(getPath(root, field), from, to));
        }
        return this;
    }

    // ========== COLLECTION OPERATIONS ==========

    public SpecificationBuilder<T> withIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            specifications.add((root, query, cb) ->
                    getPath(root, field).in(values));
        }
        return this;
    }

    public SpecificationBuilder<T> withNotIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            specifications.add((root, query, cb) ->
                    cb.not(getPath(root, field).in(values)));
        }
        return this;
    }

    // ========== STRING OPERATIONS ==========

    public SpecificationBuilder<T> withLike(String field, String value) {
        if (value != null && !value.isBlank()) {
            specifications.add((root, query, cb) ->
                    cb.like(cb.lower(getPath(root, field)), "%" + value.toLowerCase().trim() + "%"));
        }
        return this;
    }

    public SpecificationBuilder<T> withLikeFts(String keyword, String... fields) {
        if (keyword != null && !keyword.isBlank() && fields.length > 0) {
            specifications.add((root, query, cb) -> {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                List<Predicate> predicates = new ArrayList<>();
                for (String field : fields) {
                    predicates.add(cb.like(cb.lower(getPath(root, field)), like));
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }
        return this;
    }

    public SpecificationBuilder<T> withStartsWith(String field, String value) {
        if (value != null && !value.isBlank()) {
            specifications.add((root, query, cb) ->
                    cb.like(cb.lower(getPath(root, field)), value.toLowerCase().trim() + "%"));
        }
        return this;
    }

    public SpecificationBuilder<T> withEndsWith(String field, String value) {
        if (value != null && !value.isBlank()) {
            specifications.add((root, query, cb) ->
                    cb.like(cb.lower(getPath(root, field)), "%" + value.toLowerCase().trim()));
        }
        return this;
    }

    // ========== DATE/TIME OPERATIONS ==========

    public SpecificationBuilder<T> withAfter(String fieldName, LocalDate fromDate) {
        if (fromDate != null) {
            specifications.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(getPath(root, fieldName), fromDate.atStartOfDay()));
        }
        return this;
    }

    public SpecificationBuilder<T> withBefore(String fieldName, LocalDate toDate) {
        if (toDate != null) {
            specifications.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(getPath(root, fieldName), toDate.atTime(LocalTime.MAX)));
        }
        return this;
    }

    public SpecificationBuilder<T> withRange(String fieldName, LocalDate fromDate, LocalDate toDate) {
        withAfter(fieldName, fromDate);
        withBefore(fieldName, toDate);
        return this;
    }

    public SpecificationBuilder<T> withRange(String fieldName, DateRangeFilterDto range) {
        if (range != null) {
            withAfter(fieldName, range.getFromDate());
            withBefore(fieldName, range.getToDate());
        }
        return this;
    }

    public SpecificationBuilder<T> withDateTimeAfter(String fieldName, LocalDateTime fromDateTime) {
        if (fromDateTime != null) {
            specifications.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(getPath(root, fieldName), fromDateTime));
        }
        return this;
    }

    public SpecificationBuilder<T> withDateTimeBefore(String fieldName, LocalDateTime toDateTime) {
        if (toDateTime != null) {
            specifications.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(getPath(root, fieldName), toDateTime));
        }
        return this;
    }

    // ========== BOOLEAN OPERATIONS ==========

    public SpecificationBuilder<T> withIsTrue(String field) {
        specifications.add((root, query, cb) ->
                cb.isTrue(getPath(root, field)));
        return this;
    }

    public SpecificationBuilder<T> withIsFalse(String field) {
        specifications.add((root, query, cb) ->
                cb.isFalse(getPath(root, field)));
        return this;
    }

    // ========== CUSTOM SPECIFICATIONS ==========

    public SpecificationBuilder<T> with(Specification<T> specification) {
        if (specification != null) {
            specifications.add(specification);
        }
        return this;
    }

    public SpecificationBuilder<T> withOr(Specification<T>... orSpecifications) {
        if (orSpecifications != null && orSpecifications.length > 0) {
            Specification<T> orSpec = null;
            for (Specification<T> spec : orSpecifications) {
                orSpec = orSpec == null ? spec : orSpec.or(spec);
            }
            if (orSpec != null) {
                specifications.add(orSpec);
            }
        }
        return this;
    }

    // ========== JOIN OPERATIONS ==========

    public SpecificationBuilder<T> withJoinEq(String joinField, String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Join<Object, Object> join = root.join(joinField, JoinType.INNER);
                return cb.equal(join.get(field), value);
            });
        }
        return this;
    }

    public SpecificationBuilder<T> withLeftJoinEq(String joinField, String field, Object value) {
        if (value != null) {
            specifications.add((root, query, cb) -> {
                Join<Object, Object> join = root.join(joinField, JoinType.LEFT);
                return cb.equal(join.get(field), value);
            });
        }
        return this;
    }

    // ========== BUILD ==========

    public Specification<T> build() {
        if (specifications.isEmpty()) {
            return all();
        }

        return specifications.stream()
                .reduce(Specification::and)
                .orElse(all());
    }

    public Specification<T> buildOr() {
        if (specifications.isEmpty()) {
            return all();
        }

        return specifications.stream()
                .reduce(Specification::or)
                .orElse(all());
    }

    // ========== UTILITY ==========

    public SpecificationBuilder<T> clear() {
        specifications.clear();
        return this;
    }

    public boolean isEmpty() {
        return specifications.isEmpty();
    }

    private Specification<T> all() {
        return (root, query, cb) -> cb.conjunction();
    }

    // Helper method để xử lý nested fields (vd: "address.city")
    @SuppressWarnings("unchecked")
    private <Y> Path<Y> getPath(Path<?> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.");
            Path<?> path = root;
            for (String part : parts) {
                path = path.get(part);
            }
            return (Path<Y>) path;
        }
        return (Path<Y>) root.get(field);
    }
}