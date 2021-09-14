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

package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ConditionsInspectorTest {

    Field field;
    private AnalyzerConfigurationMock configurationMock;

    @Before
    public void setUp() throws
            Exception {
        configurationMock = new AnalyzerConfigurationMock();
        field = new Field(new ObjectField("Person",
                                          "Integer",
                                          "age",
                                          configurationMock),
                          "Person",
                          "Integer",
                          "age",
                          configurationMock);
    }

    @Test
    public void testSubsume001() throws
            Exception {
        final ConditionsInspectorMultiMap a = getConditions(new ComparableConditionInspector<Integer>(new FieldCondition(field,
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(1),
                                                                                                                         configurationMock),
                                                                                                      configurationMock));
        final ConditionsInspectorMultiMap b = getConditions(new ComparableConditionInspector<Integer>(new FieldCondition(field,
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(1),
                                                                                                                         configurationMock),
                                                                                                      configurationMock));

        assertTrue(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsume002() throws
            Exception {
        final ConditionsInspectorMultiMap a = getConditions(new ComparableConditionInspector<Integer>(new FieldCondition(field,
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(1),
                                                                                                                         configurationMock),
                                                                                                      configurationMock));

        final ConditionsInspectorMultiMap b = getConditions(new ComparableConditionInspector<Integer>(new FieldCondition(field,
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(1),
                                                                                                                         configurationMock),
                                                                                                      configurationMock),
                                                            new ComparableConditionInspector<Integer>(new FieldCondition(new Field(mock(ObjectField.class),
                                                                                                                                   "Person",
                                                                                                                                   "Integer",
                                                                                                                                   "balance",
                                                                                                                                   configurationMock),
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(111111111),
                                                                                                                         configurationMock),
                                                                                                      configurationMock));

        assertFalse(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsume003() throws
            Exception {
        final Field nameField = new Field(new ObjectField("Person",
                                                          "String",
                                                          "name",
                                                          configurationMock),
                                          "Person",
                                          "String",
                                          "name",
                                          configurationMock);
        final Field lastNameField = new Field(new ObjectField("Person",
                                                              "String",
                                                              "lastName",
                                                              configurationMock),
                                              "Person",
                                              "String",
                                              "lastName",
                                              configurationMock);
        final ConditionsInspectorMultiMap a = getConditions(new ComparableConditionInspector<String>(new FieldCondition(nameField,
                                                                                                                        mock(Column.class),
                                                                                                                        "==",
                                                                                                                        new Values<>("Toni"),
                                                                                                                        configurationMock),
                                                                                                     configurationMock));

        final ConditionsInspectorMultiMap b = getConditions(new ComparableConditionInspector<Integer>(new FieldCondition(field,
                                                                                                                         mock(Column.class),
                                                                                                                         "==",
                                                                                                                         new Values<>(12),
                                                                                                                         configurationMock),
                                                                                                      configurationMock),
                                                            new ComparableConditionInspector<String>(new FieldCondition(nameField,
                                                                                                                        mock(Column.class),
                                                                                                                        "==",
                                                                                                                        new Values<>("Toni"),
                                                                                                                        configurationMock),
                                                                                                     configurationMock),
                                                            new ComparableConditionInspector<String>(new FieldCondition(lastNameField,
                                                                                                                        mock(Column.class),
                                                                                                                        "==",
                                                                                                                        new Values<>("Rikkola"),
                                                                                                                        configurationMock),
                                                                                                     configurationMock));

        assertFalse(a.subsumes(b));
        assertTrue(b.subsumes(a));
    }

    private ConditionsInspectorMultiMap getConditions(final ConditionInspector... numericIntegerConditions) {
        final ConditionsInspectorMultiMap conditionsInspector = new ConditionsInspectorMultiMap(configurationMock);
        for (final ConditionInspector inspector : numericIntegerConditions) {
            conditionsInspector.put(((ComparableConditionInspector) inspector).getField()
                                            .getObjectField(),
                                    inspector);
        }
        return conditionsInspector;
    }
}