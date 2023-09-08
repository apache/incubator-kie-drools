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
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getBooleanCondition;

@ExtendWith(MockitoExtension.class)
public class BooleanConditionInspectorTest {


	@Mock
    private Field field;

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedConflictTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        BooleanConditionInspector a = getBooleanCondition(field, value1, operator1);
        BooleanConditionInspector b = getBooleanCondition(field, value2, operator2);

        assertThat(a.conflicts(b)).as(getAssertDescription(a, b, conflictExpected, "conflict")).isEqualTo(conflictExpected);
        assertThat(b.conflicts(a)).as(getAssertDescription(b, a, conflictExpected, "conflict")).isEqualTo(conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedRedundancyTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        BooleanConditionInspector a = getBooleanCondition(field, value1, operator1);
        BooleanConditionInspector b = getBooleanCondition(field, value2, operator2);

        assertThat(a.isRedundant(b)).as(getAssertDescription(a, b, !conflictExpected, "be redundant")).isEqualTo(!conflictExpected);
        assertThat(b.isRedundant(a)).as(getAssertDescription(b, a, !conflictExpected, "be redundant")).isEqualTo(!conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedOverlapTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        BooleanConditionInspector a = getBooleanCondition(field, value1, operator1);
        BooleanConditionInspector b = getBooleanCondition(field, value2, operator2);

        assertThat(a.overlaps(b)).as(getAssertDescription(a, b, !conflictExpected, "overlap")).isEqualTo(!conflictExpected);
        assertThat(b.overlaps(a)).as(getAssertDescription(b, a, !conflictExpected, "overlap")).isEqualTo(!conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedSubsumptionTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        BooleanConditionInspector a = getBooleanCondition(field, value1, operator1);
        BooleanConditionInspector b = getBooleanCondition(field, value2, operator2);

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

    private String getAssertDescription(ComparableConditionInspector a,
    		ComparableConditionInspector b,
                                        boolean conditionExpected,
                                        String condition) {
        return format("Expected conditions '%s' and '%s' %sto %s:",
                a.toHumanReadableString(),
                b.toHumanReadableString(),
                conditionExpected ? "" : "not ",
                condition);
    }

}