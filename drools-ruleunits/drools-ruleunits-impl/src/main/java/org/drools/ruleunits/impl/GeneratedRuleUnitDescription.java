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

import java.util.function.Function;

import org.drools.ruleunits.api.conf.RuleUnitConfig;
import org.drools.util.TypeResolver;
import org.kie.internal.ruleunit.RuleUnitVariable;

public class GeneratedRuleUnitDescription extends AbstractRuleUnitDescription {

    private final Function<String, Class<?>> typeResolver;
    private final String name;
    private final String packageName;
    private final String simpleName;
    private final String canonicalName;

    public GeneratedRuleUnitDescription(String name, Function<String, Class<?>> typeResolver) {
        this.typeResolver = typeResolver;
        this.name = name;
        int width = name.lastIndexOf('.');
        if (width > -1) {
            this.simpleName = name.substring(width + 1);
            this.packageName = name.substring(0, width);
            this.canonicalName = packageName + '.' + simpleName;
        } else {
            this.simpleName = name;
            this.packageName = "";
            this.canonicalName = simpleName;
        }
        setConfig(RuleUnitConfig.DEFAULT);
    }

    public GeneratedRuleUnitDescription(String name, TypeResolver typeResolver) {
        this(name, fqcn -> uncheckedLoadClass(typeResolver, fqcn));
    }

    public GeneratedRuleUnitDescription(String name, ClassLoader contextClassLoader) {
        this(name, fqcn -> uncheckedLoadClass(contextClassLoader, fqcn));
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getRuleUnitName() {
        return name;
    }

    @Override
    public RuleUnitVariable getVar(String name) {
        try {
            return super.getVar(name);
        } catch (UndefinedRuleUnitVariableException e) {
            throw new UndefinedGeneratedRuleUnitVariableException(e.getVariable(), e.getUnit());
        }
    }

    public void putSimpleVar(String name, String varTypeFQCN) {
        Class<?> varType = typeResolver.apply(varTypeFQCN);
        putSimpleVar(name, varType);
    }

    public void putDatasourceVar(String name, String datasourceTypeFQCN, String datasourceParameterTypeFQCN) {
        putDatasourceVar(
                name,
                typeResolver.apply(datasourceTypeFQCN),
                typeResolver.apply(datasourceParameterTypeFQCN));
    }

    public void putSimpleVar(String name, Class<?> varType) {
        putRuleUnitVariable(new SimpleRuleUnitVariable(name, varType));
    }

    public void putDatasourceVar(String name, Class<?> datasourceType, Class<?> datasourceParameterType) {
        putRuleUnitVariable(new SimpleRuleUnitVariable(name, datasourceType, datasourceParameterType, true));
    }

    private static Class<?> uncheckedLoadClass(TypeResolver typeResolver, String fqcn) {
        try {
            return typeResolver.resolveType(fqcn);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Class<?> uncheckedLoadClass(ClassLoader classLoader, String fqcn) {
        try {
            return classLoader.loadClass(fqcn);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
