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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.api.query.SortDirection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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

        criteriaQuery.select(root);
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

    /**
     * Creates a function that converts an AttributeFilter to a JPA Predicate.
     * 
     * @param root the query root
     * @param builder the criteria builder
     * @param criteriaQuery the criteria query
     * @return a function that converts filters to predicates
     */
    protected Function<AttributeFilter<?>, Predicate> filterPredicateFunction(Root<E> root, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
        return filter -> jsonPredicateBuilder.filter(b -> filter.isJson()).map(b -> b.buildPredicate(filter, root, builder))
                .orElseGet(() -> buildPredicateFunction(filter, root, builder, criteriaQuery));
    }

    protected final Predicate buildPredicateFunction(AttributeFilter filter, Root<E> root, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
        switch (filter.getCondition()) {
            case CONTAINS:
                if (isCollectionAttribute(filter.getAttribute())) {
                    return buildCollectionPredicate(filter, root, builder, criteriaQuery, false);
                } else {
                    return builder.isMember(filter.getValue(), getAttributePath(root, filter.getAttribute()));
                }
            case CONTAINS_ALL:
                if (isCollectionAttribute(filter.getAttribute())) {
                    return buildCollectionPredicate(filter, root, builder, criteriaQuery, false);
                } else {
                    List<Predicate> predicatesAll = (List<Predicate>) ((List) filter.getValue()).stream()
                            .map(o -> builder.isMember(o, getAttributePath(root, filter.getAttribute()))).collect(toList());
                    return builder.and(predicatesAll.toArray(new Predicate[] {}));
                }
            case CONTAINS_ANY:
                if (isCollectionAttribute(filter.getAttribute())) {
                    return buildCollectionPredicate(filter, root, builder, criteriaQuery, false);
                } else {
                    List<Predicate> predicatesAny = (List<Predicate>) ((List) filter.getValue()).stream()
                            .map(o -> builder.isMember(o, getAttributePath(root, filter.getAttribute()))).collect(toList());
                    return builder.or(predicatesAny.toArray(new Predicate[] {}));
                }
            case IN:
                return getAttributePath(root, filter.getAttribute()).in((Collection<?>) filter.getValue());
            case LIKE:
                return builder.like(getAttributePath(root, filter.getAttribute()),
                        filter.getValue().toString().replaceAll("\\*", "%"));
            case EQUAL:
                if (filter.getAttribute() != null && isCollectionAttribute(filter.getAttribute())) {
                    return buildCollectionPredicate(filter, root, builder, criteriaQuery, false);
                }
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
                return buildGroupedPredicate(filter, root, builder, criteriaQuery, false, false);
            case AND:
                return buildGroupedPredicate(filter, root, builder, criteriaQuery, true, false);
            case NOT:
                AttributeFilter<?> innerFilter = (AttributeFilter<?>) filter.getValue();

                // Handle NOT with AND/OR: Apply De Morgan's Law by negating and flipping the operator
                if (innerFilter.getCondition() == FilterCondition.AND || innerFilter.getCondition() == FilterCondition.OR) {
                    // NOT (A AND B) = NOT A OR NOT B, NOT (A OR B) = NOT A AND NOT B
                    boolean flipToAnd = innerFilter.getCondition() == FilterCondition.OR;
                    return buildGroupedPredicate(innerFilter, root, builder, criteriaQuery, flipToAnd, true);
                }

                if (innerFilter.getAttribute() != null && isCollectionAttribute(innerFilter.getAttribute()) &&
                        innerFilter.getCondition().isCollectionOperation()) {
                    return buildCollectionPredicate(innerFilter, root, builder, criteriaQuery, true);
                }

                if (innerFilter.getAttribute() != null && isCollectionAttribute(innerFilter.getAttribute())) {
                    return buildNegatedCollectionPredicate(innerFilter, root, builder, criteriaQuery);
                }

                return builder.not(filterPredicateFunction(root, builder, criteriaQuery).apply(innerFilter));
            default:
                return null;
        }

    }

    /**
     * Builds predicates for AND/OR, grouping CONTAINS filters on same collection into single EXISTS.
     * 
     * @param isNegated if true, negates each predicate (for NOT operations with De Morgan's Law)
     */
    private Predicate buildGroupedPredicate(AttributeFilter<?> filter, Root<E> root,
            CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, boolean isAnd, boolean isNegated) {

        List<AttributeFilter<?>> nestedFilters = (List<AttributeFilter<?>>) filter.getValue();

        java.util.Map<String, List<AttributeFilter<?>>> groups = new java.util.HashMap<>();
        List<Predicate> otherPredicates = new ArrayList<>();

        for (AttributeFilter<?> f : nestedFilters) {
            if (f.getAttribute() != null && isCollectionAttribute(f.getAttribute()) &&
                    f.getCondition().isCollectionOperation()) {
                String collection = f.getAttribute().split("\\.")[0];
                groups.computeIfAbsent(collection, k -> new ArrayList<>()).add(f);
            } else {
                Predicate pred = filterPredicateFunction(root, builder, criteriaQuery).apply(f);
                otherPredicates.add(isNegated ? builder.not(pred) : pred);
            }
        }

        List<Predicate> allPredicates = new ArrayList<>(otherPredicates);
        for (List<AttributeFilter<?>> groupFilters : groups.values()) {
            allPredicates.add(buildMultiFilterCollectionPredicate(groupFilters, root, builder, criteriaQuery, isAnd, isNegated));
        }

        return isAnd ? builder.and(allPredicates.toArray(new Predicate[0]))
                : builder.or(allPredicates.toArray(new Predicate[0]));
    }

    /**
     * Builds single EXISTS with HAVING for multiple CONTAINS filters on same collection.
     * Example: [{ comments.id: "A" }, { comments.status: "ACTIVE" }]
     * → EXISTS(... HAVING SUM(id='A')>0 AND SUM(status='ACTIVE')>0)
     * 
     * @param isNegated if true, uses = 0 instead of > 0 and applies De Morgan's Law
     */
    private Predicate buildMultiFilterCollectionPredicate(List<AttributeFilter<?>> filters, Root<E> root,
            CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, boolean combineWithAnd, boolean isNegated) {

        String collectionName = filters.get(0).getAttribute().split("\\.")[0];

        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<E> subRoot = subquery.from(entityClass);
        Join<?, ?> collectionJoin = subRoot.join(collectionName);

        List<Predicate> havingConditions = new ArrayList<>();

        for (AttributeFilter<?> filter : filters) {
            String property = filter.getAttribute().split("\\.")[1];
            List<Object> values = filter.getCondition().expectsSingleValue()
                    ? List.of(filter.getValue())
                    : (List<Object>) filter.getValue();
            boolean filterUseAnd = filter.getCondition().combineValuesWithAnd();

            List<Predicate> valuePreds = new ArrayList<>();
            for (Object value : values) {
                Expression<Integer> caseExpr = builder.sum(
                        builder.<Integer> selectCase()
                                .when(builder.equal(collectionJoin.get(property), value), 1)
                                .otherwise(0));
                // Apply negation: > 0 becomes = 0
                valuePreds.add(isNegated ? builder.equal(caseExpr, 0) : builder.greaterThan(caseExpr, 0));
            }
            // Apply De Morgan's Law via XOR for value combination
            boolean useAnd = filterUseAnd ^ isNegated;
            havingConditions.add(useAnd ? builder.and(valuePreds.toArray(new Predicate[0]))
                    : builder.or(valuePreds.toArray(new Predicate[0])));
        }

        Predicate havingPredicate = combineWithAnd
                ? builder.and(havingConditions.toArray(new Predicate[0]))
                : builder.or(havingConditions.toArray(new Predicate[0]));

        subquery.select(builder.literal(1))
                .where(builder.equal(subRoot.get("id"), root.get("id")))
                .groupBy(subRoot.get("id"))
                .having(havingPredicate);

        return builder.exists(subquery);
    }

    /**
     * Builds negated collection predicate using EXISTS with HAVING = 0.
     * Uses EXISTS with inverted HAVING clause instead of NOT EXISTS for better optimization.
     *
     * Examples:
     * - NOT contains: EXISTS(...HAVING SUM(CASE WHEN id='A' THEN 1 ELSE 0 END) = 0)
     * - NOT containsAll: EXISTS(...HAVING SUM(id='A')=0 OR SUM(id='B')=0)
     * - NOT containsAny: EXISTS(...HAVING SUM(id='A')=0 AND SUM(id='B')=0)
     */
    private Predicate buildNegatedCollectionPredicate(AttributeFilter<?> filter, Root<E> root,
            CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {

        return buildCollectionPredicate(filter, root, builder, criteriaQuery, true);
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

    /**
     * Checks if the attribute is a collection attribute (e.g., "nodes.name" starts with "nodes").
     * This helps detect when NOT operations need special subquery handling.
     */
    private boolean isCollectionAttribute(final String attribute) {
        if (attribute == null || !attribute.contains(".")) {
            return false;
        }
        // Extract the first part (e.g., "nodes" from "nodes.name")
        String firstPart = attribute.split("\\.")[0];
        return isPluralAttribute(firstPart);
    }

    private List<Predicate> getRecursivePredicate(AttributeFilter<?> filter, Root<E> root, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery) {
        return ((List<AttributeFilter<?>>) filter.getValue())
                .stream()
                .map(filterPredicateFunction(root, builder, criteriaQuery))
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
            criteriaQuery.where(filters.stream().map(filterPredicateFunction(root, builder, criteriaQuery)).toArray(Predicate[]::new));
        }
    }

    /**
     * Unified method to build collection predicates using EXISTS + HAVING clause.
     * Handles CONTAINS, CONTAINS_ALL, CONTAINS_ANY, and EQUAL operations.
     */
    private Predicate buildCollectionPredicate(AttributeFilter<?> filter, Root<E> root,
            CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, boolean isNegated) {

        // Parse attribute: "comments.id" -> collection="comments", property="id"
        String[] parts = filter.getAttribute().split("\\.");
        String collectionName = parts[0];
        String propertyPath = parts[1];

        // Get values: single value for CONTAINS/EQUAL, list for CONTAINS_ALL/CONTAINS_ANY
        List<Object> values = filter.getCondition().expectsSingleValue()
                ? List.of(filter.getValue())
                : (List<Object>) filter.getValue();

        // Determine combination logic: AND for CONTAINS/CONTAINS_ALL/EQUAL, OR for CONTAINS_ANY
        boolean combineUsingAnd = filter.getCondition().combineValuesWithAnd();

        // Create subquery
        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<E> subRoot = subquery.from(entityClass);
        Join<?, ?> collectionJoin = subRoot.join(collectionName);

        // Build CASE expressions: SUM(CASE WHEN c.id = 'A' THEN 1 ELSE 0 END)
        List<Predicate> havingConditions = new ArrayList<>();
        for (Object value : values) {
            Expression<Integer> caseExpr = builder.sum(
                    builder.<Integer> selectCase()
                            .when(builder.equal(collectionJoin.get(propertyPath), value), 1)
                            .otherwise(0));

            // Condition: > 0 for positive, = 0 for negative
            Predicate condition = isNegated
                    ? builder.equal(caseExpr, 0)
                    : builder.greaterThan(caseExpr, 0);
            havingConditions.add(condition);
        }

        // Combine with AND/OR (apply De Morgan's Law via XOR)
        boolean useAnd = combineUsingAnd ^ isNegated;
        Predicate havingPredicate = useAnd
                ? builder.and(havingConditions.toArray(new Predicate[0]))
                : builder.or(havingConditions.toArray(new Predicate[0]));

        // Build complete subquery
        subquery.select(builder.literal(1))
                .where(builder.equal(subRoot.get("id"), root.get("id")))
                .groupBy(subRoot.get("id"))
                .having(havingPredicate);

        return builder.exists(subquery);
    }
}
