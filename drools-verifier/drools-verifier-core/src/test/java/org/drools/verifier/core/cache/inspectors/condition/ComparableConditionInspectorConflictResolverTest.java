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
import java.util.Date;

import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getComparableCondition;


@ExtendWith(MockitoExtension.class)
public class ComparableConditionInspectorConflictResolverTest {

	@Mock
    private Field field;

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

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(final String operator1, final Comparable value1, final String operator2, final Comparable value2, final boolean conflictExpected) {
        final ComparableConditionInspector a = getComparableCondition(field, value1, operator1);
        final ComparableConditionInspector b = getComparableCondition(field, value2, operator2);

        assertThat(a.conflicts(b)).as(getAssertDescriptionConflict(a,
                b,
                conflictExpected)).isEqualTo(conflictExpected);
        assertThat(a.conflicts(b)).as(getAssertDescriptionConflict(a,
                b,
                conflictExpected)).isEqualTo(conflictExpected);
        assertThat(a.overlaps(b)).as(getAssertDescriptionOverlap(a,
                b,
                !conflictExpected)).isEqualTo(!conflictExpected);
        assertThat(b.overlaps(a)).as(getAssertDescriptionOverlap(b,
                a,
                !conflictExpected)).isEqualTo(!conflictExpected);
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
}