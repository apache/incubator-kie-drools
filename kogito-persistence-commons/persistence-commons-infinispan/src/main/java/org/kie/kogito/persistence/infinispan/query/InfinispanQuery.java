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
package org.kie.kogito.persistence.infinispan.query;

import java.util.List;
import java.util.function.Function;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class InfinispanQuery<T> implements Query<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanQuery.class);
    private static final String AND = " and ";
    private static final String OR = " or ";
    private static final String ATTRIBUTE_VALUE = "o.%s = %s";

    private QueryFactory qf;
    private Integer limit;
    private Integer offset;
    private List<AttributeFilter<?>> filters;
    private List<AttributeSort> sortBy;
    private String rootType;

    public InfinispanQuery(RemoteCache<?, T> delegate, String rootType) {
        this(Search.getQueryFactory(delegate), rootType);
    }

    protected InfinispanQuery(QueryFactory qf, String rootType) {
        this.qf = qf;
        this.rootType = rootType;
    }

    private static Function<Object, Object> getValueForQueryString() {
        return value -> value instanceof String ? "'" + value + "'" : value.toString();
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
        StringBuilder queryString = new StringBuilder("FROM " + rootType + " o");
        if (filters != null && !filters.isEmpty()) {
            queryString.append(" WHERE ");
            queryString.append(filters.stream().map(filterStringFunction()).collect(joining(AND)));
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            queryString.append(" ORDER BY ");
            queryString.append(sortBy.stream().map(f -> "o." + f.getAttribute() + " " + f.getSort().name()).collect(joining(", ")));
        }
        LOGGER.debug("Executing Infinispan query: {}", queryString);
        org.infinispan.query.dsl.Query<T> query = qf.create(queryString.toString());
        if (limit != null) {
            query.maxResults(limit);
        }
        if (offset != null) {
            query.startOffset(offset);
        }
        return query.execute().list();
    }

    private Function<AttributeFilter<?>, String> filterStringFunction() {
        return filter -> {
            switch (filter.getCondition()) {
                case CONTAINS:
                    return format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case CONTAINS_ALL:
                    return (String) ((List) filter.getValue()).stream().map(o -> format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(o))).collect(joining(AND));
                case CONTAINS_ANY:
                    return (String) ((List) filter.getValue()).stream().map(o -> format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(o))).collect(joining(OR));
                case LIKE:
                    return format("o.%s like %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue())).replaceAll("\\*", "%");
                case EQUAL:
                    return format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case IN:
                    return format("o.%s in (%s)", filter.getAttribute(), ((List) filter.getValue()).stream().map(getValueForQueryString()).collect(joining(", ")));
                case IS_NULL:
                    return format("o.%s is null", filter.getAttribute());
                case NOT_NULL:
                    return format("o.%s is not null", filter.getAttribute());
                case BETWEEN:
                    List<Object> value = (List<Object>) filter.getValue();
                    return format("o.%s between %s and %s", filter.getAttribute(), getValueForQueryString().apply(value.get(0)), getValueForQueryString().apply(value.get(1)));
                case GT:
                    return format("o.%s > %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case GTE:
                    return format("o.%s >= %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case LT:
                    return format("o.%s < %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case LTE:
                    return format("o.%s <= %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                case OR:
                    return getRecursiveString(filter, OR);
                case AND:
                    return getRecursiveString(filter, AND);
                case NOT:
                    return format("not %s", filterStringFunction().apply((AttributeFilter<?>) filter.getValue()));
                default:
                    return null;
            }
        };
    }

    private String getRecursiveString(AttributeFilter<?> filter, String joining) {
        return ((List<AttributeFilter<?>>) filter.getValue())
                .stream()
                .map(filterStringFunction())
                .collect(joining(joining, "(", ")"));
    }
}
