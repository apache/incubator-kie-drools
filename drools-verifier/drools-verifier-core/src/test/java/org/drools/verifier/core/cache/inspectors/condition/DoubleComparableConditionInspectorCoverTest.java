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
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.fieldCondition;
import static org.mockito.Mockito.mock;

public class DoubleComparableConditionInspectorCoverTest {

    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // condition value, condition operator, value, covers
                {0.5d, "==", 0.5d, true},
                {0.5d, "<", 0.5d, false},
                {0.5d, "<", 10.5d, false},
                {10.5d, "<", 0.5d, true},
                {0.5d, ">", 0.5d, false},
                {0.5d, ">", 10.5d, true},
                {10.5d, "==", 0.5d, false},
                {0.5d, "==", 10.5d, false},
                {10.5d, ">", 0.5d, false},
                {-1.5d, ">", 0.5d, true},
                {0.5d, ">", -1.5d, false},
                {-1.5d, "==", 0.5d, false},
                {0.5d, "==", -1.5d, false},
                {new Date(0), "==", new Date(0), true},
                {new Date(0), "==", new Date(1), false},
                {new Date(0), "!=", new Date(0), false},
                {new Date(0), "!=", new Date(1), true},
                {new Date(0), "after", new Date(1), true},
                {new Date(1), "after", new Date(0), false},
                {new Date(0), "before", new Date(1), false},
                {new Date(1), "before", new Date(0), true}
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(Comparable conditionValue, String conditionOperator, Comparable value, boolean coverExpected) {
        this.field = mock(Field.class);
        ComparableConditionInspector a = getCondition(conditionValue, conditionOperator);

        assertThat(a.covers(value)).as(getAssertDescription(a,
                value,
                coverExpected)).isEqualTo(coverExpected);
    }

    private String getAssertDescription(ComparableConditionInspector a,
                                        Comparable b,
                                        boolean conflictExpected) {
        return format("Expected condition '%s' %sto cover value '%s':",
                a.toHumanReadableString(),
                conflictExpected ? "" : "not ",
                b.toString());
    }

    private ComparableConditionInspector getCondition(Comparable value, String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector(fieldCondition(field, value, operator), configurationMock);
    }
}