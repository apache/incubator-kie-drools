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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FieldRangeTest {

    @Test
    public void getDoubleValue() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        new FieldRange(0.0,
                                                                       10.0),
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(10.0, FieldRange.getDoubleMaxValue(objectField),
                     1.0);
        assertEquals(0.0, FieldRange.getDoubleMinValue(objectField),
                     1.0);
    }

    @Test
    public void getIntegerValue() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        new FieldRange(0,
                                                                       10),
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(10, FieldRange.getIntegerMaxValue(objectField),
                     1.0);
        assertEquals(0, FieldRange.getIntegerMinValue(objectField),
                     1.0);
    }

    @Test
    public void getDoubleValueEmpty() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(Double.MAX_VALUE, FieldRange.getDoubleMaxValue(objectField), 1.0);
        assertEquals(Double.MIN_VALUE, FieldRange.getDoubleMinValue(objectField), 1.0);
    }

    @Test
    public void getIntegerValueEmpty() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(Integer.MAX_VALUE, FieldRange.getIntegerMaxValue(objectField));
        assertEquals(Integer.MIN_VALUE, FieldRange.getIntegerMinValue(objectField));
    }

    @Test
    public void getDoubleValueException() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        new FieldRange(null,
                                                                       null),
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(Double.MAX_VALUE, FieldRange.getDoubleMaxValue(objectField),
                     1.0);
        assertEquals(Double.MIN_VALUE, FieldRange.getDoubleMinValue(objectField),
                     1.0);
    }

    @Test
    public void getIntegerValueException() {
        final ObjectField objectField = new ObjectField("factType",
                                                        "fieldType",
                                                        "name",
                                                        new FieldRange(null,
                                                                       null),
                                                        mock(AnalyzerConfiguration.class));
        assertEquals(Integer.MAX_VALUE, FieldRange.getIntegerMaxValue(objectField));
        assertEquals(Integer.MIN_VALUE, FieldRange.getIntegerMinValue(objectField));
    }
}