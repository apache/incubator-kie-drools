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
public class StringConditionInspectorSubsumptionTest {

	@Mock
    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, aSubsumesB, bSubsumesA
                {"==", new Values<>("a"), "==", new Values<>("a"), true, true},
                {"!=", new Values<>("a"), "!=", new Values<>("a"), true, true},
                {">", new Values<>("a"), ">", new Values<>("a"), true, true},
                {">=", new Values<>("a"), ">=", new Values<>("a"), true, true},
                {"<", new Values<>("a"), "<", new Values<>("a"), true, true},
                {"<=", new Values<>("a"), "<=", new Values<>("a"), true, true},
                {"in", new Values<>("a", "b"), "in", new Values<>("a", "b"), true, true},
                {"not in", new Values<>("a", "b"), "not in", new Values<>("a", "b"), true, true},
                {"matches", new Values<>("a"), "matches", new Values<>("a"), true, true},
                {"soundslike", new Values<>("a"), "soundslike", new Values<>("a"), true, true},

                {"==", new Values<>("a"), "soundslike", new Values<>("a"), true, true},
                {"matches", new Values<>("a"), "soundslike", new Values<>("a"), true, true},
                {"==", new Values<>("a"), "==", new Values<>("b"), false, false},
                {"==", new Values<>("a"), "!=", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "!=", new Values<>("b"), false, true},
                {"==", new Values<>("a"), ">", new Values<>("a"), false, false},
                {"==", new Values<>("a"), ">", new Values<>(" "), false, false},
                {"==", new Values<>("a"), ">=", new Values<>("b"), false, false},
                {"==", new Values<>("a"), ">=", new Values<>("a"), false, true},
                {"==", new Values<>("a"), ">=", new Values<>(" "), false, false},

