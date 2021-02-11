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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.drools.ruleunit.executor.RuleUnitSessionImpl;
import org.drools.ruleunit.impl.RuleUnitDescriptionImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RuleUnitDescriptionTest {

    private RuleUnitDescriptionImpl ruleUnitDescr;

    @Before
    public void prepareRuleUnitDescr() {
        ruleUnitDescr = new RuleUnitDescriptionImpl(null, TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitClass() {
        Assertions.assertThat(ruleUnitDescr.getRuleUnitClass()).isEqualTo(TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitName() {
        Assertions.assertThat(ruleUnitDescr.getRuleUnitName()).isEqualTo(TestRuleUnit.class.getName());
    }

    @Test
    public void getEntryPointId() {
        final Optional<EntryPointId> entryPointId = ruleUnitDescr.getEntryPointId("nonexisting");
        Assertions.assertThat(entryPointId).isNotPresent();

        assertEntryPointIdExists("numbersArray");
        assertEntryPointIdExists("number");
        assertEntryPointIdExists("stringList");
        assertEntryPointIdExists("simpleFactList");
    }

    @Test
    public void getDatasourceType() {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType("nonexisting");
        Assertions.assertThat(dataSourceType).isNotPresent();

        assertDataSourceType("number", BigDecimal.class);
        assertDataSourceType("numbersArray", Integer.class);
        assertDataSourceType("stringList", String.class);
        assertDataSourceType("simpleFactList", SimpleFact.class);
    }

    @Test
    public void getVarType() {
        final Optional<Class<?>> varType = ruleUnitDescr.getVarType("nonexisting");
        Assertions.assertThat(varType).isNotPresent();

        assertVarType("number", BigDecimal.class);
        assertVarType("numbersArray", Integer[].class);
        assertVarType("stringList", List.class);
        assertVarType("simpleFactList", List.class);
    }

    @Test
    public void hasVar() {
        Assertions.assertThat(ruleUnitDescr.hasVar("nonexisting")).isFalse();
        Assertions.assertThat(ruleUnitDescr.hasVar("numbers")).isFalse();
        Assertions.assertThat(ruleUnitDescr.hasVar("number")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasVar("numbersArray")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasVar("stringList")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasVar("simpleFactList")).isTrue();
    }

    @Test
    public void getUnitVars() {
        final Collection<String> unitVars = ruleUnitDescr.getUnitVars();
        Assertions.assertThat(unitVars).isNotEmpty();
        Assertions.assertThat(unitVars).hasSize(5);
        Assertions.assertThat(unitVars).containsExactlyInAnyOrder("bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void getUnitVarAccessors() {
        final Collection<? extends RuleUnitVariable> unitVarAccessors = ruleUnitDescr.getUnitVarDeclarations();
        Assertions.assertThat(unitVarAccessors).isNotEmpty();
        Assertions.assertThat(unitVarAccessors).hasSize(5);
        Assertions.assertThat(unitVarAccessors)
                .extracting("name", String.class)
                .containsExactlyInAnyOrder("bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void hasDataSource() {
        Assertions.assertThat(ruleUnitDescr.hasDataSource("nonexisting")).isFalse();
        Assertions.assertThat(ruleUnitDescr.hasDataSource("numbers")).isFalse();
        Assertions.assertThat(ruleUnitDescr.hasDataSource("number")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasDataSource("numbersArray")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasDataSource("stringList")).isTrue();
        Assertions.assertThat(ruleUnitDescr.hasDataSource("simpleFactList")).isTrue();
    }

    @Test
    public void bindDataSourcesNonexistingEntryPoints() {
        final StatefulKnowledgeSessionImpl sessionImpl = mock(StatefulKnowledgeSessionImpl.class);
        when(sessionImpl.getEntryPoint("number")).thenReturn(null);
        when(sessionImpl.getEntryPoint("numbersArray")).thenReturn(null);
        when(sessionImpl.getEntryPoint("stringList")).thenReturn(null);
        when(sessionImpl.getEntryPoint("simpleFactList")).thenReturn(null);

        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);

        ruleUnitDescr.bindDataSources(new RuleUnitSessionImpl(null, sessionImpl), testRuleUnit);
        verify(sessionImpl, never()).insert(anyObject());
        verify(sessionImpl, never()).insert(anyObject(), anyBoolean(), anyObject(), anyObject());
    }

    @Test
    public void bindDataSources() {
        final WorkingMemoryEntryPoint numberEntryPoint = mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint numbersArrayEntryPoint = mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint stringListEntryPoint = mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint simpleFactListEntryPoint = mock(WorkingMemoryEntryPoint.class);

        final StatefulKnowledgeSessionImpl sessionImpl = mock(StatefulKnowledgeSessionImpl.class);
        when(sessionImpl.getEntryPoint(TestRuleUnit.class.getCanonicalName() + ".number")).thenReturn(numberEntryPoint);
        when(sessionImpl.getEntryPoint(TestRuleUnit.class.getCanonicalName() + ".numbersArray")).thenReturn(numbersArrayEntryPoint);
        when(sessionImpl.getEntryPoint(TestRuleUnit.class.getCanonicalName() + ".stringList")).thenReturn(stringListEntryPoint);
        when(sessionImpl.getEntryPoint(TestRuleUnit.class.getCanonicalName() + ".simpleFactList")).thenReturn(simpleFactListEntryPoint);

        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);

        ruleUnitDescr.bindDataSources(new RuleUnitSessionImpl(null, sessionImpl), testRuleUnit);

        verify(numberEntryPoint).insert(BigDecimal.TEN);

        verify(numbersArrayEntryPoint).insert(1);
        verify(numbersArrayEntryPoint).insert(2);
        verify(numbersArrayEntryPoint).insert(5);

        verify(simpleFactListEntryPoint).insert(simpleFact);
    }

    @Test
    public void unbindDataSources() {
        final WorkingMemoryEntryPoint boundPropEntryPoint = mock(WorkingMemoryEntryPoint.class);

        final StatefulKnowledgeSessionImpl sessionImpl = mock(StatefulKnowledgeSessionImpl.class);
        // This is a little hack, see description in TestRuleUnit class.
        when(sessionImpl.getEntryPoint(TestRuleUnit.class.getCanonicalName() + ".bound")).thenReturn(boundPropEntryPoint);

        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);

        Assertions.assertThat(testRuleUnit.bound).isFalse();

        RuleUnitSessionImpl ruSession = new RuleUnitSessionImpl(null, sessionImpl);
        // Bind calls getBound() which switches the bound flag to true
        ruleUnitDescr.bindDataSources(ruSession, testRuleUnit);
        verify(boundPropEntryPoint).insert(true);

        // Unbind calls getBound() which switches the bound flag to false
        ruleUnitDescr.unbindDataSources(ruSession, testRuleUnit);
        // We have to observe directly. By calling getBound, it will get switched again.
        Assertions.assertThat(testRuleUnit.bound).isFalse();
    }

    @Test
    public void getValue() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);

        Object value = ruleUnitDescr.getValue(testRuleUnit, "nonexisting");
        Assertions.assertThat(value).isNull();

        value = ruleUnitDescr.getValue(testRuleUnit, "number");
        Assertions.assertThat(value).isInstanceOf(BigDecimal.class);
        Assertions.assertThat(value).isEqualTo(BigDecimal.TEN);

        value = ruleUnitDescr.getValue(testRuleUnit, "numbersArray");
        Assertions.assertThat(value).isInstanceOf(Integer[].class);
        Assertions.assertThat(value).isEqualTo(new Integer[]{1, 2, 5});

        value = ruleUnitDescr.getValue(testRuleUnit, "simpleFactList");
        Assertions.assertThat(value).isInstanceOfSatisfying(List.class, list -> Assertions.assertThat(list).containsExactly(simpleFact));
    }

    private void assertEntryPointIdExists(final String entryPointIdName) {
        final Optional<EntryPointId> entryPointId = ruleUnitDescr.getEntryPointId(entryPointIdName);
        Assert.assertTrue(entryPointId.isPresent());
        Assert.assertEquals(TestRuleUnit.class.getName() + "." + entryPointIdName, entryPointId.get().getEntryPointId());
    }

    private void assertDataSourceType(final String dataSourceName, final Class<?> expectedType) {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType(dataSourceName);
        Assert.assertTrue(dataSourceType.isPresent());
        Assert.assertEquals(expectedType, dataSourceType.get());
    }

    private void assertVarType(final String varName, final Class<?> expectedType) {
        final Optional<Class<?>> variableTable = ruleUnitDescr.getVarType(varName);
        Assert.assertTrue(variableTable.isPresent());
        Assert.assertEquals(expectedType, variableTable.get());
    }
}