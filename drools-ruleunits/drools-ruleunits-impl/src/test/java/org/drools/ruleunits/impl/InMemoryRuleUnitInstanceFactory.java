/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;

import static org.drools.ruleunits.impl.InterpretedRuleUnit.createRuleUnitKieProject;

public class InMemoryRuleUnitInstanceFactory {

    private static final Map<Class, ClassLoader> kieModuleClassLoaders = new HashMap<>();

    public static <T extends RuleUnitData> RuleUnitInstance<T> generateAndInstance(T ruleUnit, String... drls) {
        ClassLoader kieModuleClassLoader = kieModuleClassLoaders.computeIfAbsent(ruleUnit.getClass(), c -> createRuleUnitKieProject(c, drls).getClassLoader());
        return loadRuleUnits(kieModuleClassLoader).get(ruleUnit.getClass().getCanonicalName()).createInstance(ruleUnit);
    }

    private static Map<String, RuleUnit> loadRuleUnits(ClassLoader kieModuleClassLoader) {
        Map<String, RuleUnit> map = new HashMap<>();
        ServiceLoader<RuleUnit> loader = ServiceLoader.load(RuleUnit.class, kieModuleClassLoader);
        for (RuleUnit impl : loader) {
            map.put(impl.id(), impl);
        }
        return map;
    }
}
