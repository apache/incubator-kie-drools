/**
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
package org.drools.ruleunits.impl;

import java.lang.reflect.Type;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.util.ClassUtils.rawType;
import static org.drools.util.StringUtils.ucFirst;
import static org.drools.wiring.api.util.ClassUtils.convertFromPrimitiveType;

public final class SimpleRuleUnitVariable implements RuleUnitVariable {

    private final String name;
    private final Type type;
    private final Class<?> dataSourceParameterType;
    private final Class<?> boxedVarType;
    private final String getter;
    private final String setter;

    public SimpleRuleUnitVariable(String name, Type type, Class<?> dataSourceParameterType, boolean writable) {
        this(name, type, dataSourceParameterType, writable ? "set" + ucFirst(name) : null);
    }

    public SimpleRuleUnitVariable(String name, Type type, Class<?> dataSourceParameterType, String setter) {
        this.name = name;
        this.getter = "get" + ucFirst(name);
        this.setter = setter;
        this.type = type;
        this.dataSourceParameterType = dataSourceParameterType;

        Class<?> varType = type instanceof Class ? convertFromPrimitiveType((Class)type) : rawType(type);
        try {
            this.boxedVarType = varType.getClassLoader() == null || varType.getClassLoader() == DataSource.class.getClassLoader() ?
                    varType :
                    DataSource.class.getClassLoader().loadClass(varType.getCanonicalName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleRuleUnitVariable(String name, Class<?> type) {
        this(name, type, null, true);
    }

    @Override
    public boolean isDataSource() {
        return DataSource.class.isAssignableFrom(boxedVarType) && dataSourceParameterType != null;
    }

    @Override
    public boolean isDataStore() {
        return DataStore.class.isAssignableFrom(boxedVarType) && dataSourceParameterType != null;
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
    public Type getType() {
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