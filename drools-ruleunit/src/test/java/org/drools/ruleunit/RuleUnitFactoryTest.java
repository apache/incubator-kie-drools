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
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.drools.ruleunit.datasources.CursoredDataSource;
import org.drools.ruleunit.executor.InternalRuleUnitExecutor;
import org.drools.ruleunit.impl.RuleUnitFactory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class RuleUnitFactoryTest {

    private RuleUnitFactory factory;

    @Before
    public void prepareRuleUnitFactory() {
        factory = new RuleUnitFactory();
    }

    @Test
    public void getOrCreateRuleUnitWithClass() {
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);
        final TestRuleUnit testRuleUnit = factory.getOrCreateRuleUnit(ruleUnitExecutor, TestRuleUnit.class);
        Assertions.assertThat(testRuleUnit).isNotNull();
        assertThat(factory.getOrCreateRuleUnit(ruleUnitExecutor, TestRuleUnit.class)).isSameAs(testRuleUnit);
        verifyZeroInteractions(ruleUnitExecutor);
    }

    @Test
    public void getOrCreateRuleUnitWithClassName() {
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);
        final RuleUnit testRuleUnit = factory.getOrCreateRuleUnit(ruleUnitExecutor, TestRuleUnit.class.getCanonicalName(), this.getClass().getClassLoader());
        assertThat(testRuleUnit).isNotNull().isInstanceOf(TestRuleUnit.class);
        assertThat(factory.getOrCreateRuleUnit(ruleUnitExecutor, TestRuleUnit.class)).isSameAs(testRuleUnit);
        verifyZeroInteractions(ruleUnitExecutor);
    }

    @Test
    public void registerUnit() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{}, BigDecimal.ZERO);
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);

        assertThat(factory.registerUnit(ruleUnitExecutor, testRuleUnit)).isSameAs(testRuleUnit);
        verifyZeroInteractions(ruleUnitExecutor);

        assertThat(factory.getOrCreateRuleUnit(ruleUnitExecutor, TestRuleUnit.class)).isSameAs(testRuleUnit);
        verifyZeroInteractions(ruleUnitExecutor);
    }

    @Test
    public void injectUnitVariablesNoDataSourceInUnit() {
        factory.bindVariable("numberVariable", BigDecimal.ONE);
        factory.bindVariable("stringList", Collections.singletonList("test"));

        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{}, BigDecimal.ZERO);
        testRuleUnit.getStringList().add("bla");
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);

        factory.injectUnitVariables(ruleUnitExecutor, testRuleUnit);
        // Unassigned variables or numbers equal 0 should be reassigned if a variable exists.
        Assertions.assertThat(testRuleUnit.getNumber()).isEqualTo(BigDecimal.ONE);

        // Others should remain the same.
        Assertions.assertThat(testRuleUnit.bound).isFalse();
        Assertions.assertThat(testRuleUnit.getNumbersArray()).isNotNull().isEmpty();
        Assertions.assertThat(testRuleUnit.getSimpleFactList()).isNotNull().isEmpty();
        Assertions.assertThat(testRuleUnit.getStringList()).isNotNull().hasSize(1).containsExactly("bla");

        verifyZeroInteractions(ruleUnitExecutor);
    }

    @Test
    public void injectUnitVariablesDataSourceInUnit() {
        final CursoredDataSource<Object> dataSource = mock(CursoredDataSource.class);
        final RuleUnitWithDataSource testRuleUnit = new RuleUnitWithDataSource(dataSource);
        final InternalRuleUnitExecutor ruleUnitExecutor = mock(InternalRuleUnitExecutor.class);

        factory.injectUnitVariables(ruleUnitExecutor, testRuleUnit);
        verify(ruleUnitExecutor).bindDataSource(dataSource);
        verifyNoMoreInteractions(ruleUnitExecutor);
    }
}