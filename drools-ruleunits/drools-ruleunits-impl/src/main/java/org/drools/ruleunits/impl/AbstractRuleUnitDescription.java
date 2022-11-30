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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.ruleunits.api.conf.RuleUnitConfig;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

public abstract class AbstractRuleUnitDescription implements RuleUnitDescription {

    private final Map<String, RuleUnitVariable> varDeclarations = new HashMap<>();
    private RuleUnitConfig config;

    @Override
    public Optional<Class<?>> getDatasourceType(String name) {
        return Optional.ofNullable(varDeclarations.get(name))
                .filter(RuleUnitVariable::isDataSource)
                .map(RuleUnitVariable::getDataSourceParameterType);
    }

    @Override
    public Optional<Type> getVarType(String name) {
        return Optional.ofNullable(varDeclarations.get(name)).map(RuleUnitVariable::getType);
    }

    @Override
    public boolean hasVar(String name) {
        return varDeclarations.containsKey(name);
    }

    @Override
    public RuleUnitVariable getVar(String name) {
        RuleUnitVariable ruleUnitVariable = varDeclarations.get(name);
        if (ruleUnitVariable == null) {
            throw new UndefinedRuleUnitVariableException(name, this.getCanonicalName());
        }
        return ruleUnitVariable;
    }

    @Override
    public Collection<String> getUnitVars() {
        return varDeclarations.keySet();
    }

    @Override
    public Collection<RuleUnitVariable> getUnitVarDeclarations() {
        return varDeclarations.values();
    }

    @Override
    public boolean hasDataSource(String name) {
        RuleUnitVariable ruleUnitVariable = varDeclarations.get(name);
        return ruleUnitVariable != null && ruleUnitVariable.isDataSource();
    }

    protected void putRuleUnitVariable(RuleUnitVariable varDeclaration) {
        varDeclarations.put(varDeclaration.getName(), varDeclaration);
    }

    protected void setConfig(RuleUnitConfig config) {
        this.config = config;
    }

    public RuleUnitConfig getConfig() {
        return config;
    }

    @Override
    public ClockTypeOption getClockType() {
        return (config.getDefaultedClockType() == org.drools.ruleunits.api.conf.ClockType.PSEUDO) ? ClockTypeOption.PSEUDO : ClockTypeOption.REALTIME;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleUnitDescription && getRuleUnitName().equals(((RuleUnitDescription) obj).getRuleUnitName());
    }

    @Override
    public int hashCode() {
        return getRuleUnitName().hashCode();
    }
}
