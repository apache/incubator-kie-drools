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
package org.kie.kogito.persistence.redis;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.persistence.api.query.AttributeFilter;

import static org.kie.kogito.persistence.redis.Constants.INDEX_NAME_FIELD;

public class RedisQueryFactory {

    static String buildQueryBody(String indexName, List<AttributeFilter<?>> filters) {
        List<String> components = new ArrayList<>();
        components.add(String.format("@%s:%s", INDEX_NAME_FIELD, indexName));
        for (AttributeFilter attributeFilter : filters) {
            switch (attributeFilter.getCondition()) {
                // Indexed values have to be escaped according to https://github.com/RediSearch/RediSearch/issues/1148
                case EQUAL:
                    components.add(String.format("@%s:%s", attributeFilter.getAttribute(), Sanitizer.sanitize(attributeFilter.getValue())));
                    break;
                case LIKE:
                    if (!"".equals(attributeFilter.getValue()) && !"*".equals(attributeFilter.getValue())) {
                        components.add(String.format("@%s:%s", attributeFilter.getAttribute(), Sanitizer.sanitize(attributeFilter.getValue())));
                    }
                    break;
            }
        }
        return String.join(" ", components);
    }

    static void addFilters(io.redisearch.Query query, List<AttributeFilter<?>> filters) {
        for (AttributeFilter attributeFilter : filters) {
            switch (attributeFilter.getCondition()) {
                case EQUAL:
                case LIKE:
                    break;
                case GT:
                    query.addFilter(
                            new io.redisearch.Query.NumericFilter(
                                    attributeFilter.getAttribute(), convertNumeric(attributeFilter.getValue()), true, Double.POSITIVE_INFINITY, false));
                    break;
                case GTE:
                    query.addFilter(
                            new io.redisearch.Query.NumericFilter(
                                    attributeFilter.getAttribute(), convertNumeric(attributeFilter.getValue()), false, Double.POSITIVE_INFINITY, false));
                    break;
                case LT:
                    query.addFilter(
                            new io.redisearch.Query.NumericFilter(
                                    attributeFilter.getAttribute(), Double.NEGATIVE_INFINITY, false, convertNumeric(attributeFilter.getValue()), true));
                    break;
                case LTE:
                    query.addFilter(
                            new io.redisearch.Query.NumericFilter(
                                    attributeFilter.getAttribute(), Double.NEGATIVE_INFINITY, false, convertNumeric(attributeFilter.getValue()), false));
                    break;
                case BETWEEN:
                    List<?> value = (List<?>) attributeFilter.getValue();
                    query.addFilter(
                            new io.redisearch.Query.NumericFilter(
                                    attributeFilter.getAttribute(), convertNumeric(value.get(0)), false, convertNumeric(value.get(1)), false));
                    break;
                default:
                    throw new UnsupportedOperationException("Redis does not support query filter: " + attributeFilter.getCondition());
            }
        }
    }

    private static Double convertNumeric(Object obj) {
        if (obj instanceof Long) {
            return ((Long) obj).doubleValue();
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        }
        return (Double) obj;
    }
}
