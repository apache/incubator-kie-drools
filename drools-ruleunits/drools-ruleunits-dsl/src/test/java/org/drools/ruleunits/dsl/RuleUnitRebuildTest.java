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
package org.drools.ruleunits.dsl;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleUnitRebuildTest {

    @Test
    void dynamicHelloWorld() {
        DynamicHelloWorldUnit unit = new DynamicHelloWorldUnit("Hello World");
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }

        int invalidated = RuleUnitProvider.get().invalidateRuleUnits(DynamicHelloWorldUnit.class);
        assertThat(invalidated).as("Invalidate 1 rule unit").isEqualTo(1);

        DynamicHelloWorldUnit newUnit = new DynamicHelloWorldUnit("Goodbye World");
        newUnit.getStrings().add("Hello World");

        try (RuleUnitInstance<DynamicHelloWorldUnit> newUnitInstance = RuleUnitProvider.get().createRuleUnitInstance(newUnit)) {
            assertThat(newUnitInstance.fire()).isZero();
            assertThat(newUnit.getResults()).isEmpty();

            newUnit.getStrings().add("Goodbye World");
            assertThat(newUnitInstance.fire()).isEqualTo(1);
            assertThat(newUnit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    void invalidateMultipleNamedRuleUnits() {
        NamedHelloWorldUnit unit1 = new NamedHelloWorldUnit("Name-1");
        unit1.getStrings().add("Name-1");

        try (RuleUnitInstance<NamedHelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit1)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit1.getResults()).containsExactly("it worked!");
        }

        NamedHelloWorldUnit unit2 = new NamedHelloWorldUnit("Name-2");
        unit2.getStrings().add("Name-2");

        try (RuleUnitInstance<NamedHelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit2)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit2.getResults()).containsExactly("it worked!");
        }

        int invalidated = RuleUnitProvider.get().invalidateRuleUnits(NamedHelloWorldUnit.class);
        assertThat(invalidated).as("Invalidate 2 rule units").isEqualTo(2);

        NamedHelloWorldUnit unit3 = new NamedHelloWorldUnit("Name-3");
        unit3.getStrings().add("Name-3");

        try (RuleUnitInstance<NamedHelloWorldUnit> newUnitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit3)) {
            assertThat(newUnitInstance.fire()).isEqualTo(1);
            assertThat(unit3.getResults()).containsExactly("it worked!");
        }
    }
}
