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
public class ComparableConditionInspectorSubsumptionTest {

    private final Comparable value1;
    private final Comparable value2;
    private final String operator1;
    private final String operator2;
    private final boolean aSubsumesB;
    private final boolean bSubsumesA;
    private final Field field;

    public ComparableConditionInspectorSubsumptionTest(final String operator1,
                                                       final Comparable value1,
                                                       final String operator2,
                                                       final Comparable value2,
                                                       final boolean aSubsumesB,
                                                       final boolean bSubsumesA) {
        field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.aSubsumesB = aSubsumesB;
        this.bSubsumesA = bSubsumesA;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, aSubsumesB, bSubsumesA
                {"==", 0.5d, "==", 0.5d, true, true},
                {"!=", 0.5d, "!=", 0.5d, true, true},
                {">", 0.5d, ">", 0.5d, true, true},
                {">=", 0.5d, ">=", 0.5d, true, true},
                {"<", 0.5d, "<", 0.5d, true, true},
                {"<=", 0.5d, "<=", 0.5d, true, true},

                {"==", 0.5d, "==", 1.5d, false, false},
                {"==", 0.5d, "!=", 0.5d, false, false},
                {"==", 0.5d, ">", 0.5d, false, false},
                {"==", 0.5d, ">", 10.5d, false, false},
                {"==", 0.5d, ">=", 1.5d, false, false},
                {"==", 0.5d, ">=", 10.5d, false, false},
                {"==", 0.5d, "<", 0.5d, false, false},
                {"==", 0.5d, "<", -10.5d, false, false},
                {"==", 0.5d, "<=", -1.5d, false, false},
                {"==", 0.5d, "<=", -10.5d, false, false},

                {"==", 0.5d, "!=", 1.5d, true, false},
                {"==", 0.5d, ">", -1.5d, false, true},
                {"==", 0.5d, ">", -10.5d, false, true},
                {"==", 0.5d, ">=", 0.5d, false, true},
                {"==", 0.5d, ">=", -10.5d, false, true},
                {"==", 0.5d, "<", 1.5d, false, true},
                {"==", 0.5d, "<", 10.5d, false, true},
                {"==", 0.5d, "<=", 0.5d, false, true},
                {"==", 0.5d, "<=", 10.5d, false, true},

                {"!=", 0.5d, "!=", 1.5d, false, false},
                {"!=", 0.5d, ">", -1.5d, false, false},
                {"!=", 0.5d, ">", -10.5d, false, false},
                {"!=", 0.5d, ">=", 0.5d, false, false},
                {"!=", 0.5d, ">=", -10.5d, false, false},
                {"!=", 0.5d, "<", 1.5d, false, false},
                {"!=", 0.5d, "<", 10.5d, false, false},
                {"!=", 0.5d, "<=", 0.5d, false, false},
                {"!=", 0.5d, "<=", 10.5d, false, false},

                {"!=", 0.5d, ">", 0.5d, true, false},
                {"!=", 0.5d, ">", 10.5d, true, false},
                {"!=", 0.5d, ">=", 1.5d, true, false},
                {"!=", 0.5d, ">=", 10.5d, true, false},
                {"!=", 0.5d, "<", 0.5d, true, false},
                {"!=", 0.5d, "<", -10.5d, true, false},
                {"!=", 0.5d, "<=", -1.5d, true, false},
                {"!=", 0.5d, "<=", -10.5d, true, false},

                {">", 0.5d, "<", 0.5d, false, false},
                {">", 0.5d, "<", -10.5d, false, false},
                {">", 0.5d, "<", 10.5d, false, false},
                {">", 0.5d, "<=", 0.5d, false, false},
                {">", 0.5d, "<=", -10.5d, false, false},
                {">", 0.5d, "<=", 10.5d, false, false},

                {">", 0.5d, ">", 1.5d, true, false},
                {">", 0.5d, ">", 10.5d, true, false},
                {">", 0.5d, ">=", 0.5d, false, true},
                {">", 0.5d, ">=", 10.5d, true, false},

                {">=", 0.5d, "<", 0.5d, false, false},
                {">=", 0.5d, "<", -10.5d, false, false},
                {">=", 0.5d, "<", 10.5d, false, false},
                {">=", 0.5d, "<=", -1.5d, false, false},
                {">=", 0.5d, "<=", -10.5d, false, false},
                {">=", 0.5d, "<=", 10.5d, false, false},

                {">=", 0.5d, ">=", 1.5d, true, false},
                {">=", 0.5d, ">=", 10.5d, true, false},

                {"<", 0.5d, "<", 1.5d, false, true},
                {"<", 0.5d, "<", 10.5d, false, true},
                {"<", 0.5d, "<=", 0.5d, false, true},
                {"<", 0.5d, "<=", 10.5d, false, true},

                {"<=", 0.5d, "<=", 1.5d, false, true},
                {"<=", 0.5d, "<=", 10.5d, false, true},
        });
    }

    @Test
    public void testASubsumesB() {
        final ComparableConditionInspector a = getCondition(value1,
                                                            operator1);
        final ComparableConditionInspector b = getCondition(value2,
                                                            operator2);

        assertEquals(getAssertDescription(a,
                                          b,
                                          aSubsumesB),
                     aSubsumesB,
                     a.subsumes(b));
    }

    @Test
    public void testBSubsumesA() {
        final ComparableConditionInspector a = getCondition(value1,
                                                            operator1);
        final ComparableConditionInspector b = getCondition(value2,
                                                            operator2);

        assertEquals(getAssertDescription(b,
                                          a,
                                          bSubsumesA),
                     bSubsumesA,
                     b.subsumes(a));
    }

    private String getAssertDescription(ComparableConditionInspector a,
                                        ComparableConditionInspector b,
                                        boolean subsumptionExpected) {
        return format("Expected condition '%s' %sto subsume condition '%s':",
                      a.toHumanReadableString(),
                      subsumptionExpected ? "" : "not ",
                      b.toHumanReadableString());
    }

    private ComparableConditionInspector getCondition(Comparable value,
                                                      String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector(new FieldCondition(field,
                                                                   mock(Column.class),
                                                                   operator,
                                                                   new Values<>(value),
                                                                   configurationMock),
                                                configurationMock);
    }
}