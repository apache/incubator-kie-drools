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
import org.drools.core.datasources.InternalDataSource;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.kie.api.runtime.rule.DataSource;
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
        return varAccessors.get( name ) != null;
    }

    public void bindDataSources( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit ) {
        datasources.forEach( (name, accessor) -> bindDataSource( wm, ruleUnit, name, accessor ) );
    }

    public void unbindDataSources( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit ) {
        datasources.forEach( (name, accessor) -> findDataSource( ruleUnit, accessor ).ifPresent( ds -> ds.unbind( ruleUnit ) ) );
        // TODO review
//        datasources.values().forEach( accessor -> findDataSource( ruleUnit, accessor ).filter(AbstractReactiveDataSource.class::isInstance)
//                                                                                      .map( AbstractReactiveDataSource.class::cast )
//                                                                                      .ifPresent( AbstractReactiveDataSource::unbind ) );
    }

    private void bindDataSource( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit, String name, String accessor ) {
        Optional<InternalDataSource> datasource = findDataSource( ruleUnit, accessor );
        Optional<WorkingMemoryEntryPoint> entrypoint = datasource.flatMap( ds -> propagateInserts( wm, ruleUnit, name, ds ) );
        // TODO review
//        datasource.filter(AbstractReactiveDataSource.class::isInstance)
//                  .map( AbstractReactiveDataSource.class::cast )
//                  .ifPresent( ds -> ds.bind( entrypoint.orElse( null ), ruleUnit ) );
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

    private Optional<InternalDataSource> findDataSource( RuleUnit ruleUnit, String accessor ) {
        try {
            Object value = ruleUnit.getClass().getMethod( accessor ).invoke( ruleUnit );
            return value instanceof InternalDataSource ? Optional.of( (InternalDataSource) value ) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private Optional<WorkingMemoryEntryPoint> propagateInserts( StatefulKnowledgeSessionImpl wm, RuleUnit ruleUnit, String dataSourceName, InternalDataSource dataSource ) {
        Optional<WorkingMemoryEntryPoint> entryPoint = Optional.ofNullable( wm.getEntryPoint( getEntryPointName( dataSourceName ) ) );
        entryPoint.ifPresent( ep -> dataSource.bind( ruleUnit, ep ) );
        return entryPoint;
    }

    private void indexUnitVars() {
        for (Method m : ruleUnitClass.getMethods()) {
            if ( m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0 ) {
                String id = getter2property(m.getName());
                if (id != null && !id.equals( "class" )) {
                    if (DataSource.class.isAssignableFrom( m.getReturnType() )) {
                        datasources.put( id, m.getName() );
                        Type returnType = m.getGenericReturnType();
                        Class<?> sourceType = returnType instanceof ParameterizedType ?
                                              (Class<?>) ( (ParameterizedType) returnType ).getActualTypeArguments()[0] :
                                              Object.class;
                        datasourceTypes.put( id, sourceType );
                    }
                    varAccessors.put( id, m );
                }
            }
        }
    }
}
