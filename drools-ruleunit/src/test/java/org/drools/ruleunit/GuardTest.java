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

package org.drools.ruleunit;

import java.math.BigDecimal;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Activation;
import org.drools.ruleunit.impl.Guard;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GuardTest {

    private RuleUnit guardedUnit;
    private RuleImpl guardingRule;
    private Guard guard;

    @Before
    public void prepareGuard() {
        guardedUnit = new TestRuleUnit(new Integer[]{}, BigDecimal.TEN);
        guardingRule = new RuleImpl();
        guard = new Guard(guardedUnit, guardingRule);
    }

    @Test
    public void testEquals() {
        assertThat(guard).isEqualTo(new Guard(guardedUnit, guardingRule));
    }

    @Test
    public void testAddActivation() {
        guard.addActivation(new MockActivation());
        assertThat(guard.getActivations()).isNotEmpty();
        assertThat(guard.getActivations()).hasSize(1);

        guard.addActivation(new MockActivation());
        guard.addActivation(new MockActivation());
        guard.addActivation(new MockActivation());

        assertThat(guard.getActivations()).hasSize(4);
    }

    @Test
    public void testRemoveActivation() {
        final Activation activation = new MockActivation();
        guard.addActivation(activation);
        assertThat(guard.getActivations()).isNotEmpty();
        guard.removeActivation(activation);
        assertThat(guard.getActivations()).isEmpty();
    }

    @Test
    public void testIsActive() {
        final Activation activation = new MockActivation();
        guard.addActivation(activation);
        assertThat(guard.isActive()).isTrue();
        guard.removeActivation(activation);
        assertThat(guard.isActive()).isFalse();
    }

    @Test
    public void testGetGuardedUnit() {
        assertThat(guardedUnit).isSameAs(guard.getGuardedUnit());
    }
}
