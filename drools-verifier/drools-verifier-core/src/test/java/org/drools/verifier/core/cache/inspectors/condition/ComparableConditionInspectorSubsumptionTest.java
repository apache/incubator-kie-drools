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

public class ComparableConditionInspectorSubsumptionTest {

    private Comparable value1;
    private Comparable value2;
    private String operator1;
    private String operator2;
    private boolean aSubsumesB;
    private boolean bSubsumesA;
    private Field field;

    public void initComparableConditionInspectorSubsumptionTest(final String operator1,
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

    @MethodSource("testData")
    @ParameterizedTest
    void testASubsumesB(final String operator1, final Comparable value1, final String operator2, final Comparable value2, final boolean aSubsumesB, final boolean bSubsumesA) {
        initComparableConditionInspectorSubsumptionTest(operator1, value1, operator2, value2, aSubsumesB, bSubsumesA);
        final ComparableConditionInspector a = getCondition(value1,
                operator1);
        final ComparableConditionInspector b = getCondition(value2,
                operator2);

        assertThat(a.subsumes(b)).as(getAssertDescription(a,
                b,
                aSubsumesB)).isEqualTo(aSubsumesB);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testBSubsumesA(final String operator1, final Comparable value1, final String operator2, final Comparable value2, final boolean aSubsumesB, final boolean bSubsumesA) {
        initComparableConditionInspectorSubsumptionTest(operator1, value1, operator2, value2, aSubsumesB, bSubsumesA);
        final ComparableConditionInspector a = getCondition(value1,
                operator1);
        final ComparableConditionInspector b = getCondition(value2,
                operator2);

        assertThat(b.subsumes(a)).as(getAssertDescription(b,
                a,
                bSubsumesA)).isEqualTo(bSubsumesA);
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