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
import org.drools.verifier.core.relations.Operator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StringConditionInspectorCoverTest {

    private Values<String> value1;
    private Values<String> value2;
    private String operator;
    private boolean covers;
    private Field field;

    public void initStringConditionInspectorCoverTest(Values<String> value1,
                                             String operator,
                                             Values<String> value2,
                                             boolean covers) {
        this.field = mock(Field.class);
        this.operator = operator;
        this.value1 = value1;
        this.value2 = value2;
        this.covers = covers;
    }

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {new Values("toni"), Operator.EQUALS.toString(), new Values("toni"), true},
                {new Values("toni"), Operator.MATCHES.toString(), new Values("toni"), true},
                {new Values("toni"), Operator.SOUNDSLIKE.toString(), new Values("toni"), true},
                {new Values("toni",
                        "eder"), Operator.IN.toString(), new Values("toni"), true},
                {new Values("toni"), Operator.GREATER_OR_EQUAL.toString(), new Values("toni"), true},
                {new Values("toni"), Operator.LESS_OR_EQUAL.toString(), new Values("toni"), true},

                {new Values("toni"), Operator.LESS_THAN.toString(), new Values("toni"), false},
                {new Values("toni"), Operator.GREATER_THAN.toString(), new Values("toni"), false},

                {new Values("toni"), Operator.EQUALS.toString(), new Values("michael"), false},
                {new Values("toni"), Operator.MATCHES.toString(), new Values("michael"), false},
                {new Values("toni"), Operator.SOUNDSLIKE.toString(), new Values("michael"), false},
                {new Values("toni",
                        "eder"), Operator.IN.toString(), new Values("michael"), false},
                {new Values("toni"), Operator.GREATER_OR_EQUAL.toString(), new Values("michael"), false},
                {new Values("toni"), Operator.LESS_OR_EQUAL.toString(), new Values("michael"), false},

                {new Values("toni",
                        "eder"), Operator.NOT_IN.toString(), new Values("michael"), true},
                {new Values("toni",
                        "eder"), Operator.NOT_IN.toString(), new Values("eder"), false},

                {new Values("toni"), Operator.NOT_EQUALS.toString(), new Values("toni"), false},
                {new Values("toni"), Operator.NOT_EQUALS.toString(), new Values("eder"), true},

                {new Values("toni"), Operator.NOT_MATCHES.toString(), new Values("toni"), false},
                {new Values("toni"), Operator.NOT_MATCHES.toString(), new Values("eder"), true},

                {new Values("toni rikkola"), Operator.STR_ENDS_WITH.toString(), new Values("rikkola"), true},
                {new Values("toni rikkola"), Operator.STR_ENDS_WITH.toString(), new Values("toni"), false},
                {new Values("toni rikkola"), Operator.STR_STARTS_WITH.toString(), new Values("toni"), true},
                {new Values("toni rikkola"), Operator.STR_STARTS_WITH.toString(), new Values("rikkola"), false},

                // No matter what we do this returns false
                {new Values("array"), Operator.CONTAINS.toString(), new Values("toni",
                        "eder"), false},
                {new Values("array"), Operator.CONTAINS.toString(), new Values("toni"), false},
                {new Values("array"), Operator.CONTAINS.toString(), new Values("eder"), false},
                {new Values("array"), Operator.NOT_CONTAINS.toString(), new Values("toni",
                        "eder"), false},
                {new Values("array"), Operator.NOT_CONTAINS.toString(), new Values("toni"), false},
                {new Values("array"), Operator.NOT_CONTAINS.toString(), new Values("eder"), false},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(Values<String> value1, String operator, Values<String> value2, boolean covers) {
        initStringConditionInspectorCoverTest(value1, operator, value2, covers);
        StringConditionInspector a = getCondition(value1,
                operator);

        assertThat(a.covers(value2.iterator()
                .next())).as(getAssertDescription(a,
                covers,
                                                  value2.iterator()
                                                          .next())).isEqualTo(covers);
    }

    private StringConditionInspector getCondition(final Values<String> values,
                                                  final String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new StringConditionInspector(new FieldCondition<>(field,
                        mock(Column.class),
                        operator,
                        values,
                        configurationMock),
                configurationMock);
    }

    private String getAssertDescription(final StringConditionInspector a,
                                        final boolean covers,
                                        final String condition) {
        return format("Expected condition '%s' to %s cover '%s':",
                a.toHumanReadableString(),
                covers ? "" : "not ",
                condition);
    }
}