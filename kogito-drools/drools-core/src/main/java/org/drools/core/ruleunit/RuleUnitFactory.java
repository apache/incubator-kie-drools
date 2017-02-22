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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.definition.rule.UnitVar;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.util.ClassUtils.isAssignable;

public class RuleUnitFactory {

    private Map<RuleUnit.Identity, RuleUnit> units = new HashMap<>();
    private Map<String, Object> variables = new HashMap<>();

    public RuleUnitFactory bindVariable( String name, Object dataSource ) {
        variables.put( name, dataSource );
        return this;
    }

    public RuleUnit getOrCreateRuleUnit( String name, ClassLoader classLoader ) {
        try {
            return getOrCreateRuleUnit( (Class<? extends RuleUnit>) Class.forName( name, true, classLoader ) );
        } catch (Exception e) {
            throw new RuntimeException( "Cannot find RuleUnit class " + name, e );
        }
    }

    public <T extends RuleUnit> T getOrCreateRuleUnit( Class<T> ruleUnitClass ) {
        return (T)units.computeIfAbsent( new RuleUnit.Identity(ruleUnitClass), id -> createRuleUnit( ruleUnitClass ) );
    }

    public RuleUnit registerUnit(RuleUnit ruleUnit ) {
        return units.computeIfAbsent( ruleUnit.getUnitIdentity(), id -> injectUnitVariables( ruleUnit ) );
    }

    private <T extends RuleUnit> T createRuleUnit( Class<T> ruleUnitClass ) {
        try {
            return injectUnitVariables( ruleUnitClass, (T) ruleUnitClass.newInstance() );
        } catch (Exception e) {
            throw new RuntimeException( "Unable to instance RuleUnit " + ruleUnitClass.getName(), e );
        }
    }

    public <T extends RuleUnit> T injectUnitVariables( T ruleUnit ) {
        return injectUnitVariables( (Class<T>) ruleUnit.getClass(), ruleUnit );
    }

    private <T extends RuleUnit> T injectUnitVariables( Class<T> ruleUnitClass, T ruleUnit ) {
        for (Field field : ruleUnitClass.getDeclaredFields()) {
            String fieldName = getInjectingName(field);
            Object var = variables.get( getInjectingName(field) );
            if ( isAssignable( field.getType(), var ) ) {
                field.setAccessible( true );
                try {
                    Object existingValue = field.get( ruleUnit );
                    if (existingValue == null || (existingValue instanceof Number && ( (Number) existingValue ).intValue() == 0)) {
                        field.set( ruleUnit, var );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException( "Unable to inject field " + fieldName + " into " + ruleUnitClass.getName(), e );
                }
            }
        }
        return ruleUnit;
    }

    private String getInjectingName(Field field) {
        UnitVar varName = field.getAnnotation( UnitVar.class );
        return varName != null ? varName.value() : field.getName();
    }
}
