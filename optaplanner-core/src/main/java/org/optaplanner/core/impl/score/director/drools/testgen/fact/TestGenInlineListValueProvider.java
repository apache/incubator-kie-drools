/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class TestGenInlineListValueProvider extends TestGenAbstractValueProvider<List<?>> {

    private final Map<Object, TestGenFact> existingInstances;
    private final List<TestGenFact> requiredFacts;

    public TestGenInlineListValueProvider(List<?> value, Map<Object, TestGenFact> existingInstances) {
        super(value);
        this.existingInstances = existingInstances;
        requiredFacts = value.stream()
                .map(i -> existingInstances.get(i))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestGenFact> getRequiredFacts() {
        return requiredFacts;
    }

    @Override
    public List<Class<?>> getImports() {
        return Collections.singletonList(Arrays.class);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(25 * value.size() + 17);
        sb.append("Arrays.asList(");
        Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            sb.append(existingInstances.get(it.next()));
        }
        while (it.hasNext()) {
            sb.append(", ").append(existingInstances.get(it.next()));
        }
        sb.append(")");
        return sb.toString();
    }

}
