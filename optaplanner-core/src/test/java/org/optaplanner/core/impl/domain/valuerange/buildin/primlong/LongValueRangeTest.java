/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.valuerange.buildin.primlong;

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class LongValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(10L, new LongValueRange(0L, 10L).getSize());
        assertEquals(20L, new LongValueRange(100L, 120L).getSize());
        assertEquals(40L, new LongValueRange(-15L, 25L).getSize());
        assertEquals(0L, new LongValueRange(7L, 7L).getSize());
        assertEquals(Long.MAX_VALUE - 2000L, new LongValueRange(-1000L, Long.MAX_VALUE - 3000L).getSize());
        // IncrementUnit
        assertEquals(5L, new LongValueRange(0L, 10L, 2L).getSize());
        assertEquals(5L, new LongValueRange(-1L, 9L, 2L).getSize());
        assertEquals(4L, new LongValueRange(100L, 120L, 5L).getSize());
    }

    @Test
    public void get() {
        assertEquals(3L, new LongValueRange(0L, 10L).get(3L).intValue());
        assertEquals(103L, new LongValueRange(100L, 120L).get(3L).intValue());
        assertEquals(-4L, new LongValueRange(-5L, 25L).get(1L).intValue());
        assertEquals(1L, new LongValueRange(-5L, 25L).get(6L).intValue());
        assertEquals(4L, new LongValueRange(-1000L, Long.MAX_VALUE - 3000L).get(1004L).intValue());
        // IncrementUnit
        assertEquals(6L, new LongValueRange(0L, 10L, 2L).get(3L).intValue());
        assertEquals(5L, new LongValueRange(-1L, 9L, 2L).get(3L).intValue());
        assertEquals(115L, new LongValueRange(100L, 120L, 5L).get(3L).intValue());
    }

    @Test
    public void contains() {
        assertEquals(true, new LongValueRange(0L, 10L).contains(3L));
        assertEquals(false, new LongValueRange(0L, 10L).contains(10L));
        assertEquals(false, new LongValueRange(0L, 10L).contains(null));
        assertEquals(true, new LongValueRange(100L, 120L).contains(100L));
        assertEquals(false, new LongValueRange(100L, 120L).contains(99L));
        assertEquals(true, new LongValueRange(-5L, 25L).contains(-4L));
        assertEquals(false, new LongValueRange(-5L, 25L).contains(-20L));
        // IncrementUnit
        assertEquals(true, new LongValueRange(0L, 10L, 2L).contains(2L));
        assertEquals(false, new LongValueRange(0L, 10L, 2L).contains(3L));
        assertEquals(true, new LongValueRange(-1L, 9L, 2L).contains(1L));
        assertEquals(false, new LongValueRange(-1L, 9L, 2L).contains(2L));
        assertEquals(true, new LongValueRange(100L, 120L, 5L).contains(115L));
        assertEquals(false, new LongValueRange(100L, 120L, 5L).contains(114L));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new LongValueRange(0L, 7L).createOriginalIterator(), 0L, 1L, 2L, 3L, 4L, 5L, 6L);
        assertAllElementsOfIterator(new LongValueRange(100L, 104L).createOriginalIterator(), 100L, 101L, 102L, 103L);
        assertAllElementsOfIterator(new LongValueRange(-4L, 3L).createOriginalIterator(), -4L, -3L, -2L, -1L, 0L, 1L, 2L);
        assertAllElementsOfIterator(new LongValueRange(7L, 7L).createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(new LongValueRange(0L, 10L, 2L).createOriginalIterator(), 0L, 2L, 4L, 6L, 8L);
        assertAllElementsOfIterator(new LongValueRange(-1L, 9L, 2L).createOriginalIterator(), -1L, 1L, 3L, 5L, 7L);
        assertAllElementsOfIterator(new LongValueRange(100L, 120L, 5L).createOriginalIterator(), 100L, 105L, 110L, 115L);
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);

        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(0L, 7L).createRandomIterator(workingRandom), 3L, 0L);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(100L, 104L).createRandomIterator(workingRandom), 103L, 100L);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(-4L, 3L).createRandomIterator(workingRandom), -1L, -4L);
        assertAllElementsOfIterator(new LongValueRange(7L, 7L).createRandomIterator(workingRandom));
        // IncrementUnit
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(0L, 10L, 2L).createRandomIterator(workingRandom), 6L, 0L);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(-1L, 9L, 2L).createRandomIterator(workingRandom), 5L, -1L);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new LongValueRange(100L, 120L, 5L).createRandomIterator(workingRandom), 115L, 100L);
    }

}
