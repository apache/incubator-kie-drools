/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.ClassUtils;
import org.drools.ruleunit.datasources.BindableArray;
import org.drools.ruleunit.datasources.BindableDataProvider;
import org.drools.ruleunit.datasources.BindableIterable;
import org.drools.ruleunit.datasources.BindableObject;
import org.drools.ruleunit.executor.RuleUnitSessionImpl;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.drools.ruleunit.RuleUnit;

public class RuleUnitDescriptionImpl implements RuleUnitDescription {
    private final Class<? extends RuleUnit> ruleUnitClass;

    private final Map<String, Class<?>> datasourceTypes = new HashMap<>();

    private final Map<String, Method> varAccessors = new HashMap<>();

    public RuleUnitDescriptionImpl( KiePackage pkg, Class<?> ruleUnitClass ) {
        this.ruleUnitClass = (Class<? extends RuleUnit>) ruleUnitClass;
        indexUnitVars();
    }

    public Class<? extends RuleUnit> getRuleUnitClass() {
        return ruleUnitClass;
    }

    public Optional<EntryPointId> getEntryPointId( String name ) {
        return varAccessors.containsKey( name ) ? Optional.of( new EntryPointId( getEntryPointName(name) ) ) : Optional.empty();
    }

    public Optional<Class<?>> getDatasourceType( String name ) {
        return Optional.ofNullable( datasourceTypes.get( name ) );
    }

    public Optional<Class<?>> getVarType( String name ) {
        return Optional.ofNullable( varAccessors.get( name ) ).map( Method::getReturnType );
    }

    public boolean hasVar( String name ) {
        return varAccessors.containsKey( name );
    }

    public Collection<String> getUnitVars() {
        return varAccessors.keySet();
    }

    public Map<String, Method> getUnitVarAccessors() {
        return varAccessors;
    }

    public boolean hasDataSource( String name ) {
        return varAccessors.containsKey( name );
    }

    public void bindDataSources( RuleUnitSessionImpl wm, RuleUnit ruleUnit ) {
        varAccessors.forEach( (name, method) -> bindDataSource( wm, ruleUnit, name, method ) );
    }

    private void bindDataSource( RuleUnitSessionImpl wm, RuleUnit ruleUnit, String name, Method method ) {
        WorkingMemoryEntryPoint entryPoint = wm.getEntryPoint( getEntryPointName( name ) );
        if (entryPoint != null) {
            BindableDataProvider dataSource = findDataSource( ruleUnit, method );
            if (dataSource != null) {
                entryPoint.setRuleUnit( ruleUnit );
                dataSource.bind( ruleUnit, entryPoint );
            }
        }
    }

    public void unbindDataSources( RuleUnitSessionImpl wm, RuleUnit ruleUnit ) {
        varAccessors.values().forEach( method -> unbindDataSource( ruleUnit, method ) );
    }

    private void unbindDataSource( RuleUnit ruleUnit, Method method ) {
        BindableDataProvider dataSource = findDataSource( ruleUnit, method );
        if (dataSource != null) {
            dataSource.unbind( ruleUnit );
        }
    }

    public Object getValue(RuleUnit ruleUnit, String identifier) {
        Method m = varAccessors.get(identifier);
        if (m != null) {
            try {
                return m.invoke( ruleUnit );
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    private BindableDataProvider findDataSource( RuleUnit ruleUnit, String name ) {
        return findDataSource( ruleUnit, varAccessors.get( name ) );
    }

    private BindableDataProvider findDataSource( RuleUnit ruleUnit, Method m ) {
        try {
            Object value = m.invoke( ruleUnit );
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
            if ( m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0 && !"getUnitIdentity".equals(m.getName())) {
                String id = ClassUtils.getter2property(m.getName());
                if (id != null && !id.equals( "class" )) {
                    varAccessors.put( id, m );

                    Class<?> returnClass = m.getReturnType();
                    if (returnClass.isArray()) {
                        datasourceTypes.put( id, returnClass.getComponentType() );
                    } else if (Iterable.class.isAssignableFrom( returnClass )) {
                        Type returnType = m.getGenericReturnType();
                        Class<?> sourceType = returnType instanceof ParameterizedType ?
                                (Class<?>) ( (ParameterizedType) returnType ).getActualTypeArguments()[0] :
                                Object.class;
                        datasourceTypes.put( id, sourceType );
                    } else {
                        datasourceTypes.put( id, returnClass );
                    }
                }
            }
        }
    }
}
