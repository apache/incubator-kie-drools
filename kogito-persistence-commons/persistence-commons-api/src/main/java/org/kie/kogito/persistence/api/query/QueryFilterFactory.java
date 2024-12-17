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
package org.kie.kogito.persistence.api.query;

import java.util.Arrays;
import java.util.List;

public final class QueryFilterFactory {

    private QueryFilterFactory() {
    }

    public static AttributeSort orderBy(String attribute, SortDirection sort) {
        return new AttributeSort(attribute, sort);
    }

    public static AttributeFilter<String> like(String attribute, String value) {
        return new AttributeFilter<>(attribute, FilterCondition.LIKE, value);
    }

    public static <T> AttributeFilter<T> contains(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.CONTAINS, value);
    }

    public static <T> AttributeFilter<List<T>> in(String attribute, List<T> values) {
        return new AttributeFilter<>(attribute, FilterCondition.IN, values);
    }

    public static <T> AttributeFilter<List<T>> containsAny(String attribute, List<T> values) {
        return new AttributeFilter<>(attribute, FilterCondition.CONTAINS_ANY, values);
    }

    public static <T> AttributeFilter<List<T>> containsAll(String attribute, List<T> values) {
        return new AttributeFilter<>(attribute, FilterCondition.CONTAINS_ALL, values);
    }

    public static <T> AttributeFilter<T> greaterThan(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.GT, value);
    }

    public static <T> AttributeFilter<T> greaterThanEqual(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.GTE, value);
    }

    public static <T> AttributeFilter<T> lessThanEqual(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.LTE, value);
    }

    public static <T> AttributeFilter<T> lessThan(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.LT, value);
    }

    public static <T> AttributeFilter<T> equalTo(String attribute, T value) {
        return new AttributeFilter<>(attribute, FilterCondition.EQUAL, value);
    }

    public static AttributeFilter<Object> isNull(String attribute) {
        return new AttributeFilter<>(attribute, FilterCondition.IS_NULL, null);
    }

    public static AttributeFilter<Object> notNull(String attribute) {
        return new AttributeFilter<>(attribute, FilterCondition.NOT_NULL, null);
    }

    public static <T> AttributeFilter<List<T>> between(String attribute, T from, T to) {
        return new AttributeFilter<>(attribute, FilterCondition.BETWEEN, Arrays.asList(from, to));
    }

    public static AttributeFilter<List<AttributeFilter>> or(List<AttributeFilter<?>> filters) {
        return new AttributeFilter(null, FilterCondition.OR, filters);
    }

    public static AttributeFilter<List<AttributeFilter>> and(List<AttributeFilter<?>> filters) {
        return new AttributeFilter(null, FilterCondition.AND, filters);
    }

    public static <T> AttributeFilter<AttributeFilter<T>> not(AttributeFilter<T> filter) {
        return new AttributeFilter<>(null, FilterCondition.NOT, filter);
    }
}
