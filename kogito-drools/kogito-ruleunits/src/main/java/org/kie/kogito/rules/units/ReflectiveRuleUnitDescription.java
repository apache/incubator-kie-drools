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

package org.kie.kogito.rules.units;

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
import org.kie.kogito.rules.units.AbstractRuleUnitDescription;

import static org.drools.reflective.util.ClassUtils.getter2property;

public class ReflectiveRuleUnitDescription extends AbstractRuleUnitDescription {

    private final Class<? extends RuleUnitData> ruleUnitClass;

    public ReflectiveRuleUnitDescription(InternalKnowledgePackage pkg, Class<? extends RuleUnitData> ruleUnitClass) {
        this.ruleUnitClass = ruleUnitClass;
        indexUnitVars();
    }

    @Override
    public Class<?> getRuleUnitClass() {
        return ruleUnitClass;
    }

    @Override
    public String getCanonicalName() {
        return getRuleUnitClass().getCanonicalName();
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
    public String getRuleUnitName() {
        return ruleUnitClass.getName();
    }

    private void indexUnitVars() {
        for (Method m : ruleUnitClass.getMethods()) {
            if (m.getDeclaringClass() != RuleUnit.class && m.getParameterCount() == 0) {
                String id = getter2property(m.getName());
                if (id != null && !id.equals("class")) {
                    Class<?> parametricType = getUnitVarType(m);
                    putRuleUnitVariable(
                            new SimpleRuleUnitVariable(id, m.getReturnType(), parametricType));
                }
            }
        }
    }

    private Class<?> getUnitVarType(Method m) {
        Class<?> returnClass = m.getReturnType();
        if (returnClass.isArray()) {
            return returnClass.getComponentType();
        }
        if (DataSource.class.isAssignableFrom(returnClass)) {
            return getParametricType(m);
        }
        if (Iterable.class.isAssignableFrom(returnClass)) {
            return getParametricType(m);
        }
        return null;
    }

    private Class<?> getParametricType(Method m) {
        Type returnType = m.getGenericReturnType();
        return returnType instanceof ParameterizedType ?
                (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0] :
                Object.class;
    }
}
