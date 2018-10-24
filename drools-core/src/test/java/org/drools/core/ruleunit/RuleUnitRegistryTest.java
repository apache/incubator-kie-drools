/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.ruleunit;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleUnitRegistryTest {

    @Test
    public void getRuleUnitDescr() {
        // TODO
    }

    @Test
    public void getRuleUnitFor() {
        // TODO
    }

    @Test
    public void getRuleUnitFor1() {
        // TODO
    }

    @Test
    public void getNamedRuleUnit() {
        // TODO
    }

    @Test
    public void registerRuleUnit() {
        final TestRuleUnit testRuleUnit = createTestRuleUnit();
        final RuleUnitRegistry ruleUnitRegistry = new RuleUnitRegistry();
        ruleUnitRegistry.registerRuleUnit("testRuleUnit", () -> TestRuleUnit.class);

    }

    @Test
    public void add() {
        // TODO
    }

    @Test
    public void hasUnits() {
        // TODO
    }

    private TestRuleUnit createTestRuleUnit() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);
        return testRuleUnit;
    }
}