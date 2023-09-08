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
package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getNumericCondition;

@ExtendWith(MockitoExtension.class)
public class NumericIntegerConditionInspectorConflictResolverOverlapTest {

	@Mock
	private Field field;

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

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedOverlapTest(String operator1, Integer value1, String operator2, Integer value2, boolean overlapExpected) {
        NumericIntegerConditionInspector a = getNumericCondition(field, value1, operator1);
        NumericIntegerConditionInspector b = getNumericCondition(field, value2, operator2);

        assertThat(a.overlaps(b)).as(getAssertDescription(a,
                b,
                overlapExpected,
                "overlap")).isEqualTo(overlapExpected);
        assertThat(b.overlaps(a)).as(getAssertDescription(b,
                a,
                overlapExpected,
                "overlap")).isEqualTo(overlapExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedConflictTest(String operator1, Integer value1, String operator2, Integer value2, boolean overlapExpected) {
        NumericIntegerConditionInspector a = getNumericCondition(field, value1, operator1);
        NumericIntegerConditionInspector b = getNumericCondition(field, value2, operator2);

        assertThat(a.conflicts(b)).as(getAssertDescription(a,
                b,
                !overlapExpected,
                "conflict")).isEqualTo(!overlapExpected);
        assertThat(b.conflicts(a)).as(getAssertDescription(b,
                a,
                !overlapExpected,
                "conflict")).isEqualTo(!overlapExpected);
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

}