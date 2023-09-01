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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.EventProcessing;
import org.drools.ruleunits.api.conf.RuleUnitConfig;
import org.drools.ruleunits.api.conf.SessionsPool;

import static org.drools.wiring.api.util.ClassUtils.getSetter;
import static org.drools.wiring.api.util.ClassUtils.getter2property;

public class ReflectiveRuleUnitDescription extends AbstractRuleUnitDescription {

    private final Class<? extends RuleUnitData> ruleUnitClass;
    private final AssignableChecker assignableChecker;

    public ReflectiveRuleUnitDescription(Class<? extends RuleUnitData> ruleUnitClass) {
        this.ruleUnitClass = ruleUnitClass;
        this.assignableChecker = AssignableChecker.create(ruleUnitClass.getClassLoader());
        indexUnitVars();
        setConfig(loadConfig(ruleUnitClass));
    }

    @Override
    public String getCanonicalName() {
        return ruleUnitClass.getCanonicalName();
    }

    @Override
    public String getSimpleName() {
        return ruleUnitClass.getSimpleName();
    }

    @Override
    public String getPackageName() {
        Package aPackage = ruleUnitClass.getPackage();
        if (aPackage == null) {
            String canonicalName = ruleUnitClass.getCanonicalName();
            return canonicalName.substring(0, canonicalName.length() - getSimpleName().length() - 1);
        } else {
            return aPackage.getName();
        }
    }

    @Override
    public Class<?> getRuleUnitClass() {
        return ruleUnitClass;
    }

    @Override
    public String getRuleUnitName() {
        return ruleUnitClass.getName();
    }

    private void indexUnitVars() {
        Arrays.stream(ruleUnitClass.getMethods())
                .filter( m -> m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0 )
                .forEach( this::registerRuleUnitVariable );
    }

    private void registerRuleUnitVariable(Method m) {
        String id = getter2property(m.getName());
        if (id != null && !id.equals("class")) {
            Class<?> parametricType = getUnitVarType(m);
            Method setter = getSetter(m.getDeclaringClass(), id, m.getReturnType());
            String setterName = setter != null ? setter.getName() : null;
            putRuleUnitVariable( new SimpleRuleUnitVariable(id, m.getGenericReturnType(), parametricType, setterName) );
        }
    }

    private Class<?> getUnitVarType(Method m) {
        Class<?> returnClass = m.getReturnType();
        if (returnClass.isArray()) {
            return returnClass.getComponentType();
        }
        if (assignableChecker.isAssignableFrom(DataSource.class, returnClass)) {
            return getParametricType(m);
        }
        if (Iterable.class.isAssignableFrom(returnClass)) {
            return getParametricType(m);
        }
        return null;
    }

    public AssignableChecker getAssignableChecker() {
        return assignableChecker;
    }

    private Class<?> getParametricType(Method m) {
        Type returnType = m.getGenericReturnType();
        return returnType instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0] : Object.class;
    }

    private static RuleUnitConfig loadConfig(Class<? extends RuleUnitData> ruleUnitClass) {
        Optional<EventProcessing> eventAnn = Optional.ofNullable(ruleUnitClass.getAnnotation(EventProcessing.class));
        Optional<Clock> clockAnn = Optional.ofNullable(ruleUnitClass.getAnnotation(Clock.class));
        Optional<SessionsPool> sessionsPoolAnn = Optional.ofNullable(ruleUnitClass.getAnnotation(SessionsPool.class));

        return new RuleUnitConfig(
                eventAnn.map(EventProcessing::value).orElse(null),
                clockAnn.map(Clock::value).orElse(null),
                sessionsPoolAnn.map(SessionsPool::value).orElse(null));
    }
}
