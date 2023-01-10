/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.graphql.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.kogito.persistence.api.query.AttributeFilter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class GraphQLQueryParser implements Function<Object, List<AttributeFilter<?>>> {

    private final Map<String, Function<Object, Stream<AttributeFilter<?>>>> mapper = new HashMap<>();

    public void mapAttribute(String name, Function<Object, Stream<AttributeFilter<?>>> mapperFunction) {
        mapper.put(name, mapperFunction);
    }

    @Override
    public List<AttributeFilter<?>> apply(Object where) {
        return where == null ? emptyList()
                : ((Map<String, Object>) where).entrySet().stream()
                        .filter(entry -> mapper.containsKey(entry.getKey()) && entry.getValue() != null)
                        .flatMap(entry -> mapper.get(entry.getKey()).apply(entry.getValue()))
                        .filter(Objects::nonNull)
                        .collect(toList());
    }

    @Override
    public String toString() {
        return "GraphQLQueryParser{" +
                "mapper=" + mapper +
                '}';
    }
}
