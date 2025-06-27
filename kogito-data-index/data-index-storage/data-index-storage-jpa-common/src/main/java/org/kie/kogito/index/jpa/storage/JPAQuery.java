/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.jpa.storage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.SortDirection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;

import static java.util.stream.Collectors.toList;

public class JPAQuery<E extends AbstractEntity, T> implements Query<T> {

    protected final EntityManager em;
    private Integer limit;
    private Integer offset;
    private List<AttributeFilter<?>> filters;
    private List<AttributeSort> sortBy;
    protected final Class<E> entityClass;
    protected final Function<E, T> mapper;
    private Optional<JsonPredicateBuilder> jsonPredicateBuilder;

    public JPAQuery(EntityManager em, Function<E, T> mapper, Class<E> entityClass) {
        this(em, mapper, entityClass, Optional.empty());
    }

    public JPAQuery(EntityManager em, Function<E, T> mapper, Class<E> entityClass, Optional<JsonPredicateBuilder> jsonPredicateBuilder) {
        this.em = em;
        this.mapper = mapper;
        this.entityClass = entityClass;
        this.jsonPredicateBuilder = jsonPredicateBuilder;
    }

    @Override
    public Query<T> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<T> offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<T> filter(List<AttributeFilter<?>> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public Query<T> sort(List<AttributeSort> sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public List<T> execute() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = builder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        addWhere(builder, criteriaQuery, root);
        if (sortBy != null && !sortBy.isEmpty()) {
            List<Order> orderBy = sortBy.stream().map(f -> {
                Path attributePath = getAttributePath(root, f.getAttribute());
                return f.getSort() == SortDirection.ASC ? builder.asc(attributePath) : builder.desc(attributePath);
            }).collect(toList());
            criteriaQuery.orderBy(orderBy);
        }
        jakarta.persistence.Query query = em.createQuery(criteriaQuery);
        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }
        return (List<T>) query.getResultList().stream().map(mapper).collect(toList());
    }

    protected Function<AttributeFilter<?>, Predicate> filterPredicateFunction(Root<E> root, CriteriaBuilder builder) {
        return filter -> jsonPredicateBuilder.filter(b -> filter.isJson()).map(b -> b.buildPredicate(filter, root, builder))
                .orElseGet(() -> buildPredicateFunction(filter, root, builder));
    }

    protected final Predicate buildPredicateFunction(AttributeFilter filter, Root<E> root, CriteriaBuilder builder) {
        switch (filter.getCondition()) {
            case CONTAINS:
                return builder.isMember(filter.getValue(), getAttributePath(root, filter.getAttribute()));
            case CONTAINS_ALL:
                List<Predicate> predicatesAll = (List<Predicate>) ((List) filter.getValue()).stream()
                        .map(o -> builder.isMember(o, getAttributePath(root, filter.getAttribute()))).collect(toList());
                return builder.and(predicatesAll.toArray(new Predicate[] {}));
            case CONTAINS_ANY:
                List<Predicate> predicatesAny = (List<Predicate>) ((List) filter.getValue()).stream()
                        .map(o -> builder.isMember(o, getAttributePath(root, filter.getAttribute()))).collect(toList());
                return builder.or(predicatesAny.toArray(new Predicate[] {}));
            case IN:
                return getAttributePath(root, filter.getAttribute()).in((Collection<?>) filter.getValue());
            case LIKE:
                return builder.like(getAttributePath(root, filter.getAttribute()),
                        filter.getValue().toString().replaceAll("\\*", "%"));
            case EQUAL:
                return builder.equal(getAttributePath(root, filter.getAttribute()), filter.getValue());
            case IS_NULL:
                Path pathNull = getAttributePath(root, filter.getAttribute());
                return isPluralAttribute(filter.getAttribute()) ? builder.isEmpty(pathNull) : builder.isNull(pathNull);
            case NOT_NULL:
                Path pathNotNull = getAttributePath(root, filter.getAttribute());
                return isPluralAttribute(filter.getAttribute()) ? builder.isNotEmpty(pathNotNull) : builder.isNotNull(pathNotNull);
            case BETWEEN:
                List<Object> value = (List<Object>) filter.getValue();
                return builder
                        .between(getAttributePath(root, filter.getAttribute()), (Comparable) value.get(0),
                                (Comparable) value.get(1));
            case GT:
                return builder.greaterThan(getAttributePath(root, filter.getAttribute()), (Comparable) filter.getValue());
            case GTE:
                return builder.greaterThanOrEqualTo(getAttributePath(root, filter.getAttribute()),
                        (Comparable) filter.getValue());
            case LT:
                return builder.lessThan(getAttributePath(root, filter.getAttribute()), (Comparable) filter.getValue());
            case LTE:
                return builder
                        .lessThanOrEqualTo(getAttributePath(root, filter.getAttribute()), (Comparable) filter.getValue());
            case OR:
                return builder.or(getRecursivePredicate(filter, root, builder).toArray(new Predicate[] {}));
            case AND:
                return builder.and(getRecursivePredicate(filter, root, builder).toArray(new Predicate[] {}));
            case NOT:
                return builder.not(filterPredicateFunction(root, builder).apply((AttributeFilter<?>) filter.getValue()));
            default:
                return null;
        }

    }

    private Path getAttributePath(Root<E> root, String attribute) {
        String[] split = attribute.split("\\.");
        if (split.length == 1) {
            return root.get(attribute);
        }

        Join join = root.join(split[0]);
        for (int i = 1; i < split.length - 1; i++) {
            join = join.join(split[i]);
        }
        return join.get(split[split.length - 1]);
    }

    private boolean isPluralAttribute(final String attribute) {
        return this.em.getMetamodel().entity(this.entityClass).getDeclaredPluralAttributes().stream()
                .map(Attribute::getName)
                .anyMatch(pluralAttribute -> pluralAttribute.equals(attribute));
    }

    private List<Predicate> getRecursivePredicate(AttributeFilter<?> filter, Root<E> root, CriteriaBuilder builder) {
        return ((List<AttributeFilter<?>>) filter.getValue())
                .stream()
                .map(filterPredicateFunction(root, builder))
                .collect(toList());
    }

    @Override
    public long count() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(builder.count(root));
        addWhere(builder, criteriaQuery, root);
        return em.createQuery(criteriaQuery).getSingleResult();
    }

    private <V> void addWhere(CriteriaBuilder builder, CriteriaQuery<V> criteriaQuery, Root<E> root) {
        if (filters != null && !filters.isEmpty()) {
            criteriaQuery.where(filters.stream().map(filterPredicateFunction(root, builder)).toArray(Predicate[]::new));
        }
    }
}
