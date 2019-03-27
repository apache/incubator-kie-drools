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
import java.util.Date;

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
public class ComparableConditionInspectorConflictResolverTest {

    private final Field field;
    private final Comparable value1;
    private final Comparable value2;
    private final String operator1;
    private final String operator2;
    private final boolean conflictExpected;

    public ComparableConditionInspectorConflictResolverTest(final String operator1,
                                                            final Comparable value1,
                                                            final String operator2,
                                                            final Comparable value2,
                                                            final boolean conflictExpected) {
        this.field = mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // op1, val1, op2, val2, conflicts
                {"==", 0.5d, "==", 0.5d, false},
                {"!=", 0.5d, "!=", 0.5d, false},
                {">", 0.5d, ">", 0.5d, false},
                {">=", 0.5d, ">=", 0.5d, false},
                {"<", 0.5d, "<", 0.5d, false},
                {"<=", 0.5d, "<=", 0.5d, false},

                {"==", 0.5d, "!=", 1.5d, false},
                {"==", 0.5d, ">", -1.5d, false},
                {"==", 0.5d, ">", -10.5d, false},
                {"==", 0.5d, ">=", 0.5d, false},
                {"==", 0.5d, ">=", -10.5d, false},
                {"==", 0.5d, "<", 1.5d, false},
                {"==", 0.5d, "<", 10.5d, false},
                {"==", 0.5d, "<=", 0.5d, false},
                {"==", 0.5d, "<=", 10.5d, false},

                {"==", 0.5d, "==", 1.5d, true},
                {"==", 0.5d, "!=", 0.5d, true},
                {"==", 0.5d, ">", 0.5d, true},
                {"==", 0.5d, ">", 10.5d, true},
                {"==", 0.5d, ">=", 1.5d, true},
                {"==", 0.5d, ">=", 10.5d, true},
                {"==", 0.5d, "<", 0.5d, true},
                {"==", 0.5d, "<", -10.5d, true},
                {"==", 0.5d, "<=", -1.5d, true},
                {"==", 0.5d, "<=", -10.5d, true},

                {"!=", 0.5d, "!=", 1.5d, false},
                {"!=", 0.5d, ">", -1.5d, false},
                {"!=", 0.5d, ">=", 0.5d, false},
                {"!=", 0.5d, "<", 1.5d, false},
                {"!=", 0.5d, "<=", 0.5d, false},

                {">", 0.5d, ">", 1.5d, false},
                {">", 0.5d, ">=", 0.5d, false},
                {">", 0.5d, "<", 2.5d, false},
                {">", 0.5d, "<", 10.5d, false},
                {">", 0.5d, "<=", 1.5d, false},
                {">", 0.5d, "<=", 10.5d, false},

                {">", 0.5d, "<", -1.5d, true},
                {">", 0.5d, "<", 0.5d, true},
                {">", 0.5d, "<", 1.5d, false},
                {">", 0.5d, "<=", -2.5d, true},
                {">", 0.5d, "<=", -1.5d, true},
                {">", 0.5d, "<=", 0.5d, true},

                {">=", 0.5d, ">=", 1.5d, false},
                {">=", 0.5d, "<", 1.5d, false},
                {">=", 0.5d, "<", 10.5d, false},
                {">=", 0.5d, "<=", 0.5d, false},
                {">=", 0.5d, "<=", 10.5d, false},

                {">=", 0.5d, "<", -2.5d, true},
                {">=", 0.5d, "<", -1.5d, true},
                {">=", 0.5d, "<", 0.5d, true},
                {">=", 0.5d, "<=", -3.5d, true},
                {">=", 0.5d, "<=", -2.5d, true},
                {">=", 0.5d, "<=", -1.5d, true},

                {"<", 0.5d, "<", 1.5d, false},
                {"<", 0.5d, "<=", 0.5d, false},

                {"<=", 0.5d, "<=", 1.5d, false},

                // operators only allowed for Date...
                {"after", new Date(0), "after", new Date(0), false},
                {"before", new Date(0), "before", new Date(0), false},

                {"after", new Date(10000), "before", new Date(20000), false},
                {"after", new Date(20000), "before", new Date(10000), true}
        });
    }

    @Test
    public void parametrizedTest() {
        final ComparableConditionInspector a = getCondition(value1,
                                                            operator1);
        final ComparableConditionInspector b = getCondition(value2,
                                                            operator2);

        assertEquals(getAssertDescriptionConflict(a,
                                                  b,
                                                  conflictExpected),
                     conflictExpected,
                     a.conflicts(b));
        assertEquals(getAssertDescriptionConflict(a,
                                                  b,
                                                  conflictExpected),
                     conflictExpected,
                     a.conflicts(b));
        assertEquals(getAssertDescriptionOverlap(a,
                                                 b,
                                                 !conflictExpected),
                     !conflictExpected,
                     a.overlaps(b));
        assertEquals(getAssertDescriptionOverlap(b,
                                                 a,
                                                 !conflictExpected),
                     !conflictExpected,
                     b.overlaps(a));
    }

    private String getAssertDescriptionConflict(final ComparableConditionInspector a,
                                                final ComparableConditionInspector b,
                                                final boolean conflictExpected) {
        return format("Expected condition '%s' %sto conflict with condition '%s':",
                      a.toHumanReadableString(),
                      conflictExpected ? "" : "not ",
                      b.toHumanReadableString());
    }

    private String getAssertDescriptionOverlap(final ComparableConditionInspector a,
                                               final ComparableConditionInspector b,
                                               final boolean conflictExpected) {
        return format("Expected condition '%s' %sto overlap with condition '%s':",
                      a.toHumanReadableString(),
                      conflictExpected ? "" : "not ",
                      b.toHumanReadableString());
    }

    private ComparableConditionInspector getCondition(final Comparable value,
                                                      final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector(new FieldCondition(field,
                                                                   mock(Column.class),
                                                                   operator,
                                                                   new Values<>(value),
                                                                   configurationMock),
                                                configurationMock);
    }
}