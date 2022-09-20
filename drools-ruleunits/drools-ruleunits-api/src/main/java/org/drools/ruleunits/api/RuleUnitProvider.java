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

import org.kie.api.internal.utils.KieService;

public interface RuleUnitProvider extends KieService {

    <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData);

    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData) {
        RuleUnit<T> ruleUnit = getRuleUnit(ruleUnitData);
        if (ruleUnit == null) {
            throw new RuntimeException("Cannot find any rule unit for RuleUnitData of class:" + ruleUnitData.getClass().getCanonicalName());
        }
        return ruleUnit.createInstance(ruleUnitData);
    }

    static RuleUnitProvider get() {
        return RuleUnitProvider.Factory.get();
    }

    class Factory {

        private static class LazyHolder {
            private static RuleUnitProvider INSTANCE = KieService.load(RuleUnitProvider.class);
        }

        public static RuleUnitProvider get() {
            return RuleUnitProvider.Factory.LazyHolder.INSTANCE;
        }
    }
}
