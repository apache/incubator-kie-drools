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

package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NumericIntegerConditionInspectorSubsumptionResolverTest {

    private Integer value1;
    private Integer value2;
    private String operator1;
    private String operator2;
    private boolean redundancyExpected;
    private Field field;

    public void initNumericIntegerConditionInspectorSubsumptionResolverTest(String operator1,
                                                                   Integer value1,
                                                                   String operator2,
                                                                   Integer value2,
                                                                   boolean redundancyExpected) {
        this.field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.redundancyExpected = redundancyExpected;
    }

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, redundant
                {"==", 0, "==", 0, true},
                {"!=", 0, "!=", 0, true},
                {">", 0, ">", 0, true},
                {">=", 0, ">=", 0, true},
                {"<", 0, "<", 0, true},
                {"<=", 0, "<=", 0, true},

                {"==", 0, "==", 1, false},
                {"!=", 0, "!=", 1, false},
                {">", 0, ">", 1, false},
                {">=", 0, ">=", 1, false},
                {"<", 0, "<", 1, false},
                {"<=", 0, "<=", 1, false},

                {"==", 0, "!=", 0, false},
                {"==", 0, ">", 0, false},
                {"==", 0, ">=", 0, false},
                {"==", 0, "<", 0, false},
                {"==", 0, "<=", 0, false},

                {"!=", 0, ">", 0, false},
                {"!=", 0, ">=", 0, false},
                {"!=", 0, "<", 0, false},
                {"!=", 0, "<=", 0, false},

                {">", 0, ">=", 0, false},
                {">", 0, "<", 0, false},
                {">", 0, "<=", 0, false},

                {">=", 0, "<", 0, false},
                {">=", 0, "<=", 0, false},

                {"<", 0, "<=", 0, false},

                {"==", 0, "!=", 1, false},
                {"==", 0, ">", 1, false},
                {"==", 0, ">=", 1, false},
                {"==", 0, "<", 1, false},
                {"==", 0, "<=", 1, false},

                {"!=", 0, ">", 1, false},
                {"!=", 0, ">=", 1, false},
                {"!=", 0, "<", 1, false},
                {"!=", 0, "<=", 1, false},

                {">", 0, ">=", 1, true},
                {">", 0, "<", 1, false},
                {">", 0, "<=", 1, false},

                {">=", 0, "<", 1, false},
                {">=", 0, "<=", 1, false},

                {"<", 0, "<=", 1, false},

                // integer specific
                {">", 0, ">=", 1, true},
                {"<", 1, "<=", 0, true},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(String operator1, Integer value1, String operator2, Integer value2, boolean redundancyExpected) {
        initNumericIntegerConditionInspectorSubsumptionResolverTest(operator1, value1, operator2, value2, redundancyExpected);
        NumericIntegerConditionInspector a = getCondition(value1,
                operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                operator2);

        assertThat(a.isRedundant(b)).as(getAssertDescription(a,
                b,
                redundancyExpected)).isEqualTo(redundancyExpected);
        assertThat(b.isRedundant(a)).as(getAssertDescription(b,
                a,
                redundancyExpected)).isEqualTo(redundancyExpected);
    }

    private String getAssertDescription(final NumericIntegerConditionInspector a,
                                        final NumericIntegerConditionInspector b,
                                        final boolean conflictExpected) {
        return format("Expected conditions '%s' and '%s' %sto be redundant:",
                a.toHumanReadableString(),
                b.toHumanReadableString(),
                conflictExpected ? "" : "not ");
    }

    private NumericIntegerConditionInspector getCondition(final int value,
                                                          final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new NumericIntegerConditionInspector(new FieldCondition<Integer>(field,
                        mock(Column.class),
                        operator,
                        new Values(value),
                        configurationMock),
                configurationMock);
    }
}