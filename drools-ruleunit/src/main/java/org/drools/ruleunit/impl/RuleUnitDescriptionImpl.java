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

package org.drools.ruleunit.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.rule.EntryPointId;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.datasources.BindableArray;
import org.drools.ruleunit.datasources.BindableDataProvider;
import org.drools.ruleunit.datasources.BindableIterable;
import org.drools.ruleunit.datasources.BindableObject;
import org.drools.ruleunit.executor.RuleUnitSessionImpl;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.drools.reflective.util.ClassUtils.getter2property;

public class RuleUnitDescriptionImpl implements RuleUnitDescription {
    private final Class<? extends RuleUnit> ruleUnitClass;
    private final Map<String, ReflectiveRuleUnitVariable> varDeclarations = new HashMap<>();

    public RuleUnitDescriptionImpl(KiePackage pkg, Class<?> ruleUnitClass) {
        this.ruleUnitClass = (Class<? extends RuleUnit>) ruleUnitClass;
        indexUnitVars();
    }

    public Class<? extends RuleUnit> getRuleUnitClass() {
        return ruleUnitClass;
    }

    @Override
    public String getSimpleName() {
        return ruleUnitClass.getSimpleName();
    }

    @Override
    public String getPackageName() {
        return ruleUnitClass.getPackage().getName();
    }

    public Optional<EntryPointId> getEntryPointId(String name ) {
        return varDeclarations.containsKey( name ) ? Optional.of( new EntryPointId( getEntryPointName(name) ) ) : Optional.empty();
    }


    @Override
    public Optional<Class<?>> getDatasourceType(String name) {
        return Optional.ofNullable(varDeclarations.get(name))
                .filter(RuleUnitVariable::isDataSource)
                .map(RuleUnitVariable::getDataSourceParameterType);
    }


    @Override
    public Optional<Class<?>> getVarType(String name) {
        return Optional.ofNullable(varDeclarations.get(name)).map(RuleUnitVariable::getType);
    }

    @Override
    public boolean hasVar(String name) {
        return varDeclarations.containsKey(name);
    }

    @Override
    public RuleUnitVariable getVar(String name) {
        return varDeclarations.get(name);
    }

    @Override
    public Collection<String> getUnitVars() {
        return varDeclarations.keySet();
    }

    @Override
    public Collection<? extends RuleUnitVariable> getUnitVarDeclarations() {
        return varDeclarations.values();
    }

    @Override
    public boolean hasDataSource(String name) {
        RuleUnitVariable ruleUnitVariable = varDeclarations.get(name);
        return ruleUnitVariable != null && ruleUnitVariable.isDataSource();
    }

    public void bindDataSources(RuleUnitSessionImpl wm, RuleUnit ruleUnit ) {
        varDeclarations.values().forEach( v -> bindDataSource( wm, ruleUnit, v ) );
    }

    private void bindDataSource( RuleUnitSessionImpl wm, RuleUnit ruleUnit, ReflectiveRuleUnitVariable v ) {
        WorkingMemoryEntryPoint entryPoint = wm.getEntryPoint(getEntryPointName(v.getName()) );
        if (entryPoint != null) {
            BindableDataProvider dataSource = findDataSource( ruleUnit, v );
            if (dataSource != null) {
                entryPoint.setRuleUnit( ruleUnit );
                dataSource.bind( ruleUnit, entryPoint );
            }
        }
    }

    public void unbindDataSources( RuleUnitSessionImpl wm, RuleUnit ruleUnit ) {
        varDeclarations.values().forEach( v -> unbindDataSource( ruleUnit, v ) );
    }

    private void unbindDataSource( RuleUnit ruleUnit, ReflectiveRuleUnitVariable v ) {
        BindableDataProvider dataSource = findDataSource( ruleUnit, v );
        if (dataSource != null) {
            dataSource.unbind( ruleUnit );
        }
    }

    public Object getValue(RuleUnit ruleUnit, String identifier) {
        ReflectiveRuleUnitVariable v = varDeclarations.get(identifier);
        return v == null ? null : v.getValue(ruleUnit);
    }

    private BindableDataProvider findDataSource( RuleUnit ruleUnit, String name ) {
        return findDataSource( ruleUnit, varDeclarations.get( name ) );
    }

    private BindableDataProvider findDataSource(RuleUnit ruleUnit, ReflectiveRuleUnitVariable m ) {
        try {
            Object value = m.getValue( ruleUnit );
            if (value == null) {
                return null;
            }
            if (value instanceof BindableDataProvider) {
                return ( (BindableDataProvider) value );
            }
            if (value instanceof Iterable) {
                return new BindableIterable( (Iterable) value );
            }
            if (value.getClass().isArray()) {
                return new BindableArray( (Object[]) value );
            }
            return new BindableObject( value );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private void indexUnitVars() {
        for (Method m : ruleUnitClass.getMethods()) {
            if ( m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0
                    && !"getUnitIdentity".equals(m.getName())
                    && !"getClass".equals(m.getName())) {
                String id = getter2property(m.getName());
                if (id != null) {
                    ReflectiveRuleUnitVariable v = new ReflectiveRuleUnitVariable(id, m);
                    varDeclarations.put(v.getName(), v);
                }
            }
        }
    }
}
