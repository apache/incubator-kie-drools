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

public class BooleanConditionInspectorTest {

    private boolean value1;
    private boolean value2;
    private String operator1;
    private String operator2;
    private boolean conflictExpected;
    private Field field;

    public void initBooleanConditionInspectorTest(String operator1,
                                         boolean value1,
                                         String operator2,
                                         boolean value2,
                                         boolean conflictExpected) {
        this.field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedConflictTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        initBooleanConditionInspectorTest(operator1, value1, operator2, value2, conflictExpected);
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);

        assertThat(a.conflicts(b)).as(getAssertDescription(a, b, conflictExpected, "conflict")).isEqualTo(conflictExpected);
        assertThat(b.conflicts(a)).as(getAssertDescription(b, a, conflictExpected, "conflict")).isEqualTo(conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedRedundancyTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        initBooleanConditionInspectorTest(operator1, value1, operator2, value2, conflictExpected);
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);

        assertThat(a.isRedundant(b)).as(getAssertDescription(a, b, !conflictExpected, "be redundant")).isEqualTo(!conflictExpected);
        assertThat(b.isRedundant(a)).as(getAssertDescription(b, a, !conflictExpected, "be redundant")).isEqualTo(!conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedOverlapTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        initBooleanConditionInspectorTest(operator1, value1, operator2, value2, conflictExpected);
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);

        assertThat(a.overlaps(b)).as(getAssertDescription(a, b, !conflictExpected, "overlap")).isEqualTo(!conflictExpected);
        assertThat(b.overlaps(a)).as(getAssertDescription(b, a, !conflictExpected, "overlap")).isEqualTo(!conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedSubsumptionTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        initBooleanConditionInspectorTest(operator1, value1, operator2, value2, conflictExpected);
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);

        assertThat(a.subsumes(b)).as(getAssertDescription(a, b, !conflictExpected, "be subsuming")).isEqualTo(!conflictExpected);
        assertThat(b.subsumes(a)).as(getAssertDescription(b, a, !conflictExpected, "be subsuming")).isEqualTo(!conflictExpected);
    }

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"!=", true, "!=", true, false},
                {"==", true, "==", true, false},
                {"!=", true, "==", false, false},

                {"!=", true, "!=", false, true},
                {"==", true, "==", false, true},
                {"!=", true, "==", true, true}
        });
    }

    private String getAssertDescription(BooleanConditionInspector a,
                                        BooleanConditionInspector b,
                                        boolean conditionExpected,
                                        String condition) {
        return format("Expected conditions '%s' and '%s' %sto %s:",
                a.toHumanReadableString(),
                b.toHumanReadableString(),
                conditionExpected ? "" : "not ",
                condition);
    }

    private BooleanConditionInspector getCondition(boolean value,
                                                   String operator) {
        return new BooleanConditionInspector(new FieldCondition<>(field, mock(Column.class), operator, new Values<>(value),
                        new AnalyzerConfigurationMock()),
                new AnalyzerConfigurationMock());
    }
}