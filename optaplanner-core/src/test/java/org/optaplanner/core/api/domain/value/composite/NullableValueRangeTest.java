/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.domain.value.composite;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.api.domain.value.buildin.collection.ListValueRange;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class NullableValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(5L, new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(0, 2, 5, 10))).getSize());
        assertEquals(6L, new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(100, 120, 5, 7, 8))).getSize());
        assertEquals(4L, new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(-15, 25, 0))).getSize());
        assertEquals(4L, new NullableValueRange<String>(new ListValueRange<String>(Arrays.asList("b", "z", "a"))).getSize());
        assertEquals(1L, new NullableValueRange<String>(new ListValueRange<String>(Collections.<String>emptyList())).getSize());
    }

    @Test
    public void get() {
        assertEquals(5, new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(0, 2, 5, 10))).get(2L).intValue());
        assertEquals(null, new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(0, 2, 5, 10))).get(4L));
        assertEquals("c", new NullableValueRange<String>(new ListValueRange<String>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(3L));
        assertEquals(null, new NullableValueRange<String>(new ListValueRange<String>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(6L));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(0, 2, 5, 10))).createOriginalIterator(), 0, 2, 5, 10, null);
        assertAllElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(100, 120, 5, 7, 8))).createOriginalIterator(), 100, 120, 5, 7, 8, null);
        assertAllElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(-15, 25, 0))).createOriginalIterator(), -15, 25, 0, null);
        assertAllElementsOfIterator(new NullableValueRange<String>(new ListValueRange<String>(Arrays.asList("b", "z", "a"))).createOriginalIterator(), "b", "z", "a", null);
        assertAllElementsOfIterator(new NullableValueRange<String>(new ListValueRange<String>(Collections.<String>emptyList())).createOriginalIterator(), new String[]{null});
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0, 3, 0, 3, 0, 3, 0, 0);

        assertElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(0, 2, 5))).createRandomIterator(workingRandom), null, 0);
        assertElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(100, 120, 5))).createRandomIterator(workingRandom), null, 100);
        assertElementsOfIterator(new NullableValueRange<Integer>(new ListValueRange<Integer>(Arrays.asList(-15, 25, 0))).createRandomIterator(workingRandom), null, -15);
        assertElementsOfIterator(new NullableValueRange<String>(new ListValueRange<String>(Arrays.asList("b", "z", "a"))).createRandomIterator(workingRandom), null, "b");
        assertElementsOfIterator(new NullableValueRange<String>(new ListValueRange<String>(Collections.<String>emptyList())).createRandomIterator(workingRandom), new String[]{null});
    }

}
