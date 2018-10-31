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

import org.drools.core.impl.InternalRuleUnitExecutor;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RuleUnitFactoryTest {

    @Test
    public void bindVariable() {
        // TODO
    }

    @Test
    public void getOrCreateRuleUnit() {
        // TODO
    }

    @Test
    public void getOrCreateRuleUnit1() {
        // TODO
    }

    @Test
    public void registerUnit() {
        // TODO
    }

    @Test
    public void injectUnitVariables() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{}, BigDecimal.ZERO);
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);

    }
}