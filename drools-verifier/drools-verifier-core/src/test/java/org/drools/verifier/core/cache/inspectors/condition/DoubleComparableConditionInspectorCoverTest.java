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
public class DoubleComparableConditionInspectorCoverTest {

    private final Comparable conditionValue;
    private final Comparable value;
    private final String conditionOperator;
    private final boolean coverExpected;
    private final Field field;

    public DoubleComparableConditionInspectorCoverTest(Comparable conditionValue,
                                                       String conditionOperator,
                                                       Comparable value,
                                                       boolean coverExpected) {
        this.field = mock(Field.class);
        this.conditionValue = conditionValue;
        this.value = value;
        this.conditionOperator = conditionOperator;
        this.coverExpected = coverExpected;
    }

    @Parameters
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

    @Test
    public void parametrizedTest() {
        ComparableConditionInspector a = getCondition(conditionValue,
                                                      conditionOperator);

        assertEquals(getAssertDescription(a,
                                          value,
                                          coverExpected),
                     coverExpected,
                     a.covers(value));
    }

    private String getAssertDescription(ComparableConditionInspector a,
                                        Comparable b,
                                        boolean conflictExpected) {
        return format("Expected condition '%s' %sto cover value '%s':",
                      a.toHumanReadableString(),
                      conflictExpected ? "" : "not ",
                      b.toString());
    }

    private ComparableConditionInspector getCondition(Comparable value,
                                                      String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector(new FieldCondition(field,
                                                                   mock(Column.class),
                                                                   operator,
                                                                   new Values<>(value),
                                                                   configurationMock),
                                                configurationMock);
    }
}