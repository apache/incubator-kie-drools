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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.EntryPointId;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;

import static org.drools.reflective.util.ClassUtils.convertFromPrimitiveType;
import static org.drools.reflective.util.ClassUtils.getter2property;

public class RuleUnitDescription {
    private final Class<? extends RuleUnitData> ruleUnitClass;

    private final Map<String, String> datasources = new HashMap<>();
    private final Map<String, Class<?>> datasourceTypes = new HashMap<>();

    private final Map<String, Method> varAccessors = new HashMap<>();

    public RuleUnitDescription( InternalKnowledgePackage pkg, Class<? extends RuleUnitData> ruleUnitClass ) {
        this.ruleUnitClass = ruleUnitClass;
        indexUnitVars(pkg);
    }

    public Class<? extends RuleUnitData> getRuleUnitClass() {
        return ruleUnitClass;
    }

    public String getRuleUnitName() {
        return ruleUnitClass.getName();
    }

    public Optional<EntryPointId> getEntryPointId( String name ) {
        return Optional.ofNullable( datasources.get( name ) ).map( ds -> new EntryPointId( name ) );
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
        return datasources.containsKey( name );
    }

    public Object getValue( RuleUnitData ruleUnit, String identifier) {
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

    private void indexUnitVars(InternalKnowledgePackage pkg) {
        for (Method m : ruleUnitClass.getMethods()) {
            if ( m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0 ) {
                String id = getter2property(m.getName());
                if (id != null && !id.equals( "class" )) {
                    indexUnitAccessor( pkg, m, id );
                }
            }
        }
    }

    private void indexUnitAccessor( InternalKnowledgePackage pkg, Method m, String id ) {
        datasources.put( id, m.getName() );
        varAccessors.put( id, m );

        Class<?> unitVarType = getUnitVarType(m);
        datasourceTypes.put( id, unitVarType );
        pkg.addGlobal( id, convertFromPrimitiveType( m.getReturnType() ) );
    }

    private Class<?> getUnitVarType(Method m) {
        Class<?> returnClass = m.getReturnType();
        if (returnClass.isArray()) {
            return returnClass.getComponentType();
        }
        if (DataSource.class.isAssignableFrom( returnClass )) {
            return getParametricType(m);
        }
        if (Iterable.class.isAssignableFrom( returnClass )) {
            return getParametricType(m);
        }
        return returnClass;
    }

    private Class<?> getParametricType(Method m) {
        Type returnType = m.getGenericReturnType();
        return  returnType instanceof ParameterizedType ?
                (Class<?>) ( (ParameterizedType) returnType ).getActualTypeArguments()[0] :
                Object.class;
    }
}
