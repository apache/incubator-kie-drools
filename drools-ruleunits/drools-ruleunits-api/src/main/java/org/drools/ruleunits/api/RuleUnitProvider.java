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

import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.internal.utils.KieService;

/**
 * A provider of {@link RuleUnit} and {@link RuleUnitInstance} from a given {@link RuleUnitData}.
 */
public interface RuleUnitProvider extends KieService {

    /**
     * Provides the {@link RuleUnit} generated for the given {@link RuleUnitData}.
     * @return The generated {@link RuleUnit} or null if there's no {@link RuleUnit} generated for the given {@link RuleUnitData}.
     */
    default <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData) {
        return getRuleUnit(ruleUnitData, false);
    }

    /**
     * Provides the {@link RuleUnit} generated for the given {@link RuleUnitData}.
     * @param rebuild if true, the {@link RuleUnit} is regenerated instead of using the cached one.
     * @return The generated {@link RuleUnit} or null if there's no {@link RuleUnit} generated for the given {@link RuleUnitData}.
     */
    <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData, boolean rebuild);

    /**
     * Creates a new {@link RuleUnitInstance} from the {@link RuleUnit} generated for the given {@link RuleUnitData}.
     * This is equivalent to
     * <pre>
     * RuleUnitProvider.get().getRuleUnit(ruleUnitData).createInstance(ruleUnitData);
     * </pre>
     * throwing a runtime exception if there isn't any {@link RuleUnit} generated for the given {@link RuleUnitData}.
     */
    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData) {
        return createRuleUnitInstance(ruleUnitData, false);
    }

    /**
     * Creates a new {@link RuleUnitInstance} from the {@link RuleUnit} generated for the given {@link RuleUnitData}.
     * This is equivalent to
     * <pre>
     * RuleUnitProvider.get().getRuleUnit(ruleUnitData, rebuild).createInstance(ruleUnitData);
     * </pre>
     * throwing a runtime exception if there isn't any {@link RuleUnit} generated for the given {@link RuleUnitData}.
     *
     * @param rebuild if true, the {@link RuleUnit} is regenerated before creating the {@link RuleUnitInstance}.
     */
    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData, boolean rebuild) {
        RuleUnit<T> ruleUnit = getRuleUnit(ruleUnitData, rebuild);
        if (ruleUnit == null) {
            throw new RuntimeException("Cannot find any rule unit for RuleUnitData of class:" + ruleUnitData.getClass().getCanonicalName());
        }
        return ruleUnit.createInstance(ruleUnitData);
    }

    /**
     * Creates a new {@link RuleUnitInstance} from the {@link RuleUnit} generated for the given {@link RuleUnitData} with {@link RuleConfig}.
     * This is equivalent to
     * <pre>
     * RuleUnitProvider.get().getRuleUnit(ruleUnitData).createInstance(ruleUnitData, ruleConfig);
     * </pre>
     * throwing a runtime exception if there isn't any {@link RuleUnit} generated for the given {@link RuleUnitData}.
     */
    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData, RuleConfig ruleConfig) {
        return createRuleUnitInstance(ruleUnitData, ruleConfig, false);
    }

    /**
     * Creates a new {@link RuleUnitInstance} from the {@link RuleUnit} generated for the given {@link RuleUnitData} with {@link RuleConfig}.
     * This is equivalent to
     * <pre>
     * RuleUnitProvider.get().getRuleUnit(ruleUnitData).createInstance(ruleUnitData, ruleConfig);
     * </pre>
     * throwing a runtime exception if there isn't any {@link RuleUnit} generated for the given {@link RuleUnitData}.
     *
     * @param rebuild if true, the {@link RuleUnit} is regenerated before creating the {@link RuleUnitInstance}.
     */
    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData, RuleConfig ruleConfig, boolean rebuild) {
        RuleUnit<T> ruleUnit = getRuleUnit(ruleUnitData, rebuild);
        if (ruleUnit == null) {
            throw new RuntimeException("Cannot find any rule unit for RuleUnitData of class:" + ruleUnitData.getClass().getCanonicalName());
        }
        return ruleUnit.createInstance(ruleUnitData, ruleConfig);
    }

    /**
     * Creates a new RuleConfig instance.
     */
    RuleConfig newRuleConfig();

    /**
     * Returns an instance of the RuleUnitProvider.
     */
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
