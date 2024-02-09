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
package org.drools.ruleunits.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.ruleunits.impl.domain.SimpleFact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RuleUnitDescriptionTest {

    private ReflectiveRuleUnitDescription ruleUnitDescr;

    @BeforeEach
    public void prepareRuleUnitDescr() {
        ruleUnitDescr = new ReflectiveRuleUnitDescription(TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitClass() {
        assertThat(ruleUnitDescr.getRuleUnitName()).isEqualTo(TestRuleUnit.class.getName());
    }

    @Test
    public void getRuleUnitName() {
        assertThat(ruleUnitDescr.getRuleUnitName()).isEqualTo(TestRuleUnit.class.getName());
    }

    @Test
    public void getRuleUnitVariable() {
        assertThat(ruleUnitDescr.getVar("number")).isNotNull();
        assertThatThrownBy(() -> ruleUnitDescr.getVar("undefinedField")).isInstanceOf(UndefinedRuleUnitVariableException.class);
    }

    @Test
    public void getEntryPointId() {
        final String entryPointId = ruleUnitDescr.getEntryPointName("nonexisting");
        assertThat(entryPointId).isNotNull();

        assertEntryPointIdExists("numbersArray");
        assertEntryPointIdExists("number");
        assertEntryPointIdExists("stringList");
        assertEntryPointIdExists("simpleFactList");
    }

    @Test
    public void getDatasourceType() {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType("nonexisting");
        assertThat(dataSourceType).isNotPresent();

        assertDataSourceType("strings", String.class);
    }

    @Test
    public void getVarType() {
        final Optional<Type> varType = ruleUnitDescr.getVarType("nonexisting");
        assertThat(varType).isNotPresent();

        assertVarType("number", BigDecimal.class);
        assertVarType("numbersArray", Integer[].class);
        assertVarType("stringList", new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class };
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        assertVarType("simpleFactList", new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { SimpleFact.class };
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }

    @Test
    public void hasVar() {
        assertThat(ruleUnitDescr.hasVar("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasVar("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasVar("number")).isTrue();
        assertThat(ruleUnitDescr.hasVar("numbersArray")).isTrue();
        assertThat(ruleUnitDescr.hasVar("stringList")).isTrue();
        assertThat(ruleUnitDescr.hasVar("simpleFactList")).isTrue();
    }

    @Test
    public void getUnitVars() {
        final Collection<String> unitVars = ruleUnitDescr.getUnitVars();
        assertThat(unitVars).isNotEmpty();
        assertThat(unitVars).hasSize(6);
        assertThat(unitVars).containsExactlyInAnyOrder("strings", "bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void getUnitVarAccessors() {
        final Collection<? extends RuleUnitVariable> unitVarAccessors = ruleUnitDescr.getUnitVarDeclarations();
        assertThat(unitVarAccessors).isNotEmpty();
        assertThat(unitVarAccessors).hasSize(6);
        assertThat(unitVarAccessors)
                .extracting("name", String.class)
                .containsExactlyInAnyOrder("strings", "bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void hasDataSource() {
        assertThat(ruleUnitDescr.hasDataSource("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("strings")).isTrue();
    }

    private void assertEntryPointIdExists(final String entryPointIdName) {
        final String entryPointId = ruleUnitDescr.getEntryPointName(entryPointIdName);
        assertThat(entryPointId).isNotNull();
        assertThat(TestRuleUnit.class.getName() + "." + entryPointIdName).isEqualTo(entryPointId);
    }

    private void assertDataSourceType(final String dataSourceName, final Class<?> expectedType) {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType(dataSourceName);
        assertThat(dataSourceType).isPresent();
        assertThat(expectedType).isEqualTo(dataSourceType.get());
    }

    private void assertVarType(final String varName, final Type expectedType) {
        final Optional<Type> variableTable = ruleUnitDescr.getVarType(varName);
        assertThat(variableTable).isPresent();
        assertThat(variableTable.get()).isEqualTo(expectedType);
    }

    @Test
    public void getClockType() {
        assertThat(ruleUnitDescr.getClockType()).isEqualTo(ClockTypeOption.PSEUDO);
    }

    @Test
    public void getKieBaseOptions() {
        Collection<KieBaseOption> kieBaseOptions = ruleUnitDescr.getKieBaseOptions();
        assertThat(kieBaseOptions).contains(EventProcessingOption.STREAM);
    }
}
