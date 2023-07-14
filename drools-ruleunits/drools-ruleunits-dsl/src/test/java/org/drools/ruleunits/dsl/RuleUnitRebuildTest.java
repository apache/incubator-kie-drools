/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.dsl;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.listener.TestRuleRuntimeEventListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleUnitRebuildTest {

    @Test
    void dynamicHelloWorld() {
        DynamicHelloWorldUnit unit = new DynamicHelloWorldUnit("Hello World");
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, true)) { // rebuild. Do not be affected by other tests
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }

        DynamicHelloWorldUnit newUnit = new DynamicHelloWorldUnit("Goodbye World");
        newUnit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> newUnitInstance = RuleUnitProvider.get().createRuleUnitInstance(newUnit, true)) { // rebuild
            assertThat(newUnitInstance.fire()).isZero();
            assertThat(newUnit.getResults()).isEmpty();

            newUnit.getStrings().add("Goodbye World");
            assertThat(newUnitInstance.fire()).isEqualTo(1);
            assertThat(newUnit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    void dynamicHelloWorldWithRuleConfig() {
        TestRuleRuntimeEventListener testRuleRuntimeEventListener = new TestRuleRuntimeEventListener();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getRuleRuntimeListeners().add(testRuleRuntimeEventListener);

        DynamicHelloWorldUnit unit = new DynamicHelloWorldUnit("Hello World");
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig, true)) { // rebuild. Do not be affected by other tests
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
            assertThat(testRuleRuntimeEventListener.getResults()).containsExactly("objectInserted : Hello World");
        }
        testRuleRuntimeEventListener.getResults().clear();

        DynamicHelloWorldUnit newUnit = new DynamicHelloWorldUnit("Goodbye World"); // rule has changed
        newUnit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> newUnitInstance = RuleUnitProvider.get().createRuleUnitInstance(newUnit, ruleConfig, true)) { // rebuild
            assertThat(newUnitInstance.fire()).isZero();
            assertThat(newUnit.getResults()).isEmpty();

            newUnit.getStrings().add("Goodbye World");
            assertThat(newUnitInstance.fire()).isEqualTo(1);
            assertThat(newUnit.getResults()).containsExactly("it worked!");
            assertThat(testRuleRuntimeEventListener.getResults()).containsExactly("objectInserted : Hello World", "objectInserted : Goodbye World");
        }
    }
}
