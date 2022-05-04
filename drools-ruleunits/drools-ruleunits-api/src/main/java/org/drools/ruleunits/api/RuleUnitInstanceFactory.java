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
package org.drools.ruleunits.api;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class RuleUnitInstanceFactory {

    private static class RuleUnitServices {
        private static final Map<String, RuleUnit> ruleUnitMap = loadRuleUnits();

        static Map<String, RuleUnit> loadRuleUnits() {
            Map<String, RuleUnit> map = new HashMap<>();
            ServiceLoader<RuleUnit> loader = ServiceLoader.load(RuleUnit.class);
            for (RuleUnit impl : loader) {
                map.put(impl.id(), impl);
            }
            return map;
        }
    }

    public static <T extends RuleUnitData> RuleUnitInstance<T> instance(T ruleUnit) {
        RuleUnit unit = RuleUnitServices.ruleUnitMap.get(ruleUnit.getClass().getCanonicalName());
        if (unit == null) {
            throw new RuntimeException("There isn't any generated rule unit for " + ruleUnit.getClass().getCanonicalName() + ", do you forget to run code generation?");
        }
        return unit.createInstance(ruleUnit);
    }
}
