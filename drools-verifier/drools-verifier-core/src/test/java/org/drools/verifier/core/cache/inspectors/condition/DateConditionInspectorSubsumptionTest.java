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
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DateConditionInspectorSubsumptionTest {

    @Mock
    private Field field;

    @Test
    public void testSubsume001() throws
            Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(100),
                                                            "!=");
        ComparableConditionInspector<Date> b = getCondition(new Date(100),
                                                            "!=");

        assertTrue(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsumeEquals001() throws
            Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(100),
                                                            "==");
        ComparableConditionInspector<Date> b = getCondition(new Date(10),
                                                            ">");

        assertFalse(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsumeEquals002() throws
            Exception {
        ComparableConditionInspector<Date> a = getCondition(new Date(10),
                                                            "==");
        ComparableConditionInspector<Date> b = getCondition(new Date(100),
                                                            ">");

        assertFalse(a.subsumes(b));
        assertFalse(b.subsumes(a));
    }

    private ComparableConditionInspector<Date> getCondition(Date date,
                                                            String operator) {
        AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
        return new ComparableConditionInspector<Date>(new FieldCondition(field,
                                                                         mock(Column.class),
                                                                         operator,
                                                                         new Values<>(date),
                                                                         configurationMock),
                                                      configurationMock);
    }
}