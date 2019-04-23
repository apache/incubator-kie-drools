/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.checks.gaps;

import java.util.ArrayList;

import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.NumericIntegerConditionInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IntegerRangeTest {

    @Test
    public void testMinValueDoesNotFlipToMaxValue() {
        final AnalyzerConfiguration analyzerConfiguration = mock(AnalyzerConfiguration.class);

        final ArrayList<ConditionInspector> list = new ArrayList<>();

        list.add(new NumericIntegerConditionInspector(new FieldCondition(mock(Field.class),
                                                                         mock(Column.class),
                                                                         ">=",
                                                                         new Values(Integer.MIN_VALUE),
                                                                         analyzerConfiguration),
                                                      analyzerConfiguration));

        final IntegerRange range = new IntegerRange(list);

        assertTrue(range.lowerBound.equals(Integer.MIN_VALUE));
    }

    @Test
    public void testMaxValueDoesNotFlipToMinValue() {
        final AnalyzerConfiguration analyzerConfiguration = mock(AnalyzerConfiguration.class);

        final ArrayList<ConditionInspector> list = new ArrayList<>();

        list.add(new NumericIntegerConditionInspector(new FieldCondition(mock(Field.class),
                                                                         mock(Column.class),
                                                                         "<=",
                                                                         new Values(Integer.MAX_VALUE),
                                                                         analyzerConfiguration),
                                                      analyzerConfiguration));

        final IntegerRange range = new IntegerRange(list);

        assertTrue(range.upperBound.equals(Integer.MAX_VALUE));
    }
}