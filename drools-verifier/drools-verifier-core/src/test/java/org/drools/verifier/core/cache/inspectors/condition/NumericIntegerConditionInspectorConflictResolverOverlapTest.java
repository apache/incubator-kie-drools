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
public class NumericIntegerConditionInspectorConflictResolverOverlapTest {

    private final Integer value1;
    private final Integer value2;
    private final String operator1;
    private final String operator2;
    private final boolean overlapExpected;
    private final Field field;

    public NumericIntegerConditionInspectorConflictResolverOverlapTest(String operator1,
                                                                       Integer value1,
                                                                       String operator2,
                                                                       Integer value2,
                                                                       boolean overlapExpected) {
        this.field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.overlapExpected = overlapExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, overlaps
                {"==", 0, "==", 0, true},
                {"!=", 0, "!=", 0, true},
                {">", 0, ">", 0, true},
                {">=", 0, ">=", 0, true},
                {"<", 0, "<", 0, true},
                {"<=", 0, "<=", 0, true},

                {"==", 0, "==", 1, false},
                {"==", 0, "!=", 0, false},
                {"==", 0, ">", 0, false},
                {"==", 0, ">", 10, false},
                {"==", 0, ">=", 1, false},
                {"==", 0, ">=", 10, false},
                {"==", 0, "<", 0, false},
                {"==", 0, "<", -10, false},
                {"==", 0, "<=", -1, false},
                {"==", 0, "<=", -10, false},

                {"==", 0, "!=", 1, true},
                {"==", 0, ">", -1, true},
                {"==", 0, ">", -10, true},
                {"==", 0, ">=", 0, true},
                {"==", 0, ">=", -10, true},
                {"==", 0, "<", 1, true},
                {"==", 0, "<", 10, true},
                {"==", 0, "<=", 0, true},
                {"==", 0, "<=", 10, true},

                {"!=", 0, "!=", 1, true},
                {"!=", 0, ">", -1, true},
                {"!=", 0, ">", -10, true},
                {"!=", 0, ">=", 0, true},
                {"!=", 0, ">=", -10, true},
                {"!=", 0, "<", 1, true},
                {"!=", 0, "<", 10, true},
                {"!=", 0, "<=", 0, true},
                {"!=", 0, "<=", 10, true},

                {">", 0, "<", 1, true},
                {">", 0, "<", -10, false},
                {">", 0, "<=", 0, false},
                {">", 0, "<=", -10, false},

                {">", 0, ">", -1, true},
                {">", 0, ">", -10, true},
                {">", 0, ">=", 0, true},
                {">", 0, ">=", 1, true},
                {">", 0, ">=", -10, true},
                {">", 0, "<", 2, true},
                {">", 0, "<", 10, true},
                {">", 0, "<=", 1, true},
                {">", 0, "<=", 10, true},

                {">=", 0, "<", 0, false},
                {">=", 0, "<", -10, false},
                {">=", 0, "<=", -1, false},
                {">=", 0, "<=", -10, false},

                {">=", 0, ">=", 1, true},
                {">=", 0, ">=", -10, true},
                {">=", 0, "<", 1, true},
                {">=", 0, "<", 10, true},
                {">=", 0, "<=", 0, true},
                {">=", 0, "<=", 10, true},

                {"<", 0, "<", 1, true},
                {"<", 0, "<", 10, true},
                {"<", 0, "<=", -1, true},
                {"<", 0, "<=", 0, true},
                {"<", 0, "<=", 10, true},

                {"<=", 0, "<=", -1, true},
                {"<=", 0, "<=", 10, true},
        });
    }

    @Test
    public void parametrizedOverlapTest() {
        NumericIntegerConditionInspector a = getCondition(value1,
                                                          operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                                                          operator2);

        assertEquals(getAssertDescription(a,
                                          b,
                                          overlapExpected,
                                          "overlap"),
                     overlapExpected,
                     a.overlaps(b));
        assertEquals(getAssertDescription(b,
                                          a,
                                          overlapExpected,
                                          "overlap"),
                     overlapExpected,
                     b.overlaps(a));
    }

    @Test
    public void parametrizedConflictTest() {
        NumericIntegerConditionInspector a = getCondition(value1,
                                                          operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                                                          operator2);

        assertEquals(getAssertDescription(a,
                                          b,
                                          !overlapExpected,
                                          "conflict"),
                     !overlapExpected,
                     a.conflicts(b));
        assertEquals(getAssertDescription(b,
                                          a,
                                          !overlapExpected,
                                          "conflict"),
                     !overlapExpected,
                     b.conflicts(a));
    }

    private String getAssertDescription(final NumericIntegerConditionInspector a,
                                        final NumericIntegerConditionInspector b,
                                        final boolean conflictExpected,
                                        final String condition) {
        return format("Expected condition '%s' %sto %s with condition '%s':",
                      a.toHumanReadableString(),
                      conflictExpected ? "" : "not ",
                      condition,
                      b.toHumanReadableString());
    }

    private NumericIntegerConditionInspector getCondition(final int value,
                                                          final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new NumericIntegerConditionInspector(new FieldCondition<>(field,
                                                                         mock(Column.class),
                                                                         operator,
                                                                         new Values<>(value),
                                                                         configurationMock),
                                                    configurationMock);
    }
}