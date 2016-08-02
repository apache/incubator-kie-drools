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

class TestGenMapValueProvider extends TestGenAbstractValueProvider {

    private final String identifier;
    private final Type[] typeArguments;
    private final Map<Object, TestGenFact> existingInstances;
    private final List<Class<?>> imports = new ArrayList<Class<?>>();

    public TestGenMapValueProvider(Object value, String identifier,
            Type[] typeArguments, Map<Object, TestGenFact> existingInstances) {
        super(value);
        this.identifier = identifier;
        this.typeArguments = typeArguments;
        this.existingInstances = existingInstances;
        imports.add(HashMap.class);
        imports.add((Class<?>) typeArguments[0]);
        imports.add((Class<?>) typeArguments[1]);
        for (Map.Entry<? extends Object, ? extends Object> entry : ((java.util.Map<?, ?>) value).entrySet()) {
            imports.add(entry.getKey().getClass());
            imports.add(entry.getValue().getClass());
        }
    }

    public List<TestGenFact> getFacts() {
        ArrayList<TestGenFact> facts = new ArrayList<TestGenFact>();
        for (Map.Entry<? extends Object, ? extends Object> entry : ((java.util.Map<?, ?>) value).entrySet()) {
            addFact(facts, entry.getKey());
            addFact(facts, entry.getValue());
        }
        return facts;
    }

    public List<Class<?>> getImports() {
        return imports;
    }

    private void addFact(List<TestGenFact> facts, Object o) {
        TestGenFact fact = existingInstances.get(o);
        if (fact != null) {
            facts.add(fact);
        }
    }

    @Override
    public void printSetup(StringBuilder sb) {
        String k = ((Class<?>) typeArguments[0]).getSimpleName();
        String v = ((Class<?>) typeArguments[1]).getSimpleName();
        sb.append(String.format("        HashMap<%s, %s> %s = new HashMap<%s, %s>();%n", k, v, identifier, k, v));
        for (Map.Entry<? extends Object, ? extends Object> entry : ((java.util.Map<?, ?>) value).entrySet()) {
            sb.append(String.format("        //%s => %s%n", entry.getKey(), entry.getValue()));
            sb.append(String.format("        %s.put(%s, %s);%n", identifier, existingInstances.get(entry.getKey()), existingInstances.get(entry.getValue())));
        }
    }

    @Override
    public String toString() {
        return identifier;
    }

}
