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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StringConditionInspectorToHumanReadableTest {

    private static final String FIELD_NAME = "name";
    private static final String VALUE = "someValue";
    private static final String IS_NOT_NULL = "!= null";
    private static final String IS_NULL = "== null";

    private String operator;

    private AnalyzerConfiguration configuration;

    public void initStringConditionInspectorToHumanReadableTest(String operator) {
        this.operator = operator;
    }

    public static Collection<Object[]> testData() {
        // not sure if '== null' and '!= null' from OperatorsOracle.STRING_OPERATORS make much sense here
        ArrayList<Object> data = new ArrayList<Object>(Arrays.asList("==", "!=", "<", ">", "<=", ">=", "matches", "soundslike", "== null", "!= null"));
        data.addAll(Arrays.asList("in", "not in"));
        Collection<Object[]> data2 = new ArrayList<Object[]>();
        for (Object operator : data) {
            data2.add(new Object[]{operator});
        }
        return data2;
    }

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testToHumanReadableString(String operator) {

        initStringConditionInspectorToHumanReadableTest(operator);

        final StringConditionInspector inspector = getStringConditionInspector();

        if (IS_NOT_NULL.matches(operator)) {
            assertThat(inspector.toHumanReadableString()).isEqualTo(format("%s %s",
                    FIELD_NAME,
                    operator));
        } else if (IS_NULL.matches(operator)) {
            assertThat(inspector.toHumanReadableString()).isEqualTo(format("%s %s",
                    FIELD_NAME,
                    operator));
        } else {
            assertThat(inspector.toHumanReadableString()).isEqualTo(format("%s %s %s",
                    FIELD_NAME,
                    operator,
                    VALUE));
        }
    }

    private StringConditionInspector getStringConditionInspector() {
        if (IS_NOT_NULL.matches(operator)) {
            return new StringConditionInspector(new FieldCondition<>(new Field(mock(ObjectField.class),
                                    "Test",
                                    "String",
                                    FIELD_NAME,
                                    configuration),
                            mock(Column.class),
                            "!=",
                            Values.nullValue(),
                            configuration),
                    configuration);
        } else if (IS_NULL.matches(operator)) {
            return new StringConditionInspector(new FieldCondition<>(new Field(mock(ObjectField.class),
                                    "Test",
                                    "String",
                                    FIELD_NAME,
                                    configuration),
                            mock(Column.class),
                            "==",
                            Values.nullValue(),
                            configuration),
                    configuration);
        } else {
            return new StringConditionInspector(new FieldCondition<>(new Field(mock(ObjectField.class),
                                    "Test",
                                    "String",
                                    FIELD_NAME,
                                    configuration),
                            mock(Column.class),
                            operator,
                            new Values(VALUE),
                            configuration),
                    configuration);
        }
    }
}