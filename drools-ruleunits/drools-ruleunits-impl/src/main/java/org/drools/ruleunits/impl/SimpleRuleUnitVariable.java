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

import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.core.util.StringUtils.ucFirst;
import static org.drools.wiring.api.util.ClassUtils.convertFromPrimitiveType;

public final class SimpleRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Class<?> type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;

    public SimpleRuleUnitVariable(String name, Class<?> type, Class<?> dataSourceParameterType, boolean writable) {
        this.name = name;
        this.getter = "get" + ucFirst(name);
        this.setter = writable ? "set" + ucFirst(name) : null;
        this.type = type;
        this.dataSourceParameterType = dataSourceParameterType;
        this.boxedVarType = convertFromPrimitiveType(type);
    }

    public SimpleRuleUnitVariable(String name, Class<?> type) {
        this(name, type, null, true);
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

    @Override
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
}