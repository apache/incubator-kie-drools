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
package org.drools.ruleunits.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.core.util.StringUtils.ucFirst;
import static org.drools.wiring.api.util.ClassUtils.convertFromPrimitiveType;
import static org.drools.wiring.api.util.ClassUtils.getter2property;

public final class ReflectiveRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Class<?> type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;
    private final Method getterMethod;

    public ReflectiveRuleUnitVariable(String name, Method getterMethod) {
        Objects.requireNonNull(name, "Invalid name was given: null");

        if (getterMethod.getParameterCount() != 0) {
            throw new IllegalArgumentException(
                    String.format("The given method '%s' is not from a RuleUnit instance", getterMethod));
        }

        if (getterMethod.getName().equals("getClass")) {
            throw new IllegalArgumentException("'getClass' is not a valid method for a rule unit variable");
        }

        String id = getter2property(getterMethod.getName());

        if (id == null) {
            throw new IllegalArgumentException(
                    String.format("Could not parse getter name for method '%s'", getterMethod));
        }

        this.name = name;
        this.getter = getterMethod.getName();
        this.setter = "set" + ucFirst(name);
        this.getterMethod = getterMethod;
        this.type = getterMethod.getReturnType();
        this.dataSourceParameterType = getUnitVarType(getterMethod);
        this.boxedVarType = convertFromPrimitiveType(type);
    }

    private Class<?> getUnitVarType(Method m) {
        Class<?> returnClass = m.getReturnType();
        if (returnClass.isArray()) {
            return returnClass.getComponentType();
        } else if (Iterable.class.isAssignableFrom(returnClass)) {
            Type returnType = m.getGenericReturnType();
            Class<?> sourceType = returnType instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0] : Object.class;
            return sourceType;
        } else {
            return returnClass;
        }
    }

    @Override
    public boolean isDataSource() {
        return dataSourceParameterType != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getter() {
        return getter;
    }

    public String setter() {
        return setter;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Class<?> getDataSourceParameterType() {
        return dataSourceParameterType;
    }

    @Override
    public Class<?> getBoxedVarType() {
        return boxedVarType;
    }

    @Override
    public String toString() {
        return "ReflectiveRuleUnitVariable{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", dataSourceParameterType=" + dataSourceParameterType +
                '}';
    }
}