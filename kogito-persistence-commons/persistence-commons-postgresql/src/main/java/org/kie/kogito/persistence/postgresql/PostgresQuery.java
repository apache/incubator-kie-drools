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
package org.kie.kogito.persistence.postgresql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class PostgresQuery<T> implements Query<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresQuery.class);
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String ATTRIBUTE_ACCESSOR = "(json_value->>'%s')";

    private final String name;
    private final CacheEntityRepository repository;
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    private Integer limit;
    private Integer offset;
    private List<AttributeFilter<?>> filters;
    private List<AttributeSort> sortBy;
    private Map<String, JsonField> fields;

    private static final class JsonField {

        String name;
        Object value;

        JsonField(String name) {
            this(name, null);
        }

        JsonField(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public PostgresQuery(String name, CacheEntityRepository repository, ObjectMapper objectMapper, Class<T> type) {
        this.name = name;
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.type = type;
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
        //Get type information from filters/sorting to cast JSON document fields in query
        fields = addFilters(new HashMap<>(), filters);
        if (sortBy != null && !sortBy.isEmpty()) {
            sortBy.stream().filter(sortBy -> !fields.containsKey(sortBy.getAttribute()))
                    .forEach(sortBy -> fields.put(sortBy.getAttribute(),
                            new JsonField(sortBy.getAttribute())));
        }

        // Build the query to retrieve the filtered data from the temporary table above.
        StringBuilder queryString = new StringBuilder("SELECT * FROM kogito_data_cache")
                .append(" WHERE name = '")
                .append(name)
                .append("'");
        if (filters != null && !filters.isEmpty()) {
            queryString.append(" AND ");
            queryString.append(filters.stream()
                    .map(filter -> new StringBuilder()
                            .append(filterStringFunction(filter)))
                    .collect(joining(AND)));
        }

        // Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            queryString.append(" ORDER BY ");
            queryString.append(sortBy.stream().map(f -> {
                final JsonField field = fields.get(f.getAttribute());
                return cast(field, format(ATTRIBUTE_ACCESSOR, f.getAttribute())).append(" ").append(f.getSort().name());
            }).collect(joining(", ")));
        }

        LOGGER.debug("Executing PostgreSQL query: {}", queryString);
        javax.persistence.Query query = repository.getEntityManager().createNativeQuery(queryString.toString());
        query.unwrap(org.hibernate.query.NativeQuery.class).addScalar("json_value", JsonNodeBinaryType.INSTANCE);

        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }

        List<?> results = query.getResultList();

        return results.stream().map(r -> {
            if (r == null) {
                return null;
            }
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(r), type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Map<String, JsonField> addFilters(final Map<String, JsonField> fields,
            final List<AttributeFilter<?>> filters) {
        if (Objects.isNull(filters) || filters.isEmpty()) {
            return fields;
        }

        filters.stream()
                .filter(filter -> Objects.nonNull(filter.getAttribute()))
                .filter(filter -> !Objects.equals(filter.getCondition(), FilterCondition.NOT))
                .filter(filter -> !Objects.equals(filter.getCondition(), FilterCondition.AND))
                .filter(filter -> !Objects.equals(filter.getCondition(), FilterCondition.OR))
                .filter(filter -> !Objects.equals(filter.getCondition(), FilterCondition.BETWEEN))
                .filter(filter -> !fields.containsKey(filter.getAttribute()))
                .forEach(filter -> fields.put(filter.getAttribute(),
                        new JsonField(filter.getAttribute(), filter.getValue())));

        //Add Children of NOT conditions
        addFilters(fields,
                filters.stream()
                        .filter(filter -> Objects.equals(filter.getCondition(), FilterCondition.NOT))
                        .map(filter -> (AttributeFilter<?>) filter.getValue())
                        .collect(Collectors.toList()));

        //Add Children of AND conditions
        addFilters(fields,
                filters.stream()
                        .filter(filter -> Objects.equals(filter.getCondition(), FilterCondition.AND))
                        .map(filter -> (List<AttributeFilter<?>>) filter.getValue())
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));

        //Add Children of OR conditions
        addFilters(fields,
                filters.stream()
                        .filter(filter -> Objects.equals(filter.getCondition(), FilterCondition.OR))
                        .map(filter -> (List<AttributeFilter<?>>) filter.getValue())
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));

        //Add Children of BETWEEN conditions
        filters.stream()
                .filter(filter -> Objects.equals(filter.getCondition(), FilterCondition.BETWEEN))
                .filter(filter -> !fields.containsKey(filter.getAttribute()))
                .forEach(filter -> fields.put(filter.getAttribute(),
                        new JsonField(filter.getAttribute(),
                                ((List<Object>) filter.getValue()).get(0))));

        return fields;
    }

    @SuppressWarnings("unchecked")
    private String filterStringFunction(AttributeFilter<?> filter) {
        JsonField field = fields.get(filter.getAttribute());
        switch (filter.getCondition()) {
            case CONTAINS:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("= %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case CONTAINS_ALL:
                return (String) ((List) filter.getValue())
                        .stream()
                        .map(o -> cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                                .append(format("= %s", getValueForQueryString(o))))
                        .collect(joining(AND));
            case CONTAINS_ANY:
                return (String) ((List) filter.getValue())
                        .stream()
                        .map(o -> cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                                .append(format("= %s", getValueForQueryString(o))))
                        .collect(joining(OR));
            case LIKE:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("LIKE %s", getValueForQueryString(filter.getValue())))
                        .toString()
                        .replaceAll("\\*", "%");
            case EQUAL:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("= %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case IN:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("IN (%s)", ((List) filter.getValue()).stream().map(PostgresQuery::getValueForQueryString).collect(joining(", "))))
                        .toString();
            case IS_NULL:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append("IS NULL")
                        .toString();
            case NOT_NULL:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append("IS NOT NULL")
                        .toString();
            case BETWEEN:
                List<Object> value = (List<Object>) filter.getValue();
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("BETWEEN %s AND %s", getValueForQueryString(value.get(0)), getValueForQueryString(value.get(1))))
                        .toString();
            case GT:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("> %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case GTE:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format(">= %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case LT:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("< %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case LTE:
                return cast(field, format(ATTRIBUTE_ACCESSOR, filter.getAttribute()))
                        .append(format("<= %s", getValueForQueryString(filter.getValue())))
                        .toString();
            case OR:
                return getRecursiveString(filter, OR);
            case AND:
                return getRecursiveString(filter, AND);
            case NOT:
                return format("not %s", filterStringFunction((AttributeFilter<?>) filter.getValue()));
            default:
                return null;
        }
    }

    // Text values extracted from the JSON structure may need casting into primitive types
    private static StringBuilder cast(JsonField field, String accessor) {
        StringBuilder cast = new StringBuilder();
        Object value = field.value;
        if (value instanceof Number) {
            cast.append("(").append(accessor).append(")\\:\\:numeric ");
        } else {
            cast.append(accessor).append(" ");
        }
        return cast;
    }

    private static String getValueForQueryString(Object value) {
        return value instanceof String ? "'" + value + "'" : value.toString();
    }

    @SuppressWarnings("unchecked")
    private String getRecursiveString(AttributeFilter<?> filter, String joining) {
        return ((List<AttributeFilter<?>>) filter.getValue())
                .stream()
                .map(this::filterStringFunction)
                .collect(joining(joining, "(", ")"));
    }
}
