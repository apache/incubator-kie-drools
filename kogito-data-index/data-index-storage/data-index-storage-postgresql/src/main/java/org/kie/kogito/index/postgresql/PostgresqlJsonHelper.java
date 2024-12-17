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
package org.kie.kogito.index.postgresql;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.persistence.api.query.AttributeFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static java.util.stream.Collectors.toList;

public class PostgresqlJsonHelper {

    private PostgresqlJsonHelper() {
    }

    public static Predicate buildPredicate(AttributeFilter<?> filter, Root<?> root,
            CriteriaBuilder builder) {
        boolean isString;
        List<Object> values;
        switch (filter.getCondition()) {
            case EQUAL:
                isString = filter.getValue() instanceof String;
                return builder.equal(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, filter.getValue(), isString));
            case GT:
                isString = filter.getValue() instanceof String;
                return builder.greaterThan(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, filter.getValue(), isString));
            case GTE:
                isString = filter.getValue() instanceof String;
                return builder.greaterThanOrEqualTo(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, filter.getValue(), isString));
            case LT:
                isString = filter.getValue() instanceof String;
                return builder.lessThan(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, filter.getValue(), isString));
            case LTE:
                isString = filter.getValue() instanceof String;
                return builder
                        .lessThanOrEqualTo(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, filter.getValue(), isString));
            case LIKE:
                return builder.like(buildPathExpression(builder, root, filter.getAttribute(), true),
                        filter.getValue().toString().replaceAll("\\*", "%"));
            case IS_NULL:
                return builder.isNull(buildPathExpression(builder, root, filter.getAttribute(), false));
            case NOT_NULL:
                return builder.isNotNull(buildPathExpression(builder, root, filter.getAttribute(), false));
            case BETWEEN:
                values = (List<Object>) filter.getValue();
                isString = values.get(0) instanceof String;
                return builder.between(buildPathExpression(builder, root, filter.getAttribute(), isString), buildObjectExpression(builder, values.get(0), isString),
                        buildObjectExpression(builder, values.get(1), isString));
            case IN:
                values = (List<Object>) filter.getValue();
                isString = values.get(0) instanceof String;
                return buildPathExpression(builder, root, filter.getAttribute(), isString).in(values.stream().map(o -> buildObjectExpression(builder, o, isString)).collect(Collectors.toList()));
            case CONTAINS:
                return builder.isTrue(
                        builder.function(ContainsSQLFunction.CONTAINS_NAME, Boolean.class, buildPathExpression(builder, root, filter.getAttribute(), false), builder.literal(filter.getValue())));
            case CONTAINS_ANY:
                return containsPredicate(filter, root, builder, ContainsSQLFunction.CONTAINS_ANY_NAME);
            case CONTAINS_ALL:
                return containsPredicate(filter, root, builder, ContainsSQLFunction.CONTAINS_ALL_NAME);
        }
        throw new UnsupportedOperationException("Filter " + filter + " is not supported");
    }

    private static Predicate containsPredicate(AttributeFilter<?> filter, Root<?> root, CriteriaBuilder builder, String name) {
        return builder.isTrue(
                builder.function(name, Boolean.class,
                        Stream.concat(Stream.of(buildPathExpression(builder, root, filter.getAttribute(), false)), ((List<?>) filter.getValue()).stream().map(o -> builder.literal(o)))
                                .toArray(Expression[]::new)));
    }

    private static Expression buildObjectExpression(CriteriaBuilder builder, Object value, boolean isString) {
        return isString ? builder.literal(value) : builder.function("to_jsonb", Object.class, builder.literal(value));
    }

    private static Expression buildObjectExpression(CriteriaBuilder builder, Object value) {
        return buildObjectExpression(builder, value, value instanceof String);
    }

    private static Expression buildPathExpression(CriteriaBuilder builder, Root<?> root, String attributeName, boolean isStr) {
        String[] attributes = attributeName.split("\\.");
        Expression<?>[] arguments = new Expression[attributes.length];
        arguments[0] = root.get(attributes[0]);
        for (int i = 1; i < attributes.length; i++) {
            arguments[i] = builder.literal(attributes[i]);
        }
        return isStr ? builder.function("jsonb_extract_path_text", String.class, arguments) : builder.function("jsonb_extract_path", Object.class, arguments);
    }
}
