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

import java.lang.reflect.Method;
import java.util.List;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.BeanPropertyMemberAccessor;

public class TestGenFactField implements Comparable<TestGenFactField> {

    private final TestGenValueFact fact;
    private final BeanPropertyMemberAccessor accessor;
    private final TestGenValueProvider<?> valueProvider;
    private boolean active = true;

    TestGenFactField(TestGenValueFact fact, BeanPropertyMemberAccessor accessor, TestGenValueProvider<?> valueProvider) {
        this.fact = fact;
        this.accessor = accessor;
        this.valueProvider = valueProvider;
    }

    void reset() {
        Object value = active ? valueProvider.get() : valueProvider.getUninitialized();
        accessor.executeSetter(fact.getInstance(), value);
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
            Method setter = ReflectionHelper.getSetterMethod(fact.getInstance().getClass(), accessor.getName());
            if (setter == null) {
                throw new IllegalStateException("Setter for '" + fact.getInstance().getClass().getSimpleName() + "."
                        + accessor.getName() + "' not found!");
            }
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
        return accessor.getName().compareTo(o.accessor.getName());
    }

}
