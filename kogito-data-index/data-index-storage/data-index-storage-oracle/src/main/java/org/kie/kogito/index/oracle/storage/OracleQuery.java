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
package org.kie.kogito.index.oracle.storage;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.kie.kogito.index.oracle.model.AbstractEntity;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.SortDirection;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import static java.util.stream.Collectors.toList;

public class OracleQuery<E extends AbstractEntity, T> implements Query<T> {

    private PanacheRepositoryBase<E, String> repository;
    private Integer limit;
    private Integer offset;
    private List<AttributeFilter<?>> filters;
    private List<AttributeSort> sortBy;
    private Class<E> entityClass;
    private Function<E, T> mapper;

    public OracleQuery(PanacheRepositoryBase<E, String> repository, Function<E, T> mapper, Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityClass = entityClass;
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
        CriteriaBuilder builder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = builder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        if (filters != null && !filters.isEmpty()) {
            List<Predicate> predicates = getPredicates(builder, root);
            criteriaQuery.where(predicates.toArray(new Predicate[] {}));
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            List<Order> orderBy = sortBy.stream().map(f -> {
                Path attributePath = getAttributePath(root, f.getAttribute());
                return f.getSort() == SortDirection.ASC ? builder.asc(attributePath) : builder.desc(attributePath);
            }).collect(toList());
            criteriaQuery.orderBy(orderBy);
        }

        javax.persistence.Query query = repository.getEntityManager().createQuery(criteriaQuery);

        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }
        return (List<T>) query.getResultList().stream().map(mapper).collect(toList());
    }

    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<E> root) {
        return filters.stream().map(filterPredicateFunction(root, builder)).collect(toList());
    }

    private Function<AttributeFilter<?>, Predicate> filterPredicateFunction(Root<E> root, CriteriaBuilder builder) {
        return filter -> {
            String attribute = filter.getAttribute();
            Object value = filter.getValue();
            switch (filter.getCondition()) {
                case CONTAINS:
                    return builder.isMember(value, getAttributePath(root, attribute));
                case CONTAINS_ALL:
                    List<Predicate> predicatesAll = (List<Predicate>) ((List) value).stream()
                            .map(o -> builder.isMember(o, getAttributePath(root, attribute))).collect(toList());
                    return builder.and(predicatesAll.toArray(new Predicate[] {}));
                case CONTAINS_ANY:
                    List<Predicate> predicatesAny = (List<Predicate>) ((List) value).stream()
                            .map(o -> builder.isMember(o, getAttributePath(root, attribute))).collect(toList());
                    return builder.or(predicatesAny.toArray(new Predicate[] {}));
                case IN:
                    return getAttributePath(root, attribute).in((Collection<?>) value);
                case LIKE:
                    return builder.like(getAttributePath(root, attribute),
                            value.toString().replaceAll("\\*", "%"));
                case EQUAL:
                    return builder.equal(getAttributePath(root, attribute), value);
                case IS_NULL:
                    Path pathNull = getAttributePath(root, attribute);
                    return pathNull instanceof PluralAttributePath ? builder.isEmpty(pathNull) : builder.isNull(pathNull);
                case NOT_NULL:
                    Path pathNotNull = getAttributePath(root, attribute);
                    return pathNotNull instanceof PluralAttributePath ? builder.isNotEmpty(pathNotNull) : builder.isNotNull(pathNotNull);
                case BETWEEN:
                    List<Object> v = (List<Object>) value;
                    return builder.between(getAttributePath(root, attribute), (Comparable) v.get(0),
                            (Comparable) v.get(1));
                case GT:
                    return builder.greaterThan(getAttributePath(root, attribute), (Comparable) value);
                case GTE:
                    return builder.greaterThanOrEqualTo(getAttributePath(root, attribute),
                            (Comparable) value);
                case LT:
                    return builder.lessThan(getAttributePath(root, attribute), (Comparable) value);
                case LTE:
                    return builder.lessThanOrEqualTo(getAttributePath(root, attribute), (Comparable) value);
                case OR:
                    return builder.or(getRecursivePredicate(filter, root, builder).toArray(new Predicate[] {}));
                case AND:
                    return builder.and(getRecursivePredicate(filter, root, builder).toArray(new Predicate[] {}));
                case NOT:
                    return builder.not(filterPredicateFunction(root, builder).apply((AttributeFilter<?>) value));
                default:
                    return null;
            }
        };
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

    private List<Predicate> getRecursivePredicate(AttributeFilter<?> filter, Root<E> root, CriteriaBuilder builder) {
        return ((List<AttributeFilter<?>>) filter.getValue())
                .stream()
                .map(filterPredicateFunction(root, builder))
                .collect(toList());
    }

}
