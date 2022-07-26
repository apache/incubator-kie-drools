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

public class NumericIntegerConditionInspectorSubsumptionTest {

    private Integer value1;
    private Integer value2;
    private String operator1;
    private String operator2;
    private boolean aSubsumesB;
    private boolean bSubsumesA;
    private Field field;

    public void initNumericIntegerConditionInspectorSubsumptionTest(String operator1,
                                                           Integer value1,
                                                           String operator2,
                                                           Integer value2,
                                                           boolean aSubsumesB,
                                                           boolean bSubsumesA) {
        this.field = mock(Field.class);
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
                {"==", 0, "==", 0, true, true},
                {"!=", 0, "!=", 0, true, true},
                {">", 0, ">", 0, true, true},
                {">=", 0, ">=", 0, true, true},
                {"<", 0, "<", 0, true, true},
                {"<=", 0, "<=", 0, true, true},

                {"==", 0, "==", 1, false, false},
                {"==", 0, "!=", 0, false, false},
                {"==", 0, ">", 0, false, false},
                {"==", 0, ">", 10, false, false},
                {"==", 0, ">=", 1, false, false},
                {"==", 0, ">=", 10, false, false},
                {"==", 0, "<", 0, false, false},
                {"==", 0, "<", -10, false, false},
                {"==", 0, "<=", -1, false, false},
                {"==", 0, "<=", -10, false, false},

                {"==", 0, "!=", 1, true, false},
                {"==", 0, ">", -1, false, true},
                {"==", 0, ">", -10, false, true},
                {"==", 0, ">=", 0, false, true},
                {"==", 0, ">=", -10, false, true},
                {"==", 0, "<", 1, false, true},
                {"==", 0, "<", 10, false, true},
                {"==", 0, "<=", 0, false, true},
                {"==", 0, "<=", 10, false, true},

                {"!=", 0, "!=", 1, false, false},
                {"!=", 0, ">", -1, false, false},
                {"!=", 0, ">", -10, false, false},
                {"!=", 0, ">=", 0, false, false},
                {"!=", 0, ">=", -10, false, false},
                {"!=", 0, "<", 1, false, false},
                {"!=", 0, "<", 10, false, false},
                {"!=", 0, "<=", 0, false, false},
                {"!=", 0, "<=", 10, false, false},

                {"!=", 0, ">", 0, true, false},
                {"!=", 0, ">", 10, true, false},
                {"!=", 0, ">=", 1, true, false},
                {"!=", 0, ">=", 10, true, false},
                {"!=", 0, "<", 0, true, false},
                {"!=", 0, "<", -10, true, false},
                {"!=", 0, "<=", -1, true, false},
                {"!=", 0, "<=", -10, true, false},

                {">", 0, "<", 1, false, false},
                {">", 0, "<", -10, false, false},
                {">", 0, "<", 10, false, false},
                {">", 0, "<=", 0, false, false},
                {">", 0, "<=", -10, false, false},
                {">", 0, "<=", 10, false, false},

                {">", 0, ">", 1, true, false},
                {">", 0, ">", 10, true, false},
                {">", 0, ">=", 0, false, true},
                {">", 0, ">=", 10, true, false},

                {">=", 0, "<", 0, false, false},
                {">=", 0, "<", -10, false, false},
                {">=", 0, "<", 10, false, false},
                {">=", 0, "<=", -1, false, false},
                {">=", 0, "<=", -10, false, false},
                {">=", 0, "<=", 10, false, false},

                {">=", 0, ">=", 1, true, false},
                {">=", 0, ">=", 10, true, false},

                {"<", 0, "<", 1, false, true},
                {"<", 0, "<", 10, false, true},
                {"<", 0, "<=", 0, false, true},
                {"<", 0, "<=", 10, false, true},

                {"<=", 0, "<=", 1, false, true},
                {"<=", 0, "<=", 10, false, true},

                // integer specific
                {">", 0, ">=", 1, true, true},
                {"<", 0, "<=", -1, true, true},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testASubsumesB(String operator1, Integer value1, String operator2, Integer value2, boolean aSubsumesB, boolean bSubsumesA) {
        initNumericIntegerConditionInspectorSubsumptionTest(operator1, value1, operator2, value2, aSubsumesB, bSubsumesA);
        NumericIntegerConditionInspector a = getCondition(value1,
                operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                operator2);

        assertThat(a.subsumes(b)).as(getAssertDescription(a,
                b,
                aSubsumesB)).isEqualTo(aSubsumesB);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testBSubsumesA(String operator1, Integer value1, String operator2, Integer value2, boolean aSubsumesB, boolean bSubsumesA) {
        initNumericIntegerConditionInspectorSubsumptionTest(operator1, value1, operator2, value2, aSubsumesB, bSubsumesA);
        NumericIntegerConditionInspector a = getCondition(value1,
                operator1);
        NumericIntegerConditionInspector b = getCondition(value2,
                operator2);

        assertThat(b.subsumes(a)).as(getAssertDescription(b,
                a,
                bSubsumesA)).isEqualTo(bSubsumesA);
    }

    private String getAssertDescription(NumericIntegerConditionInspector a,
                                        NumericIntegerConditionInspector b,
                                        boolean subsumptionExpected) {
        return format("Expected condition '%s' %sto subsume condition '%s':",
                a.toHumanReadableString(),
                subsumptionExpected ? "" : "not ",
                b.toHumanReadableString());
    }

    private NumericIntegerConditionInspector getCondition(int value,
                                                          String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new NumericIntegerConditionInspector(new FieldCondition<>(field,
                        mock(Column.class),
                        operator,
                        new Values<>(value),
                        configurationMock),
                configurationMock);
    }
}