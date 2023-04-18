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
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.fieldCondition;

public class StringConditionInspectorSubsumptionResolverTest {

    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, redundant
                {"==", new Values<>("a"), "==", new Values<>("a"), true},
                {"!=", new Values<>("a"), "!=", new Values<>("a"), true},
                {">", new Values<>("a"), ">", new Values<>("a"), true},
                {">=", new Values<>("a"), ">=", new Values<>("a"), true},
                {"<", new Values<>("a"), "<", new Values<>("a"), true},
                {"<=", new Values<>("a"), "<=", new Values<>("a"), true},
                {"in", new Values<>("a", "b"), "in", new Values<>("a", "b"), true},
                {"not in", new Values<>("a", "b"), "not in", new Values<>("a", "b"), true},
                {"matches", new Values<>("a"), "matches", new Values<>("a"), true},
                {"soundslike", new Values<>("a"), "soundslike", new Values<>("a"), true},

                {"==", new Values<>("a"), "==", new Values<>("b"), false},
                {"==", new Values<>("a"), "!=", new Values<>("a"), false},
                {"==", new Values<>("a"), ">", new Values<>("a"), false},
                {"==", new Values<>("a"), ">=", new Values<>("a"), false},
                {"==", new Values<>("a"), "<", new Values<>("a"), false},
                {"==", new Values<>("a"), "<=", new Values<>("a"), false},
                {"==", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {"==", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"==", new Values<>("a"), "in", new Values<>("a"), true},

                {"!=", new Values<>("a"), "!=", new Values<>("b"), false},
                {"!=", new Values<>("a"), ">", new Values<>("a"), false},
                {"!=", new Values<>("a"), ">=", new Values<>("a"), false},
                {"!=", new Values<>("a"), "<", new Values<>("a"), false},
                {"!=", new Values<>("a"), "<=", new Values<>("a"), false},
                {"!=", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {"!=", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"!=", new Values<>("a"), "not in", new Values<>("a"), true},

                {">", new Values<>("a"), ">", new Values<>("b"), false},
                {">", new Values<>("a"), ">=", new Values<>("a"), false},
                {">", new Values<>("a"), "<", new Values<>("a"), false},
                {">", new Values<>("a"), "<=", new Values<>("a"), false},
                {">", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {">", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {">=", new Values<>("a"), ">=", new Values<>("b"), false},
                {">=", new Values<>("a"), "<", new Values<>("a"), false},
                {">=", new Values<>("a"), "<=", new Values<>("a"), false},
                {">=", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {">=", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"<", new Values<>("a"), "<", new Values<>("b"), false},
                {"<", new Values<>("a"), "<=", new Values<>("a"), false},
                {"<", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {"<", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"<=", new Values<>("a"), "<=", new Values<>("b"), false},
                {"<=", new Values<>("a"), "in", new Values<>("a", "b"), false},
                {"<=", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"in", new Values<>("a"), "in", new Values<>("b"), false},
                {"in", new Values<>("a"), "not in", new Values<>("a", "b"), false},

                {"in", new Values<>("a", "b"), "in", new Values<>("b", "a"), true},

                {"not in", new Values<>("a"), "not in", new Values<>("b"), false},

                {"not in", new Values<>("a", "b"), "not in", new Values<>("b", "a"), true},

                {">", new Values<>("a"), ">=", new Values<>("b"), false},
                {"<", new Values<>("b"), "<=", new Values<>("a"), false},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(String operator1, Values value1, String operator2, Values value2, boolean redundancyExpected) {
        this.field = mock(Field.class);
        StringConditionInspector a = getCondition(value1, operator1);
        StringConditionInspector b = getCondition(value2, operator2);

        assertThat(a.isRedundant(b)).as(getAssertDescription(a,
                b,
                redundancyExpected)).isEqualTo(redundancyExpected);
        assertThat(b.isRedundant(a)).as(getAssertDescription(b,
                a,
                redundancyExpected)).isEqualTo(redundancyExpected);
    }

    private String getAssertDescription(StringConditionInspector a,
                                        StringConditionInspector b,
                                        boolean conflictExpected) {
        return format("Expected conditions '%s' and '%s' %sto be redundant:",
                a.toHumanReadableString(),
                b.toHumanReadableString(),
                conflictExpected ? "" : "not ");
    }

    private StringConditionInspector getCondition(Values values, String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new StringConditionInspector(fieldCondition(field, values, operator), configurationMock);
    }
}