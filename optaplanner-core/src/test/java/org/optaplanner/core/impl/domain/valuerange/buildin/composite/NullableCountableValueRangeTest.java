/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class NullableCountableValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(5L, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).getSize());
        assertEquals(6L, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8))).getSize());
        assertEquals(4L, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0))).getSize());
        assertEquals(4L, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).getSize());
        assertEquals(1L, new NullableCountableValueRange<>(new ListValueRange<>(Collections.<String>emptyList())).getSize());
    }

    @Test
    public void get() {
        assertEquals(5, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).get(2L).intValue());
        assertEquals(null, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).get(4L));
        assertEquals("c", new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(3L));
        assertEquals(null, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(6L));
    }

    @Test
    public void contains() {
        assertEquals(true, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(5));
        assertEquals(false, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(4));
        assertEquals(true, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(null));
        assertEquals(true, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains("a"));
        assertEquals(false, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains("n"));
        assertEquals(true, new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains(null));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).createOriginalIterator(), null, 0, 2, 5, 10);
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8))).createOriginalIterator(), null, 100, 120, 5, 7, 8);
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0))).createOriginalIterator(), null, -15, 25, 0);
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).createOriginalIterator(), null, "b", "z", "a");
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Collections.<String>emptyList())).createOriginalIterator(), new String[]{null});
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);

        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5))).createRandomIterator(workingRandom), null, 0);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5))).createRandomIterator(workingRandom), null, 100);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0))).createRandomIterator(workingRandom), null, -15);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).createRandomIterator(workingRandom), null, "b");
        when(workingRandom.nextInt(anyInt())).thenReturn(0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Collections.<String>emptyList())).createRandomIterator(workingRandom), new String[]{null});
    }

}
