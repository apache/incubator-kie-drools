/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.director.drools.testgen.fact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TestGenMapValueProvider extends TestGenAbstractValueProvider<Map<?, ?>> {

    private final String identifier;
    private final Type[] typeArguments;
    private final Map<Object, TestGenFact> existingInstances;
    private final List<Class<?>> imports = new ArrayList<>();
    private final List<TestGenFact> requiredFacts;

    public TestGenMapValueProvider(Map<?, ?> value, String identifier, Type[] typeArguments,
            Map<Object, TestGenFact> existingInstances) {
        super(value);
        this.identifier = identifier;
        this.typeArguments = typeArguments;
        this.existingInstances = existingInstances;
        imports.add(HashMap.class);
        imports.add((Class<?>) typeArguments[0]);
        imports.add((Class<?>) typeArguments[1]);
        requiredFacts = value.entrySet().stream()
                .flatMap(entry -> Stream.of(
                        existingInstances.get(entry.getKey()),
                        existingInstances.get(entry.getValue())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestGenFact> getRequiredFacts() {
        return requiredFacts;
    }

    @Override
    public List<Class<?>> getImports() {
        return imports;
    }

    @Override
    public void printSetup(StringBuilder sb) {
        String k = ((Class<?>) typeArguments[0]).getSimpleName();
        String v = ((Class<?>) typeArguments[1]).getSimpleName();
        sb.append(String.format("        HashMap<%s, %s> %s = new HashMap<>();\n", k, v, identifier));
        for (Map.Entry<? extends Object, ? extends Object> entry : value.entrySet()) {
            sb.append(String.format("        //%s => %s\n", entry.getKey(), entry.getValue()));
            sb.append(String.format("        %s.put(%s, %s);\n",
                    identifier,
                    existingInstances.get(entry.getKey()),
                    existingInstances.get(entry.getValue())));
        }
    }

    @Override
    public String toString() {
        return identifier;
    }

}
