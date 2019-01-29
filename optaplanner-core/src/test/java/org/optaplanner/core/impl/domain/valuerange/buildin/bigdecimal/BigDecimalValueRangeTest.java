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

package org.optaplanner.core.impl.domain.valuerange.buildin.bigdecimal;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class BigDecimalValueRangeTest {

    @Test
    public void getSize() {
        assertEquals(10L, new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).getSize());
        assertEquals(200L, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).getSize());
        assertEquals(4007L, new BigDecimalValueRange(new BigDecimal("-15.00"), new BigDecimal("25.07")).getSize());
        assertEquals(0L, new BigDecimalValueRange(new BigDecimal("7.0"), new BigDecimal("7.0")).getSize());
        // IncrementUnit
        assertEquals(5L, new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).getSize());
        assertEquals(5L, new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).getSize());
        assertEquals(4L, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).getSize());
    }

    @Test
    public void get() {
        assertEquals(new BigDecimal("3"), new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).get(3L));
        assertEquals(new BigDecimal("100.3"), new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).get(3L));
        assertEquals(new BigDecimal("-4"), new BigDecimalValueRange(new BigDecimal("-5"), new BigDecimal("25")).get(1L));
        assertEquals(new BigDecimal("-4.94"), new BigDecimalValueRange(new BigDecimal("-5.00"), new BigDecimal("25.00")).get(6L));
        // IncrementUnit
        assertEquals(new BigDecimal("6.0"), new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).get(3L));
        assertEquals(new BigDecimal("5.0"), new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).get(3L));
        assertEquals(new BigDecimal("115.3"), new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).get(3L));
    }

    @Test
    public void contains() {
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(new BigDecimal("3")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(new BigDecimal("10")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(null));
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).contains(new BigDecimal("100.0")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).contains(new BigDecimal("99.9")));
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("-5.3"), new BigDecimal("25.2")).contains(new BigDecimal("-5.2")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("-5.3"), new BigDecimal("25.2")).contains(new BigDecimal("-5.4")));
        // IncrementUnit
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).contains(new BigDecimal("2.0")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).contains(new BigDecimal("3.0")));
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).contains(new BigDecimal("1.0")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).contains(new BigDecimal("2.0")));
        assertEquals(true, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).contains(new BigDecimal("115.3")));
        assertEquals(false, new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).contains(new BigDecimal("115.0")));
    }

    @Test
    public void createOriginalIterator() {
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createOriginalIterator(), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createOriginalIterator(), new BigDecimal("100.0"), new BigDecimal("100.1"), new BigDecimal("100.2"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createOriginalIterator(), new BigDecimal("-4.00"), new BigDecimal("-3.99"), new BigDecimal("-3.98"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .createOriginalIterator(), new BigDecimal("0.0"), new BigDecimal("2.0"), new BigDecimal("4.0"),
                new BigDecimal("6.0"), new BigDecimal("8.0"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                .createOriginalIterator(), new BigDecimal("-1.0"), new BigDecimal("1.0"), new BigDecimal("3.0"),
                new BigDecimal("5.0"), new BigDecimal("7.0"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .createOriginalIterator(), new BigDecimal("100.0"), new BigDecimal("105.1"), new BigDecimal("110.2"),
                new BigDecimal("115.3"));
    }

    @Test
    public void createRandomIterator() {
        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createRandomIterator(workingRandom), new BigDecimal("3"), new BigDecimal("0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createRandomIterator(workingRandom), new BigDecimal("100.3"), new BigDecimal("100.0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createRandomIterator(workingRandom), new BigDecimal("-3.97"), new BigDecimal("-4.00"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createRandomIterator(workingRandom));
        // IncrementUnit
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .createRandomIterator(workingRandom), new BigDecimal("6.0"), new BigDecimal("0.0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                .createRandomIterator(workingRandom), new BigDecimal("5.0"), new BigDecimal("-1.0"));
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .createRandomIterator(workingRandom), new BigDecimal("115.3"), new BigDecimal("100.0"));
    }

}
