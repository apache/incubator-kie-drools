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

import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getStringCondition;

@ExtendWith(MockitoExtension.class)
public class StringConditionInspectorConflictResolverOverlapTest {

	@Mock
    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // matches and soundslike are probably not doable...
                // op1, val1, op2, val2, conflicts, overlaps
                {"==", new Values<>("a"), "==", new Values<>("a"), false, true},
                {"!=", new Values<>("a"), "!=", new Values<>("a"), false, true},
                {">", new Values<>("a"), ">", new Values<>("a"), false, false},
                {">=", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {"<", new Values<>("a"), "<", new Values<>("a"), false, false},
                {"<=", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"in", new Values<>("a", "b"), "in", new Values<>("a", "b"), false, true},
                {"not in", new Values<>("a", "b"), "not in", new Values<>("a", "b"), false, true},
                {"matches", new Values<>("a"), "matches", new Values<>("a"), false, true},
                {"soundslike", new Values<>("a"), "soundslike", new Values<>("a"), false, true},

                {"==", new Values<>("a"), "!=", new Values<>("b"), false, true},
                {"==", new Values<>("a"), ">", new Values<>(" "), false, false},
                {"==", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "<", new Values<>("b"), false, false},
                {"==", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "in", new Values<>("a", "b"), false, true},
                {"==", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, true},
                {"==", new Values<>("a"), "matches", new Values<>("a"), false, true},
                {"==", new Values<>("a"), "soundslike", new Values<>("a"), false, true},

                {"==", new Values<>("a"), "!=", new Values<>("a"), true, false},
                {"==", new Values<>("a"), ">", new Values<>("a"), false, false},
                {"==", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "<", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {"==", new Values<>("a"), "in", new Values<>("b", "c", "d"), true, false},
                {"==", new Values<>("a"), "not in", new Values<>("a", "b"), true, false},
                {"==", new Values<>("a"), "matches", new Values<>("a"), false, true},
                {"==", new Values<>("a"), "soundslike", new Values<>("a"), false, true},

                {"!=", new Values<>("a"), "!=", new Values<>("a"), false, true},
                {"!=", new Values<>("a"), ">", new Values<>(" "), false, false},
                {"!=", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {"!=", new Values<>("a"), "<", new Values<>("a"), false, false},
                {"!=", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"!=", new Values<>("a"), "in", new Values<>("a", "b"), false, true},
                {"!=", new Values<>("a"), "in", new Values<>("b", "c", "d"), false, true},
                {"!=", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, true},
                {"!=", new Values<>("a"), "matches", new Values<>("a"), true, false},
                {"!=", new Values<>("a"), "soundslike", new Values<>("a"), true, false},

                {"!=", new Values<>("a"), "in", new Values<>("a"), true, false},
                {"!=", new Values<>("a"), "matches", new Values<>("a"), true, false},
                {"!=", new Values<>("a"), "soundslike", new Values<>("a"), true, false},

                {">", new Values<>("a"), ">", new Values<>("a"), false, false},
                {">", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {">", new Values<>("a"), "<", new Values<>("c"), false, false},
                {">", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {">", new Values<>("a"), "in", new Values<>("a", "b"), false, false},
                {">", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, false},
                {">", new Values<>("a"), "matches", new Values<>("a"), false, false},
                {">", new Values<>("a"), "soundslike", new Values<>("a"), false, false},

                {">", new Values<>("a"), "<", new Values<>("a"), false, false},
                {">", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {">", new Values<>("a"), "in", new Values<>("0", "1", "A", "B", "a"), false, false},
                {">", new Values<>("a"), "matches", new Values<>("a"), false, false},
                {">", new Values<>("a"), "soundslike", new Values<>(""), false, false},

                {">=", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {">=", new Values<>("a"), "<", new Values<>("a"), false, false},
                {">=", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {">=", new Values<>("a"), "in", new Values<>("a"), false, false},
                {">=", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, false},
                {">=", new Values<>("a"), "matches", new Values<>("a"), false, false},
                {">=", new Values<>("a"), "soundslike", new Values<>("a"), false, false},

                {">=", new Values<>("a"), "<", new Values<>(" "), false, false},
                {">=", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {">=", new Values<>("a"), "in", new Values<>("0", "1", "A", "B"), false, false},
                {">=", new Values<>("a"), "matches", new Values<>("A"), false, false},
                {">=", new Values<>("a"), "soundslike", new Values<>(""), false, false},

                {"<", new Values<>("a"), "<", new Values<>("a"), false, false},
                {"<", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"<", new Values<>("a"), "in", new Values<>("A", "B", "a", "b"), false, false},
                {"<", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, false},
                {"<", new Values<>("a"), "matches", new Values<>("A"), false, false},
                {"<", new Values<>("a"), "soundslike", new Values<>(""), false, false},

                {"<", new Values<>("a"), "in", new Values<>("b", "c", "d"), false, false},
                {"<", new Values<>("a"), "matches", new Values<>("a"), false, false},
                {"<", new Values<>("a"), "soundslike", new Values<>("a"), false, false},

                {"<=", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"<=", new Values<>("a"), "in", new Values<>("A", "B", "a", "b"), false, false},
                {"<=", new Values<>("a"), "not in", new Values<>("b", "c", "d"), false, false},
                {"<=", new Values<>("a"), "matches", new Values<>("A"), false, false},
                {"<=", new Values<>("a"), "soundslike", new Values<>(""), false, false},

                {"<=", new Values<>("a"), "in", new Values<>("b", "c", "d"), false, false},
                {"<=", new Values<>("a"), "matches", new Values<>("a"), false, false},
                {"<=", new Values<>("a"), "soundslike", new Values<>("a"), false, false},

                {"in", new Values<>("a", "b"), "in", new Values<>("b", "c", "d"), false, true},
                {"in", new Values<>("a", "b"), "not in", new Values<>("b", "c", "d"), false, true},
                {"in", new Values<>("a", "b"), "matches", new Values<>("a"), false, true},
                {"in", new Values<>("a", "b"), "soundslike", new Values<>("a"), false, true},

                {"in", new Values<>("a", "b"), "in", new Values<>("c", "d"), true, false},
                {"in", new Values<>("a", "b"), "not in", new Values<>("a", "b"), true, false},
                {"in", new Values<>("a", "b"), "matches", new Values<>("c"), true, false},
                {"in", new Values<>("a", "b"), "soundslike", new Values<>("c"), true, false},

                {"not in", new Values<>("a", "b"), "matches", new Values<>("c"), false, true},
                {"not in", new Values<>("a", "b"), "soundslike", new Values<>("c"), false, true},

                {"not in", new Values<>("a", "b"), "matches", new Values<>("a"), true, false},

                {"matches", new Values<>("a"), "soundslike", new Values<>("a"), false, true},

                {"matches", new Values<>("a"), "soundslike", new Values<>("a"), false, true},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedConflictTest(String operator1, Values value1, String operator2, Values value2, boolean conflictExpected, boolean overlapExpected) {
        StringConditionInspector a = getStringCondition(field, value1, operator1);
        StringConditionInspector b = getStringCondition(field, value2, operator2);

        boolean conflicts = a.conflicts(b);
        assertThat(conflicts).as(getAssertDescription(a,
                b,
                conflictExpected,
                "conflict")).isEqualTo(conflictExpected);
        boolean conflicts1 = b.conflicts(a);
        assertThat(conflicts1).as(getAssertDescription(b,
                a,
                conflictExpected,
                "conflict")).isEqualTo(conflictExpected);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedOverlapTest(String operator1, Values value1, String operator2, Values value2, boolean conflictExpected, boolean overlapExpected) {
        StringConditionInspector a = getStringCondition(field, value1, operator1);
        StringConditionInspector b = getStringCondition(field, value2, operator2);

        boolean overlaps = a.overlaps(b);
        assertThat(overlaps).as(getAssertDescription(a,
                b,
                overlapExpected,
                "overlap")).isEqualTo(overlapExpected);
        boolean overlaps1 = b.overlaps(a);
        assertThat(overlaps1).as(getAssertDescription(b,
                a,
                overlapExpected,
                "overlap")).isEqualTo(overlapExpected);
    }

    private String getAssertDescription(StringConditionInspector a,
                                        StringConditionInspector b,
                                        boolean conflictExpected,
                                        String condition) {
        return format("Expected condition '%s' %sto %s with condition '%s':",
                a.toHumanReadableString(),
                conflictExpected ? "" : "not ",
                condition,
                b.toHumanReadableString());
    }
    
    
}