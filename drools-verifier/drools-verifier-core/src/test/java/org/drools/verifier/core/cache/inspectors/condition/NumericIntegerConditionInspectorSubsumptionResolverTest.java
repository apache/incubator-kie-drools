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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class NumericIntegerConditionInspectorSubsumptionResolverTest {

    private final Integer value1;
    private final Integer value2;
    private final String operator1;
    private final String operator2;
    private final boolean redundancyExpected;
    private final Field field;

    public NumericIntegerConditionInspectorSubsumptionResolverTest(String operator1,
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

    @Parameters
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

    @Test
    public void parametrizedTest() {
        NumericIntegerConditionInspector a = getCondition(value1,
                                                          operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                                                          operator2);

        assertEquals(getAssertDescription(a,
                                          b,
                                          redundancyExpected),
                     redundancyExpected,
                     a.isRedundant(b));
        assertEquals(getAssertDescription(b,
                                          a,
                                          redundancyExpected),
                     redundancyExpected,
                     b.isRedundant(a));
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