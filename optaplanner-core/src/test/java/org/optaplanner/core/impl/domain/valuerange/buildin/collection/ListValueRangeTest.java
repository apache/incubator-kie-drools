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

package org.optaplanner.core.impl.domain.valuerange.buildin.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class ListValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(4L, new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).getSize());
        assertEquals(5L, new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).getSize());
        assertEquals(3L, new ListValueRange<>(Arrays.asList(-15, 25, 0)).getSize());
        assertEquals(3L, new ListValueRange<>(Arrays.asList("b", "z", "a")).getSize());
        assertEquals(0L, new ListValueRange<>(Collections.<String>emptyList()).getSize());
    }

    @Test
    public void get() {
        assertEquals(5, new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).get(2L).intValue());
        assertEquals(-120, new ListValueRange<>(Arrays.asList(100, -120)).get(1L).intValue());
        assertEquals("c", new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d")).get(3L));
    }

    @Test
    public void contains() {
        assertEquals(true, new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(5));
        assertEquals(false, new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(4));
        assertEquals(false, new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(null));
        assertEquals(true, new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(7));
        assertEquals(false, new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(9));
        assertEquals(true, new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-15));
        assertEquals(false, new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-14));
        assertEquals(true, new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("a"));
        assertEquals(false, new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("n"));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createOriginalIterator(), 0, 2, 5, 10);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createOriginalIterator(), 100, 120, 5, 7, 8);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createOriginalIterator(), -15, 25, 0);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createOriginalIterator(), "b", "z", "a");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.<String>emptyList()).createOriginalIterator());
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createRandomIterator(workingRandom), 5, 0);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createRandomIterator(workingRandom), 5, 100);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createRandomIterator(workingRandom), 0, -15);
        when(workingRandom.nextInt(anyInt())).thenReturn(2, 0);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createRandomIterator(workingRandom), "a", "b");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.<String>emptyList()).createRandomIterator(workingRandom));
    }

}
