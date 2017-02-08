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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;

public class TestGenFactField implements Comparable<TestGenFactField> {

    private final TestGenValueFact fact;
    private final String propertyName;
    private final Method setter;
    private final TestGenValueProvider<?> valueProvider;
    private boolean active = true;

    TestGenFactField(TestGenValueFact fact, String propertyName, TestGenValueProvider<?> valueProvider) {
        this.fact = fact;
        this.propertyName = propertyName;
        this.valueProvider = valueProvider;
        setter = ReflectionHelper.getSetterMethod(fact.getInstance().getClass(), propertyName);
        if (setter == null) {
            throw new IllegalStateException("Setter for '" + fact.getInstance().getClass().getSimpleName() + "."
                    + propertyName + "' not found!");
        }
    }

    void reset() {
        Object value = active ? valueProvider.get() : valueProvider.getUninitialized();
        try {
            setter.invoke(fact.getInstance(), value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to reset " + fact.getInstance().getClass().getSimpleName() + "."
                    + propertyName, ex);
        }
    }

    public List<Class<?>> getImports() {
        return valueProvider.getImports();
    }

    public List<TestGenFact> getRequiredFacts() {
        return valueProvider.getRequiredFacts();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    void print(StringBuilder sb) {
        if (active) {
            valueProvider.printSetup(sb);
            // null original value means the field is uninitialized so there's no need to .set(null);
            if (valueProvider.get() != null) {
                sb.append(String.format("        %s.%s(%s);\n",
                        fact.getVariableName(), setter.getName(), valueProvider.toString()));
            }
        }
    }

    @Override
    public int compareTo(TestGenFactField o) {
        return propertyName.compareTo(o.propertyName);
    }

}
