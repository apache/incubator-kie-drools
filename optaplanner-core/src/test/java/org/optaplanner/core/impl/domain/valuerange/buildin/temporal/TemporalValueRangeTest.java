/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.valuerange.buildin.temporal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class TemporalValueRangeTest {

    @Test
    public void getSizeForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 8);
        assertEquals(7L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(1L, new TemporalValueRange<>(from, to, 7, ChronoUnit.DAYS).getSize());
        assertEquals(1L, new TemporalValueRange<>(from, to, 1, ChronoUnit.WEEKS).getSize());

        from = LocalDate.of(2016, 7, 7);
        to = LocalDate.of(2016, 7, 17);
        assertEquals(10L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(5L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(2L, new TemporalValueRange<>(from, to, 5, ChronoUnit.DAYS).getSize());
        from = LocalDate.of(2016, 7, 7);
        to = LocalDate.of(2016, 7, 7);
        assertEquals(0L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 2, 28); // Exactly 1 month later
        assertEquals(28L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(14L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(1L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 17);
        assertEquals(198L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(99L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 1);
        assertEquals(6L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2015, 12, 20);
        to = LocalDate.of(2016, 1, 21);
        assertEquals(32L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(16L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        from = LocalDate.of(2015, 12, 20);
        to = LocalDate.of(2016, 1, 20);
        assertEquals(1L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2050, 7, 7);
        assertEquals(32702L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(16351L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2050, 6, 24);
        assertEquals(1074L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());
        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2049, 12, 24);
        assertEquals(89L, new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).getSize());
    }

    @Test
    public void getSizeForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize());

        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 8);
        assertEquals(1L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize());

        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 7, 7, 7, 7);
        assertEquals(5L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize());
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 7, 7, 7);
        assertEquals(158L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize());
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 7, 7);
        assertEquals(3797L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize());
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 12, 7);
        assertEquals(227825L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize());
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 12, 12);
        assertEquals(13669505L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize());
    }

    @Test
    public void getForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 8);
        assertEquals(LocalDate.of(2016, 7, 1), new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(0));
        assertEquals(LocalDate.of(2016, 7, 2), new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(1));
        assertEquals(LocalDate.of(2016, 7, 7), new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(6));

        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 1);
        assertEquals(LocalDate.of(2016, 1, 1), new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).get(0));
        assertEquals(LocalDate.of(2016, 6, 1), new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).get(5));

        from = LocalDate.of(1992, 1, 1);
        to = LocalDate.of(2016, 1, 1);
        assertEquals(LocalDate.of(1992, 1, 1), new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).get(0));
        assertEquals(LocalDate.of(2015, 1, 1), new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).get(23));
    }

    @Test
    public void getForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 2), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(1));
        assertEquals(LocalDateTime.of(2016, 7, 7, 7, 7, 6), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(16265165));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 1);
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 2, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(1));
        assertEquals(LocalDateTime.of(2016, 7, 7, 7, 6, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(271085));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 1, 1);
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 2, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(1));
        assertEquals(LocalDateTime.of(2016, 7, 7, 6, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(4517));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 2, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(1));
        assertEquals(LocalDateTime.of(2016, 7, 6, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(187));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 1, 1, 1, 1);
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(0));
        assertEquals(LocalDateTime.of(2016, 2, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(1));
        assertEquals(LocalDateTime.of(2016, 6, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(5));
    }

    @Test
    public void containsForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 9, 8);
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 6, 30)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2015, 7, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 7, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 7, 3)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 9, 7)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 9, 8)));

        from = LocalDate.of(2016, 7, 1);
        to = LocalDate.of(2016, 9, 1);
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 6, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 8, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 9, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 7)));

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 2, 28);
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1)));

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 3, 31);
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 30)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 31)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 4, 1)));

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 4, 30);
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 30)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 31)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 4, 1)));
    }

    @Test
    public void containsForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 1, 1);
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 6, 23, 59, 59)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 6, 1, 1)));
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 7)));

        fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 1);
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 0, 59, 59)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 7, 6, 1)));
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 1)));

        fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).contains(LocalDateTime.of(2016, 7, 7, 1, 0, 59)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 6)));
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 7)));
    }

    @Test
    public void createOriginalIteratorForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 10);
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).createOriginalIterator(),
                LocalDate.of(2016, 7, 1),
                LocalDate.of(2016, 7, 2),
                LocalDate.of(2016, 7, 3),
                LocalDate.of(2016, 7, 4),
                LocalDate.of(2016, 7, 5),
                LocalDate.of(2016, 7, 6),
                LocalDate.of(2016, 7, 7),
                LocalDate.of(2016, 7, 8),
                LocalDate.of(2016, 7, 9));
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 3, ChronoUnit.DAYS).createOriginalIterator(),
                LocalDate.of(2016, 7, 1),
                LocalDate.of(2016, 7, 4),
                LocalDate.of(2016, 7, 7));

        from = LocalDate.of(2016, 9, 3);
        to = LocalDate.of(2017, 3, 3);
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).createOriginalIterator(),
                LocalDate.of(2016, 9, 3),
                LocalDate.of(2016, 10, 3),
                LocalDate.of(2016, 11, 3),
                LocalDate.of(2016, 12, 3),
                LocalDate.of(2017, 1, 3),
                LocalDate.of(2017, 2, 3));

        from = LocalDate.of(1999, 9, 3);
        to = LocalDate.of(2003, 9, 3);
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).createOriginalIterator(),
                LocalDate.of(1999, 9, 3),
                LocalDate.of(2000, 9, 3),
                LocalDate.of(2001, 9, 3),
                LocalDate.of(2002, 9, 3));
    }

    @Test
    public void createOriginalIteratorForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 1, 4, 5, 12);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 3, 4, 5, 12);
        assertAllElementsOfIterator(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).createOriginalIterator(),
                LocalDateTime.of(2016, 7, 1, 4, 5, 12),
                LocalDateTime.of(2016, 7, 2, 4, 5, 12));

        fromTime = LocalDateTime.of(2016, 7, 1, 23, 5, 12);
        toTime = LocalDateTime.of(2016, 7, 2, 5, 5, 12);
        assertAllElementsOfIterator(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).createOriginalIterator(),
                LocalDateTime.of(2016, 7, 1, 23, 5, 12),
                LocalDateTime.of(2016, 7, 2, 0, 5, 12),
                LocalDateTime.of(2016, 7, 2, 1, 5, 12),
                LocalDateTime.of(2016, 7, 2, 2, 5, 12),
                LocalDateTime.of(2016, 7, 2, 3, 5, 12),
                LocalDateTime.of(2016, 7, 2, 4, 5, 12));
    }

    @Test
    public void createRandomIteratorForLocalDate() {
        Random workingRandom = mock(Random.class);

        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 11);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).createRandomIterator(workingRandom),
                LocalDate.of(2016, 7, 4));
        when(workingRandom.nextInt(anyInt())).thenReturn(0, 0);
        assertElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).createRandomIterator(workingRandom),
                LocalDate.of(2016, 7, 1));
        when(workingRandom.nextInt(anyInt())).thenReturn(1, 0);
        assertElementsOfIterator(new TemporalValueRange<>(from, to, 5, ChronoUnit.DAYS).createRandomIterator(workingRandom),
                LocalDate.of(2016, 7, 6));
    }

    @Test
    public void createRandomIteratorForLocalDateTime() {
        Random workingRandom = mock(Random.class);

        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 1, 4, 5, 12);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 3, 12, 15, 12);
        when(workingRandom.nextInt(anyInt())).thenReturn(3, 0);
        assertElementsOfIterator(new TemporalValueRange<>(fromTime, toTime, 10, ChronoUnit.MINUTES).createRandomIterator(workingRandom),
                LocalDateTime.of(2016, 7, 1, 4, 35, 12));
    }

    @Test
    public void emptyRandomIterator() {
        Iterator<Year> it = new TemporalValueRange<>(Year.of(0), Year.of(0), 1, ChronoUnit.YEARS)
                .createRandomIterator(new Random(0));
        assertFalse(it.hasNext());
    }

    @Test
    public void getAndCreateOriginalIteratorInSyncForLocalDate() {
        LocalDate from = LocalDate.of(2016, 1, 31);
        LocalDate to = LocalDate.of(2016, 7, 31);
        TemporalValueRange<LocalDate> temporalValueRange = new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS);

        assertEquals(6, temporalValueRange.getSize());
        assertEquals(LocalDate.of(2016, 1, 31), temporalValueRange.get(0));
        assertEquals(LocalDate.of(2016, 2, 29), temporalValueRange.get(1));
        assertEquals(LocalDate.of(2016, 3, 31), temporalValueRange.get(2));
        assertEquals(LocalDate.of(2016, 4, 30), temporalValueRange.get(3));
        assertEquals(LocalDate.of(2016, 5, 31), temporalValueRange.get(4));
        assertEquals(LocalDate.of(2016, 6, 30), temporalValueRange.get(5));
        assertAllElementsOfIterator(temporalValueRange.createOriginalIterator(),
                LocalDate.of(2016, 1, 31),
                LocalDate.of(2016, 2, 29),
                LocalDate.of(2016, 3, 31),
                LocalDate.of(2016, 4, 30),
                LocalDate.of(2016, 5, 31),
                LocalDate.of(2016, 6, 30));
    }

    @Test
    public void fullLocalDateRange() {
        TemporalUnit unit = ChronoUnit.DAYS;
        LocalDate from = LocalDate.MIN;
        LocalDate to = LocalDate.MAX;
        int increment = 4093;
        TemporalValueRange<LocalDate> range = new TemporalValueRange<>(from, to, increment, unit);
        assertEquals(from.until(to, unit), range.getSize() * increment);
        assertTrue(range.contains(from));
        assertFalse(range.contains(to));
    }

    @Test
    public void fullLocalDateTimeRange() {
        TemporalUnit unit = ChronoUnit.DAYS;
        LocalDateTime from = LocalDateTime.MIN;
        LocalDateTime to = LocalDateTime.MAX.truncatedTo(unit);
        int increment = 1;
        TemporalValueRange<LocalDateTime> range = new TemporalValueRange<>(from, to, increment, unit);
        assertEquals(from.until(to, unit), range.getSize() * increment);
        assertTrue(range.contains(from));
        assertFalse(range.contains(to));
    }

    @Test
    public void fullYearRange() {
        TemporalUnit unit = ChronoUnit.YEARS;
        Year from = Year.of(Year.MIN_VALUE);
        Year to = Year.of(Year.MAX_VALUE);
        int increment = 1;
        TemporalValueRange<Year> range = new TemporalValueRange<>(from, to, increment, unit);
        assertEquals(from.until(to, unit), range.getSize() * increment);
        assertTrue(range.contains(from));
        assertFalse(range.contains(to));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFrom() {
        assertNotNull(new TemporalValueRange<>(null, LocalDate.MAX, 1, ChronoUnit.DAYS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullTo() {
        assertNotNull(new TemporalValueRange<>(LocalDate.MIN, null, 1, ChronoUnit.DAYS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIncrementUnitType() {
        assertNotNull(new TemporalValueRange<>(LocalDate.MIN, LocalDate.MAX, 1, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidIncrementUnitAmount() {
        assertNotNull(new TemporalValueRange<>(LocalDate.MIN, LocalDate.MAX, 0, ChronoUnit.DAYS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromAfterTo() {
        assertNotNull(new TemporalValueRange<>(LocalDate.MAX, LocalDate.MIN, 1, ChronoUnit.DAYS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void incrementUnitTypeNotSupportedByFrom() {
        TemporalMock from = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.FALSE);
        TemporalMock to = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.TRUE);
        assertNotNull(new TemporalValueRange<>(from, to, 1, mock(TemporalUnit.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void incrementUnitTypeNotSupportedByTo() {
        TemporalMock from = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.TRUE);
        TemporalMock to = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.FALSE);
        assertNotNull(new TemporalValueRange<>(from, to, 1, mock(TemporalUnit.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void remainderOnIncrementAmount() {
        assertNotNull(new TemporalValueRange<>(Year.of(0), Year.of(3), 2, ChronoUnit.YEARS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void remainderOnIncrementType() {
        LocalTime from = LocalTime.of(11, 30);
        LocalTime to = LocalTime.of(13, 29);
        assertNotNull(new TemporalValueRange<>(from, to, 1, ChronoUnit.HOURS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void remainderOnIncrementTypeExceedsMaximumYear() {
        Year from = Year.of(Year.MIN_VALUE);
        Year to = Year.of(Year.MAX_VALUE - 0);
        assertNotEquals(0, (to.getValue() - from.getValue()) % 10); // Maximum Year range is not divisible by 10
        assertNotNull(new TemporalValueRange<>(from, to, 1, ChronoUnit.DECADES));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndexNegative() {
        new TemporalValueRange<>(Year.of(0), Year.of(1), 1, ChronoUnit.YEARS).get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndexGreaterThanSize() {
        TemporalValueRange<Year> range = new TemporalValueRange<>(Year.of(0), Year.of(1), 1, ChronoUnit.YEARS);
        assertEquals(1L, range.getSize());
        range.get(1);
    }

    private static interface TemporalMock extends Temporal, Comparable<Temporal> {

    }
}
