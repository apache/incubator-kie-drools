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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Inline value is a value of a shadow variable. From planning perspective, the value is computed from other variable's
 * value. From TestGen perspective, the value is not part of the working facts that are recorded during initial
 * KIE session insertion. It is an anonymous constant created just for the single KIE session update.
 */
public class TestGenInlineValue implements TestGenFact {

    private final Object instance;
    private final TestGenValueProvider<?> valueProvider;

    public TestGenInlineValue(Object value, Map<Object, TestGenFact> existingInstances) {
        if (value == null) {
            throw new IllegalStateException("Value may not be null.");
        }
        if (List.class.isAssignableFrom(value.getClass())) {
            List<?> copy = new ArrayList<>((List<?>) value);
            instance = Collections.unmodifiableList(copy);
            valueProvider = new TestGenInlineListValueProvider(copy, existingInstances);
        } else {
            instance = value;
            valueProvider = new TestGenAbstractValueProvider<Object>(value) {
                @Override
                public String toString() {
                    return value.toString();
                }
            };
        }
    }

    @Override
    public void setUp(Map<Object, TestGenFact> existingInstances) {
        // no setup needed
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public List<TestGenFactField> getFields() {
        return Collections.emptyList();
    }

    @Override
    public List<TestGenFact> getDependencies() {
        return valueProvider.getRequiredFacts();
    }

    @Override
    public List<Class<?>> getImports() {
        return valueProvider.getImports();
    }

    @Override
    public void reset() {
        // no reset needed
    }

    @Override
    public void printInitialization(StringBuilder sb) {
        // no initialization
    }

    @Override
    public void printSetup(StringBuilder sb) {
        // no setup
    }

    @Override
    public String toString() {
        return valueProvider.toString();
    }

}