                {"==", new Values<>("a"), "<", new Values<>("a"), false, false},
                {"==", new Values<>("a"), "<", new Values<>("b"), false, false},
                {"==", new Values<>("a"), "<=", new Values<>("a"), false, true},
                {"==", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {"==", new Values<>("a"), "<=", new Values<>("b"), false, false},
                {"==", new Values<>("a"), "in", new Values<>("a", "b"), false, true},
                {"==", new Values<>("a"), "in", new Values<>("b", "c"), false, false},
                {"==", new Values<>("a"), "not in", new Values<>("a", "b"), false, false},
                {"==", new Values<>("a"), "not in", new Values<>("b", "c"), false, true},
                {"!=", new Values<>("a"), "!=", new Values<>("b"), false, false},

                {"!=", new Values<>("a"), ">", new Values<>(" "), false, false},
                {"!=", new Values<>("a"), ">", new Values<>("b"), false, false},
                {"!=", new Values<>("a"), ">=", new Values<>(" "), false, false},
                {"!=", new Values<>("a"), ">=", new Values<>("a"), false, false},
                {"!=", new Values<>("a"), ">=", new Values<>("b"), false, false},
                {"!=", new Values<>("a"), "<", new Values<>("b"), false, false},
                {"!=", new Values<>("a"), "<", new Values<>(" "), false, false},
                {"!=", new Values<>("a"), "<=", new Values<>("b"), false, false},
                {"!=", new Values<>("a"), "<=", new Values<>("a"), false, false},
                {"!=", new Values<>("a"), "<=", new Values<>(" "), false, false},

                // This is tricky since, != a conflicts with ==a, but subsumes ==b
                // At this point we need to ignore this, since we do not support "or"
                {"!=", new Values<>("a"), "in", new Values<>("a", "b"), false, false},

                {"!=", new Values<>("a"), "in", new Values<>("b", "c"), true, false},

                {"!=", new Values<>("a"), "not in", new Values<>("a", "b"), false, true},
                {"!=", new Values<>("a"), "not in", new Values<>("b", "c"), false, false},

                {">", new Values<>("a"), ">", new Values<>("b"), false, false},
                {">", new Values<>("a"), ">=", new Values<>("a"), false, true},
                {">", new Values<>("a"), ">=", new Values<>("b"), false, false},
                {">", new Values<>("a"), "<", new Values<>(" "), false, false},
                {">", new Values<>("a"), "<", new Values<>("c"), false, false},
                {">", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {">", new Values<>("a"), "<=", new Values<>("b"), false, false},
                {">", new Values<>("a"), "in", new Values<>("a", "b"), false, false},
                {">", new Values<>("a"), "in", new Values<>("b", "c"), false, false},
                {">", new Values<>("a"), "not in", new Values<>("a", "b"), false, false},
                {">", new Values<>("a"), "not in", new Values<>("0", "1"), false, false},

                {">=", new Values<>("a"), ">=", new Values<>("b"), false, false},
                {">=", new Values<>("a"), "<", new Values<>(" "), false, false},
                {">=", new Values<>("a"), "<", new Values<>("b"), false, false},
                {">=", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {">=", new Values<>("a"), "<=", new Values<>("c"), false, false},
                {">=", new Values<>("a"), "in", new Values<>("0", "b"), false, false},
                {">=", new Values<>("a"), "in", new Values<>("b", "c"), false, false},
                {">=", new Values<>("a"), "not in", new Values<>("a", "b"), false, false},
                {">=", new Values<>("a"), "not in", new Values<>("0", "1"), false, false},

                {"<", new Values<>("a"), "<", new Values<>("b"), false, false},
                {"<", new Values<>("a"), "<=", new Values<>(" "), false, false},
                {"<", new Values<>("a"), "<=", new Values<>("c"), false, false},
                {"<", new Values<>("a"), "in", new Values<>("a", "b"), false, false},
                {"<", new Values<>("a"), "in", new Values<>("0", "1"), false, false},
                {"<", new Values<>("a"), "not in", new Values<>("a", "b"), false, false},
                {"<", new Values<>("a"), "not in", new Values<>("0", "1"), false, false},

                {"<=", new Values<>("a"), "<=", new Values<>("b"), false, false},
                {"<=", new Values<>("a"), "in", new Values<>("a", "b"), false, false},
                {"<=", new Values<>("a"), "in", new Values<>("0", "1"), false, false},
                {"<=", new Values<>("a"), "not in", new Values<>("b", "c"), false, false},
                {"<=", new Values<>("a"), "not in", new Values<>("0", "1"), false, false},

                {"in", new Values<>("a"), "in", new Values<>("a", "b"), false, true},
                {"in", new Values<>("b", "a"), "in", new Values<>("a", "b"), true, true},
                {"in", new Values<>("a"), "in", new Values<>("0", "1"), false, false},
                {"in", new Values<>("a"), "not in", new Values<>("a", "b"), false, false},
                {"in", new Values<>("a"), "not in", new Values<>("b", "c"), false, true},

                {"not in", new Values<>("b", "a"), "not in", new Values<>("a", "b"), true, true},
                {"not in", new Values<>("a"), "not in", new Values<>("a", "b"), false, true},
                {"not in", new Values<>("a"), "not in", new Values<>("b", "c"), false, false},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testASubsumesB(String operator1, Values value1, String operator2, Values value2, boolean aSubsumesB, boolean bSubsumesA) {
        StringConditionInspector a = getStringCondition(field, value1, operator1);
        StringConditionInspector b = getStringCondition(field, value2, operator2);

        assertThat(a.subsumes(b)).as(getAssertDescription1(a,
                b,
                aSubsumesB)).isEqualTo(aSubsumesB);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testBSubsumesA(String operator1, Values value1, String operator2, Values value2, boolean aSubsumesB, boolean bSubsumesA) {
        StringConditionInspector a = getStringCondition(field, value1, operator1);
        StringConditionInspector b = getStringCondition(field, value2, operator2);

        assertThat(b.subsumes(a)).as(getAssertDescription1(b,
                a,
                bSubsumesA)).isEqualTo(bSubsumesA);
    }

    private String getAssertDescription1(StringConditionInspector a,
                                        StringConditionInspector b,
                                        boolean conflictExpected) {
        return format("Expected condition '%s' %sto subsume condition '%s':",
                a.toHumanReadableString(),
                conflictExpected ? "" : "not ",
                b.toHumanReadableString());
    }

}