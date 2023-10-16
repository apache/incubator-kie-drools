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
package org.kie.kogito.persistence.mongodb.query;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.bson.conversions.Bson;
import org.kie.kogito.persistence.api.query.AttributeFilter;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;
import static java.util.stream.Collectors.toList;

public class QueryUtils {

    private QueryUtils() {
    }

    static Optional<Bson> generateQuery(List<AttributeFilter<?>> filters, UnaryOperator<String> filterFunction) {
        return Optional.ofNullable(filters).filter(f -> !f.isEmpty()).map(fs -> and(fs.stream().map(f -> generateSingleQuery(f, filterFunction)).collect(toList())));
    }

    static Bson generateSingleQuery(AttributeFilter<?> filter, UnaryOperator<String> filterFunction) {
        switch (filter.getCondition()) {
            case CONTAINS:
            case EQUAL:
                return eq(filterFunction.apply(filter.getAttribute()), filter.getValue());
            case LIKE:
                return regex(filterFunction.apply(filter.getAttribute()), ((String) filter.getValue()).replace("*", ".*"));
            case IS_NULL:
                return exists(filterFunction.apply(filter.getAttribute()), false);
            case NOT_NULL:
                return exists(filterFunction.apply(filter.getAttribute()), true);
            case GT:
                return gt(filterFunction.apply(filter.getAttribute()), filter.getValue());
            case GTE:
                return gte(filterFunction.apply(filter.getAttribute()), filter.getValue());
            case LT:
                return lt(filterFunction.apply(filter.getAttribute()), filter.getValue());
            case LTE:
                return lte(filterFunction.apply(filter.getAttribute()), filter.getValue());
            case BETWEEN:
                List<?> value = (List<?>) filter.getValue();
                return and(gte(filterFunction.apply(filter.getAttribute()), value.get(0)),
                        lte(filterFunction.apply(filter.getAttribute()), value.get(1)));
            case IN:
                return in(filterFunction.apply(filter.getAttribute()), (List<?>) filter.getValue());
            case CONTAINS_ALL:
                return all(filterFunction.apply(filter.getAttribute()), (List<?>) filter.getValue());
            case CONTAINS_ANY:
                return or(((List<?>) filter.getValue()).stream().map(v -> eq(filterFunction.apply(filter.getAttribute()), v)).collect(toList()));
            case OR:
                return or(((List<AttributeFilter<?>>) filter.getValue()).stream().map(f -> generateSingleQuery(f, filterFunction)).collect(toList()));
            case AND:
                return and(((List<AttributeFilter<?>>) filter.getValue()).stream().map(f -> generateSingleQuery(f, filterFunction)).collect(toList()));
            case NOT:
                return not(generateSingleQuery((AttributeFilter<?>) filter.getValue(), filterFunction));
            default:
                return null;
        }
    }
}
