/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.query;

import java.util.Arrays;
import java.util.List;

import static org.kie.kogito.index.query.FilterCondition.*;

public final class QueryFilterFactory {

    private QueryFilterFactory() {
    }

    public static AttributeSort orderBy(String attribute, SortDirection sort) {
        return new AttributeSort(attribute, sort);
    }

    public static AttributeFilter like(String attribute, String value) {
        return new AttributeFilter(attribute, LIKE, value);
    }

    public static AttributeFilter contains(String attribute, String value) {
        return new AttributeFilter(attribute, CONTAINS, value);
    }

    public static AttributeFilter in(String attribute, List values) {
        return new AttributeFilter(attribute, IN, values);
    }

    public static AttributeFilter containsAny(String attribute, List<String> values) {
        return new AttributeFilter(attribute, CONTAINS_ANY, values);
    }

    public static AttributeFilter containsAll(String attribute, List<String> values) {
        return new AttributeFilter(attribute, CONTAINS_ALL, values);
    }

    public static AttributeFilter greaterThan(String attribute, Object value) {
        return new AttributeFilter(attribute, GT, value);
    }

    public static AttributeFilter greaterThanEqual(String attribute, Object value) {
        return new AttributeFilter(attribute, GTE, value);
    }

    public static AttributeFilter lessThanEqual(String attribute, Object value) {
        return new AttributeFilter(attribute, LTE, value);
    }

    public static AttributeFilter lessThan(String attribute, Object value) {
        return new AttributeFilter(attribute, LT, value);
    }

    public static AttributeFilter equalTo(String attribute, Object value) {
        return new AttributeFilter(attribute, EQUAL, value);
    }

    public static AttributeFilter isNull(String attribute) {
        return new AttributeFilter(attribute, IS_NULL, null);
    }

    public static AttributeFilter notNull(String attribute) {
        return new AttributeFilter(attribute, NOT_NULL, null);
    }

    public static AttributeFilter between(String attribute, Object from, Object to) {
        return new AttributeFilter(attribute, BETWEEN, Arrays.asList(from, to));
    }

    public static AttributeFilter or(List<AttributeFilter> filters) {
        return new AttributeFilter(null, OR, filters);
    }

    public static AttributeFilter and(List<AttributeFilter> filters) {
        return new AttributeFilter(null, AND, filters);
    }
}
