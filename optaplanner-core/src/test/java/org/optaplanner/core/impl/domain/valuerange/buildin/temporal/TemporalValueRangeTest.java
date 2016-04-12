package org.optaplanner.core.impl.domain.valuerange.buildin.temporal;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
        assertEquals(0L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2016, 1, 31);
        to = LocalDate.of(2016, 3, 1); // exactly 1 month later
        assertEquals(30L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(15L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(1L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 17);
        assertEquals(198L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(99L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(6L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(2015, 12, 20);
        to = LocalDate.of(2016, 1, 21);
        assertEquals(32L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(16L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(1L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());

        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2050, 7, 7);
        assertEquals(32702L, new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize());
        assertEquals(16351L, new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize());
        assertEquals(1074L, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize());
        assertEquals(89L, new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).getSize());
    }

    @Test
    public void getSizeForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 8);
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize());
        assertEquals(0L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize());
        assertEquals(1L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize());

        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 12, 12);
        assertEquals(5L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize());
        assertEquals(158L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize());
        assertEquals(3797L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize());
        assertEquals(227825L, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize());
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
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(0));

        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 1, 2), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(1));
        assertEquals(LocalDateTime.of(2016, 1, 1, 1, 2, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(1));
        assertEquals(LocalDateTime.of(2016, 1, 1, 2, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(1));
        assertEquals(LocalDateTime.of(2016, 1, 2, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(1));
        assertEquals(LocalDateTime.of(2016, 2, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(1));

        assertEquals(LocalDateTime.of(2016, 6, 1, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(5));
        assertEquals(LocalDateTime.of(2016, 7, 6, 1, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(187));
        assertEquals(LocalDateTime.of(2016, 7, 7, 6, 1, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(4517));
        assertEquals(LocalDateTime.of(2016, 7, 7, 7, 6, 1), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(271085));
        assertEquals(LocalDateTime.of(2016, 7, 7, 7, 7, 6), new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(16265165));
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

        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 6, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 1)));
        assertEquals(true, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 8, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 9, 1)));
        assertEquals(false, new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 7)));
    }

    @Test
    public void containsForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 6, 23, 59, 59)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 6, 1, 1)));
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 7)));

        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 0, 59, 59)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1)));
        assertEquals(true, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 7, 6, 1)));
        assertEquals(false, new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).contains(LocalDateTime.of(2016, 7, 7, 7, 7, 1)));

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
        to = LocalDate.of(2017, 3, 12);
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).createOriginalIterator(),
                LocalDate.of(2016, 9, 3),
                LocalDate.of(2016, 10, 3),
                LocalDate.of(2016, 11, 3),
                LocalDate.of(2016, 12, 3),
                LocalDate.of(2017, 1, 3),
                LocalDate.of(2017, 2, 3));

        from = LocalDate.of(1999, 9, 3);
        to = LocalDate.of(2004, 3, 12);
        assertAllElementsOfIterator(new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).createOriginalIterator(),
                LocalDate.of(1999, 9, 3),
                LocalDate.of(2000, 9, 3),
                LocalDate.of(2001, 9, 3),
                LocalDate.of(2002, 9, 3));
    }

    @Test
    public void createOriginalIteratorForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 1, 4, 5, 12);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 3, 12, 12, 17);
        assertAllElementsOfIterator(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).createOriginalIterator(),
                LocalDateTime.of(2016, 7, 1, 4, 5, 12),
                LocalDateTime.of(2016, 7, 2, 4, 5, 12));

        fromTime = LocalDateTime.of(2016, 7, 1, 23, 5, 12);
        toTime = LocalDateTime.of(2016, 7, 2, 5, 12, 17);
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

}
