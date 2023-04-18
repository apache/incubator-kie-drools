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

import java.util.Date;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.fieldCondition;

@ExtendWith(MockitoExtension.class)
public class DateConditionInspectorSubsumptionTest {

    @Mock
    private Field field;

    @Test
    void testSubsume001() throws Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(100),
                "!=");
        ComparableConditionInspector<Date> b = getCondition(new Date(100),
                "!=");

        assertThat(a.subsumes(b)).isTrue();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumeEquals001() throws Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(100),
                "==");
        ComparableConditionInspector<Date> b = getCondition(new Date(10),
                ">");

        assertThat(a.subsumes(b)).isFalse();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumeEquals002() throws Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(10),
                "==");
        ComparableConditionInspector<Date> b = getCondition(new Date(100),
                ">");

        assertThat(a.subsumes(b)).isFalse();
        assertThat(b.subsumes(a)).isFalse();
    }

    private ComparableConditionInspector<Date> getCondition(Date date,
                                                            String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector<Date>(fieldCondition(field, date, operator), configurationMock);
    }
}