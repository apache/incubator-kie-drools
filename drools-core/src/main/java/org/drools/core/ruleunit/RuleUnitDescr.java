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

package org.drools.core.ruleunit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.datasources.BindableArray;
import org.drools.core.datasources.BindableDataProvider;
import org.drools.core.datasources.BindableIterable;
import org.drools.core.datasources.BindableObject;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.util.ClassUtils.getter2property;

public class RuleUnitDescr {
    private final Class<? extends RuleUnit> ruleUnitClass;

    private final Map<String, String> datasources = new HashMap<>();
    private final Map<String, Class<?>> datasourceTypes = new HashMap<>();

    private final Map<String, Method> varAccessors = new HashMap<>();

    public RuleUnitDescr( Class<? extends RuleUnit> ruleUnitClass ) {
        this.ruleUnitClass = ruleUnitClass;
        indexUnitVars();
    }

    public Class<? extends RuleUnit> getRuleUnitClass() {
        return ruleUnitClass;
    }

    public String getRuleUnitName() {
        return ruleUnitClass.getName();
    }

    private String getEntryPointName(String name) {
        return getRuleUnitName() + "." + name;
    }

    public Optional<EntryPointId> getEntryPointId( String name ) {
        return Optional.ofNullable( datasources.get( name ) ).map( ds -> new EntryPointId( getEntryPointName(name) ) );
    }

    public Optional<Class<?>> getDatasourceType( String name ) {
        return Optional.ofNullable( datasourceTypes.get( name ) );
    }

    public Optional<Class<?>> getVarType( String name ) {
        return Optional.ofNullable( varAccessors.get( name ) ).map( m -> m.getReturnType() );
    }

    public boolean hasVar( String name ) {
        return varAccessors.containsKey( name );
    }

    public boolean hasDataSource( String name ) {
        return datasources.containsKey( name );
    }

    public void bindDataSources( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit ) {
        datasources.forEach( (name, accessor) -> bindDataSource( wm, ruleUnit, name, accessor ) );
    }

    private void bindDataSource( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit, String name, String accessor ) {
        BindableDataProvider dataSource = findDataSource( ruleUnit, accessor );
        if (dataSource != null) {
            WorkingMemoryEntryPoint entryPoint = wm.getEntryPoint( getEntryPointName( name ) );
            if (entryPoint != null) {
                dataSource.bind( ruleUnit, entryPoint );
            }
        }
    }

    public void unbindDataSources( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit ) {
        datasources.values().forEach( accessor -> unbindDataSource( ruleUnit, accessor ) );
    }

    private void unbindDataSource( RuleUnit ruleUnit, String accessor ) {
        BindableDataProvider dataSource = findDataSource( ruleUnit, accessor );
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

    private BindableDataProvider findDataSource( RuleUnit ruleUnit, String accessor ) {
        try {
            Object value = ruleUnit.getClass().getMethod( accessor ).invoke( ruleUnit );
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
            if ( m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0 ) {
                String id = getter2property(m.getName());
                if (id != null && !id.equals( "class" )) {
                    datasources.put( id, m.getName() );
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
