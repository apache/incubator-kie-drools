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
    <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData);

    /**
     * Creates a new {@link RuleUnitInstance} from the {@link RuleUnit} generated for the given {@link RuleUnitData}.
     * This is equivalent to
     * <pre>
     * RuleUnitProvider.get().getRuleUnit(ruleUnitData).createInstance(ruleUnitData);
     * </pre>
     * throwing a runtime exception if there isn't any {@link RuleUnit} generated for the given {@link RuleUnitData}.
     */
    default <T extends RuleUnitData> RuleUnitInstance<T> createRuleUnitInstance(T ruleUnitData) {
        RuleUnit<T> ruleUnit = getRuleUnit(ruleUnitData);
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
        RuleUnit<T> ruleUnit = getRuleUnit(ruleUnitData);
        if (ruleUnit == null) {
            throw new RuntimeException("Cannot find any rule unit for RuleUnitData of class:" + ruleUnitData.getClass().getCanonicalName());
        }
        return ruleUnit.createInstance(ruleUnitData, ruleConfig);
    }

    /**
     * Invalidates all {@link RuleUnit}s generated from the given class.
     * @return The number of invalidated ruleunits.
     */
    <T extends RuleUnitData> int invalidateRuleUnits(Class<T> ruleUnitDataClass);

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
