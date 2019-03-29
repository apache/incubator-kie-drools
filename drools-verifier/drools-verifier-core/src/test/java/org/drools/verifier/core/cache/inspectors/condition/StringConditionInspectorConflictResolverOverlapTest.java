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
public class StringConditionInspectorConflictResolverOverlapTest {

    private final Values value1;
    private final Values value2;
    private final String operator1;
    private final String operator2;
    private final boolean conflictExpected;
    private final boolean overlapExpected;
    private final Field field;

    public StringConditionInspectorConflictResolverOverlapTest(String operator1,
                                                               Values value1,
                                                               String operator2,
                                                               Values value2,
                                                               boolean conflictExpected,
                                                               boolean overlapExpected) {
        this.field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
        this.overlapExpected = overlapExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // matches and soundslike are probably not doable...
                // op1, val1, op2, val2, conflicts, overlaps
                {"==", new Values("a"), "==", new Values("a"), false, true},
                {"!=", new Values("a"), "!=", new Values("a"), false, true},
                {">", new Values("a"), ">", new Values("a"), false, false},
                {">=", new Values("a"), ">=", new Values("a"), false, false},
                {"<", new Values("a"), "<", new Values("a"), false, false},
                {"<=", new Values("a"), "<=", new Values("a"), false, false},
                {"in", new Values("a",
                                  "b"), "in", new Values("a",
                                                         "b"), false, true},
                {"not in", new Values("a",
                                      "b"), "not in", new Values("a",
                                                                 "b"), false, true},
                {"matches", new Values("a"), "matches", new Values("a"), false, true},
                {"soundslike", new Values("a"), "soundslike", new Values("a"), false, true},

                {"==", new Values("a"), "!=", new Values("b"), false, true},
                {"==", new Values("a"), ">", new Values(" "), false, false},
                {"==", new Values("a"), ">=", new Values("a"), false, false},
                {"==", new Values("a"), "<", new Values("b"), false, false},
                {"==", new Values("a"), "<=", new Values("a"), false, false},
                {"==", new Values("a"), "in", new Values("a",
                                                         "b"), false, true},
                {"==", new Values("a"), "not in", new Values("b",
                                                             "c",
                                                             "d"), false, true},
                {"==", new Values("a"), "matches", new Values("a"), false, true},
                {"==", new Values("a"), "soundslike", new Values("a"), false, true},

                {"==", new Values("a"), "!=", new Values("a"), true, false},
                {"==", new Values("a"), ">", new Values("a"), false, false},
                {"==", new Values("a"), ">=", new Values("a"), false, false},
                {"==", new Values("a"), "<", new Values("a"), false, false},
                {"==", new Values("a"), "<=", new Values(" "), false, false},
                {"==", new Values("a"), "in", new Values("b",
                                                         "c",
                                                         "d"), true, false},
                {"==", new Values("a"), "not in", new Values("a",
                                                             "b"), true, false},
                {"==", new Values("a"), "matches", new Values("a"), false, true},
                {"==", new Values("a"), "soundslike", new Values("a"), false, true},

                {"!=", new Values("a"), "!=", new Values("a"), false, true},
                {"!=", new Values("a"), ">", new Values(" "), false, false},
                {"!=", new Values("a"), ">=", new Values("a"), false, false},
                {"!=", new Values("a"), "<", new Values("a"), false, false},
                {"!=", new Values("a"), "<=", new Values("a"), false, false},
                {"!=", new Values("a"), "in", new Values("a",
                                                         "b"), false, true},
                {"!=", new Values("a"), "in", new Values("b",
                                                         "c",
                                                         "d"), false, true},
                {"!=", new Values("a"), "not in", new Values("b",
                                                             "c",
                                                             "d"), false, true},
                {"!=", new Values("a"), "matches", new Values("a"), true, false},
                {"!=", new Values("a"), "soundslike", new Values("a"), true, false},

                {"!=", new Values("a"), "in", new Values("a"), true, false},
                {"!=", new Values("a"), "matches", new Values("a"), true, false},
                {"!=", new Values("a"), "soundslike", new Values("a"), true, false},

                {">", new Values("a"), ">", new Values("a"), false, false},
                {">", new Values("a"), ">=", new Values("a"), false, false},
                {">", new Values("a"), "<", new Values("c"), false, false},
                {">", new Values("a"), "<=", new Values("a"), false, false},
                {">", new Values("a"), "in", new Values("a",
                                                        "b"), false, false},
                {">", new Values("a"), "not in", new Values("b",
                                                            "c",
                                                            "d"), false, false},
                {">", new Values("a"), "matches", new Values("a"), false, false},
                {">", new Values("a"), "soundslike", new Values("a"), false, false},

                {">", new Values("a"), "<", new Values("a"), false, false},
                {">", new Values("a"), "<=", new Values("a"), false, false},
                {">", new Values("a"), "in", new Values("0",
                                                        "1",
                                                        "A",
                                                        "B",
                                                        "a"), false, false},
                {">", new Values("a"), "matches", new Values("a"), false, false},
                {">", new Values("a"), "soundslike", new Values(""), false, false},

                {">=", new Values("a"), ">=", new Values("a"), false, false},
                {">=", new Values("a"), "<", new Values("a"), false, false},
                {">=", new Values("a"), "<=", new Values("a"), false, false},
                {">=", new Values("a"), "in", new Values("a"), false, false},
                {">=", new Values("a"), "not in", new Values("b",
                                                             "c",
                                                             "d"), false, false},
                {">=", new Values("a"), "matches", new Values("a"), false, false},
                {">=", new Values("a"), "soundslike", new Values("a"), false, false},

                {">=", new Values("a"), "<", new Values(" "), false, false},
                {">=", new Values("a"), "<=", new Values(" "), false, false},
                {">=", new Values("a"), "in", new Values("0",
                                                         "1",
                                                         "A",
                                                         "B"), false, false},
                {">=", new Values("a"), "matches", new Values("A"), false, false},
                {">=", new Values("a"), "soundslike", new Values(""), false, false},

                {"<", new Values("a"), "<", new Values("a"), false, false},
                {"<", new Values("a"), "<=", new Values("a"), false, false},
                {"<", new Values("a"), "in", new Values("A",
                                                        "B",
                                                        "a",
                                                        "b"), false, false},
                {"<", new Values("a"), "not in", new Values("b",
                                                            "c",
                                                            "d"), false, false},
                {"<", new Values("a"), "matches", new Values("A"), false, false},
                {"<", new Values("a"), "soundslike", new Values(""), false, false},

                {"<", new Values("a"), "in", new Values("b",
                                                        "c",
                                                        "d"), false, false},
                {"<", new Values("a"), "matches", new Values("a"), false, false},
                {"<", new Values("a"), "soundslike", new Values("a"), false, false},

                {"<=", new Values("a"), "<=", new Values("a"), false, false},
                {"<=", new Values("a"), "in", new Values("A",
                                                         "B",
                                                         "a",
                                                         "b"), false, false},
                {"<=", new Values("a"), "not in", new Values("b",
                                                             "c",
                                                             "d"), false, false},
                {"<=", new Values("a"), "matches", new Values("A"), false, false},
                {"<=", new Values("a"), "soundslike", new Values(""), false, false},

                {"<=", new Values("a"), "in", new Values("b",
                                                         "c",
                                                         "d"), false, false},
                {"<=", new Values("a"), "matches", new Values("a"), false, false},
                {"<=", new Values("a"), "soundslike", new Values("a"), false, false},

                {"in", new Values("a",
                                  "b"), "in", new Values("b",
                                                         "c",
                                                         "d"), false, true},
                {"in", new Values("a",
                                  "b"), "not in", new Values("b",
                                                             "c",
                                                             "d"), false, true},
                {"in", new Values("a",
                                  "b"), "matches", new Values("a"), false, true},
                {"in", new Values("a",
                                  "b"), "soundslike", new Values("a"), false, true},

                {"in", new Values("a",
                                  "b"), "in", new Values("c",
                                                         "d"), true, false},
                {"in", new Values("a",
                                  "b"), "not in", new Values("a",
                                                             "b"), true, false},
                {"in", new Values("a",
                                  "b"), "matches", new Values("c"), true, false},
                {"in", new Values("a",
                                  "b"), "soundslike", new Values("c"), true, false},

                {"not in", new Values("a",
                                      "b"), "matches", new Values("c"), false, true},
                {"not in", new Values("a",
                                      "b"), "soundslike", new Values("c"), false, true},

                {"not in", new Values("a",
                                      "b"), "matches", new Values("a"), true, false},

                {"matches", new Values("a"), "soundslike", new Values("a"), false, true},

                {"matches", new Values("a"), "soundslike", new Values("a"), false, true},
        });
    }

    @Test
    public void parametrizedConflictTest() {
        StringConditionInspector a = getCondition(value1,
                                                  operator1);
        StringConditionInspector b = getCondition(value2,
                                                  operator2);

        boolean conflicts = a.conflicts(b);
        assertEquals(getAssertDescription(a,
                                          b,
                                          conflictExpected,
                                          "conflict"),
                     conflictExpected,
                     conflicts);
        boolean conflicts1 = b.conflicts(a);
        assertEquals(getAssertDescription(b,
                                          a,
                                          conflictExpected,
                                          "conflict"),
                     conflictExpected,
                     conflicts1);
    }

    @Test
    public void parametrizedOverlapTest() {
        StringConditionInspector a = getCondition(value1,
                                                  operator1);
        StringConditionInspector b = getCondition(value2,
                                                  operator2);

        boolean overlaps = a.overlaps(b);
        assertEquals(getAssertDescription(a,
                                          b,
                                          overlapExpected,
                                          "overlap"),
                     overlapExpected,
                     overlaps);
        boolean overlaps1 = b.overlaps(a);
        assertEquals(getAssertDescription(b,
                                          a,
                                          overlapExpected,
                                          "overlap"),
                     overlapExpected,
                     overlaps1);
    }

    private StringConditionInspector getCondition(final Values values,
                                                  final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new StringConditionInspector(new FieldCondition<>(field,
                                                                 mock(Column.class),
                                                                 operator,
                                                                 values,
                                                                 configurationMock),
                                            configurationMock);
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