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
package org.kie.kogito.index.graphql.query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.SortDirection;

import graphql.language.Argument;
import graphql.language.EnumValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.schema.DataFetchingEnvironment;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;

public class GraphQLQueryOrderByParser implements Function<DataFetchingEnvironment, List<AttributeSort>> {

    @Override
    public List<AttributeSort> apply(DataFetchingEnvironment env) {
        if (env == null) {
            return emptyList();
        }
        Optional<Argument> sortByArgument = env.getMergedField().getArguments().stream().filter(a -> "orderBy".equals(a.getName())).findFirst();
        if (sortByArgument.isPresent()) {
            Value value = sortByArgument.get().getValue();
            if (value instanceof ObjectValue) {
                return ((ObjectValue) value).getObjectFields().stream().flatMap(mapSortBy(env)).collect(toList());
            }
            if (value instanceof VariableReference) {
                VariableReference variable = (VariableReference) value;
                Map<String, Object> sort = (Map<String, Object>) env.getVariables().get(variable.getName());
                return sort.entrySet().stream().flatMap(mapSortBy()).collect(toList());
            }
        }
        return emptyList();
    }

    private Function<Map.Entry<String, Object>, Stream<AttributeSort>> mapSortBy() {
        return entry -> {
            if (entry.getValue() instanceof Map) {
                return ((Map<String, Object>) entry.getValue()).entrySet().stream().flatMap(mapSortBy()).map(sort -> {
                    sort.setAttribute(entry.getKey() + "." + sort.getAttribute());
                    return sort;
                });
            } else {
                return Stream.of(orderBy(entry.getKey(), SortDirection.valueOf(entry.getValue().toString())));
            }
        };
    }

    private Function<ObjectField, Stream<AttributeSort>> mapSortBy(DataFetchingEnvironment env) {
        return field -> {
            if (field.getValue() instanceof EnumValue) {
                return Stream.of(orderBy(field.getName(), SortDirection.valueOf(((EnumValue) field.getValue()).getName())));
            } else if (field.getValue() instanceof ObjectValue) {
                ObjectValue objectValue = (ObjectValue) field.getValue();
                return objectValue.getObjectFields().stream().flatMap(mapSortBy(env)).map(f -> {
                    f.setAttribute(field.getName() + "." + f.getAttribute());
                    return f;
                });
            } else if (field.getValue() instanceof VariableReference) {
                VariableReference variable = (VariableReference) field.getValue();
                Object sort = env.getVariables().get(variable.getName());
                return Stream.of(orderBy(field.getName(), SortDirection.valueOf(sort.toString())));
            } else {
                return null;
            }
        };
    }
}
