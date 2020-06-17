/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class TemporalValueRangeTest {

    @Test
    public void getSizeForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 8);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(7L);
        assertThat(new TemporalValueRange<>(from, to, 7, ChronoUnit.DAYS).getSize()).isEqualTo(1L);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.WEEKS).getSize()).isEqualTo(1L);

        from = LocalDate.of(2016, 7, 7);
        to = LocalDate.of(2016, 7, 17);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(10L);
        assertThat(new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize()).isEqualTo(5L);
        assertThat(new TemporalValueRange<>(from, to, 5, ChronoUnit.DAYS).getSize()).isEqualTo(2L);
        from = LocalDate.of(2016, 7, 7);
        to = LocalDate.of(2016, 7, 7);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(0L);

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 2, 28); // Exactly 1 month later
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(28L);
        assertThat(new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize()).isEqualTo(14L);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(1L);

        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 17);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(198L);
        assertThat(new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize()).isEqualTo(99L);
        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 1);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(6L);

        from = LocalDate.of(2015, 12, 20);
        to = LocalDate.of(2016, 1, 21);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(32L);
        assertThat(new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize()).isEqualTo(16L);
        from = LocalDate.of(2015, 12, 20);
        to = LocalDate.of(2016, 1, 20);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(1L);

        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2050, 7, 7);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).getSize()).isEqualTo(32702L);
        assertThat(new TemporalValueRange<>(from, to, 2, ChronoUnit.DAYS).getSize()).isEqualTo(16351L);
        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2050, 6, 24);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(1074L);
        from = LocalDate.of(1960, 12, 24);
        to = LocalDate.of(2049, 12, 24);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).getSize()).isEqualTo(89L);
    }

    @Test
    public void getSizeForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(0L);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize()).isEqualTo(0L);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize()).isEqualTo(0L);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize()).isEqualTo(0L);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize()).isEqualTo(0L);

        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 8);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize()).isEqualTo(1L);

        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 7, 7, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).getSize()).isEqualTo(5L);
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 7, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).getSize()).isEqualTo(158L);
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).getSize()).isEqualTo(3797L);
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 12, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).getSize()).isEqualTo(227825L);
        fromTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        toTime = LocalDateTime.of(2016, 12, 12, 12, 12, 12);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).getSize()).isEqualTo(13669505L);
    }

    @Test
    public void getForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 7, 8);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(0)).isEqualTo(LocalDate.of(2016, 7, 1));
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(1)).isEqualTo(LocalDate.of(2016, 7, 2));
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).get(6)).isEqualTo(LocalDate.of(2016, 7, 7));

        from = LocalDate.of(2016, 1, 1);
        to = LocalDate.of(2016, 7, 1);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).get(0)).isEqualTo(LocalDate.of(2016, 1, 1));
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).get(5)).isEqualTo(LocalDate.of(2016, 6, 1));

        from = LocalDate.of(1992, 1, 1);
        to = LocalDate.of(2016, 1, 1);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).get(0)).isEqualTo(LocalDate.of(1992, 1, 1));
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.YEARS).get(23)).isEqualTo(LocalDate.of(2015, 1, 1));
    }

    @Test
    public void getForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(0))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(1))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 2));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS).get(16265165))
                .isEqualTo(LocalDateTime.of(2016, 7, 7, 7, 7, 6));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(0))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(1))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 2, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES).get(271085))
                .isEqualTo(LocalDateTime.of(2016, 7, 7, 7, 6, 1));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 1, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(0))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(1))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 2, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS).get(4517))
                .isEqualTo(LocalDateTime.of(2016, 7, 7, 6, 1, 1));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(0))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(1))
                .isEqualTo(LocalDateTime.of(2016, 1, 2, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.DAYS).get(187))
                .isEqualTo(LocalDateTime.of(2016, 7, 6, 1, 1, 1));

        fromTime = LocalDateTime.of(2016, 1, 1, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 1, 1, 1, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(0))
                .isEqualTo(LocalDateTime.of(2016, 1, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(1))
                .isEqualTo(LocalDateTime.of(2016, 2, 1, 1, 1, 1));
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MONTHS).get(5))
                .isEqualTo(LocalDateTime.of(2016, 6, 1, 1, 1, 1));
    }

    @Test
    public void containsForLocalDate() {
        LocalDate from = LocalDate.of(2016, 7, 1);
        LocalDate to = LocalDate.of(2016, 9, 8);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 6, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2015, 7, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 7, 1))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 7, 3))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 9, 7))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.DAYS).contains(LocalDate.of(2016, 9, 8))).isFalse();

        from = LocalDate.of(2016, 7, 1);
        to = LocalDate.of(2016, 9, 1);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 6, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 1))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 8, 1))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 9, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2016, 7, 7))).isFalse();

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 2, 28);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1))).isFalse();

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 3, 31);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 31))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 4, 1))).isFalse();

        from = LocalDate.of(2017, 1, 31);
        to = LocalDate.of(2017, 4, 30);
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 1, 31))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 27))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 2, 28))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 1))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 30))).isFalse();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 3, 31))).isTrue();
        assertThat(new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS).contains(LocalDate.of(2017, 4, 1))).isFalse();
    }

    @Test
    public void containsForLocalDateTime() {
        LocalDateTime fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        LocalDateTime toTime = LocalDateTime.of(2016, 7, 7, 7, 1, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS)
                .contains(LocalDateTime.of(2016, 7, 6, 23, 59, 59))).isFalse();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS)
                .contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS)
                .contains(LocalDateTime.of(2016, 7, 7, 6, 1, 1))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.HOURS)
                .contains(LocalDateTime.of(2016, 7, 7, 7, 7, 7))).isFalse();

        fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 1);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES)
                .contains(LocalDateTime.of(2016, 7, 7, 0, 59, 59))).isFalse();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES)
                .contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES)
                .contains(LocalDateTime.of(2016, 7, 7, 7, 6, 1))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.MINUTES)
                .contains(LocalDateTime.of(2016, 7, 7, 7, 7, 1))).isFalse();

        fromTime = LocalDateTime.of(2016, 7, 7, 1, 1, 1);
        toTime = LocalDateTime.of(2016, 7, 7, 7, 7, 7);
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS)
                .contains(LocalDateTime.of(2016, 7, 7, 1, 0, 59))).isFalse();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS)
                .contains(LocalDateTime.of(2016, 7, 7, 1, 1, 1))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS)
                .contains(LocalDateTime.of(2016, 7, 7, 7, 7, 6))).isTrue();
        assertThat(new TemporalValueRange<>(fromTime, toTime, 1, ChronoUnit.SECONDS)
                .contains(LocalDateTime.of(2016, 7, 7, 7, 7, 7))).isFalse();
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
        assertElementsOfIterator(
                new TemporalValueRange<>(fromTime, toTime, 10, ChronoUnit.MINUTES).createRandomIterator(workingRandom),
                LocalDateTime.of(2016, 7, 1, 4, 35, 12));
    }

    @Test
    public void emptyRandomIterator() {
        Iterator<Year> it = new TemporalValueRange<>(Year.of(0), Year.of(0), 1, ChronoUnit.YEARS)
                .createRandomIterator(new Random(0));
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void getAndCreateOriginalIteratorInSyncForLocalDate() {
        LocalDate from = LocalDate.of(2016, 1, 31);
        LocalDate to = LocalDate.of(2016, 7, 31);
        TemporalValueRange<LocalDate> temporalValueRange = new TemporalValueRange<>(from, to, 1, ChronoUnit.MONTHS);

        assertThat(temporalValueRange.getSize()).isEqualTo(6);
        assertThat(temporalValueRange.get(0)).isEqualTo(LocalDate.of(2016, 1, 31));
        assertThat(temporalValueRange.get(1)).isEqualTo(LocalDate.of(2016, 2, 29));
        assertThat(temporalValueRange.get(2)).isEqualTo(LocalDate.of(2016, 3, 31));
        assertThat(temporalValueRange.get(3)).isEqualTo(LocalDate.of(2016, 4, 30));
        assertThat(temporalValueRange.get(4)).isEqualTo(LocalDate.of(2016, 5, 31));
        assertThat(temporalValueRange.get(5)).isEqualTo(LocalDate.of(2016, 6, 30));
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
        assertThat(range.getSize() * increment).isEqualTo(from.until(to, unit));
        assertThat(range.contains(from)).isTrue();
        assertThat(range.contains(to)).isFalse();
    }

    @Test
    public void fullLocalDateTimeRange() {
        TemporalUnit unit = ChronoUnit.DAYS;
        LocalDateTime from = LocalDateTime.MIN;
        LocalDateTime to = LocalDateTime.MAX.truncatedTo(unit);
        int increment = 1;
        TemporalValueRange<LocalDateTime> range = new TemporalValueRange<>(from, to, increment, unit);
        assertThat(range.getSize() * increment).isEqualTo(from.until(to, unit));
        assertThat(range.contains(from)).isTrue();
        assertThat(range.contains(to)).isFalse();
    }

    @Test
    public void fullYearRange() {
        TemporalUnit unit = ChronoUnit.YEARS;
        Year from = Year.of(Year.MIN_VALUE);
        Year to = Year.of(Year.MAX_VALUE);
        int increment = 1;
        TemporalValueRange<Year> range = new TemporalValueRange<>(from, to, increment, unit);
        assertThat(range.getSize() * increment).isEqualTo(from.until(to, unit));
        assertThat(range.contains(from)).isTrue();
        assertThat(range.contains(to)).isFalse();
    }

    @Test
    public void nullFrom() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(null, LocalDate.MAX, 1, ChronoUnit.DAYS));
    }

    @Test
    public void nullTo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(LocalDate.MIN, null, 1, ChronoUnit.DAYS));
    }

    @Test
    public void nullIncrementUnitType() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(LocalDate.MIN, LocalDate.MAX, 1, null));
    }

    @Test
    public void invalidIncrementUnitAmount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(LocalDate.MIN, LocalDate.MAX, 0, ChronoUnit.DAYS));
    }

    @Test
    public void fromAfterTo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(LocalDate.MAX, LocalDate.MIN, 1, ChronoUnit.DAYS));
    }

    @Test
    public void incrementUnitTypeNotSupportedByFrom() {
        TemporalMock from = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.FALSE);
        TemporalMock to = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.TRUE);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(from, to, 1, mock(TemporalUnit.class)));
    }

    @Test
    public void incrementUnitTypeNotSupportedByTo() {
        TemporalMock from = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.TRUE);
        TemporalMock to = mock(TemporalMock.class);
        when(from.isSupported(any(TemporalUnit.class))).thenReturn(Boolean.FALSE);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(from, to, 1, mock(TemporalUnit.class)));
    }

    @Test
    public void remainderOnIncrementAmount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(Year.of(0), Year.of(3), 2, ChronoUnit.YEARS));
    }

    @Test
    public void remainderOnIncrementType() {
        LocalTime from = LocalTime.of(11, 30);
        LocalTime to = LocalTime.of(13, 29);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(from, to, 1, ChronoUnit.HOURS));
    }

    @Test
    public void remainderOnIncrementTypeExceedsMaximumYear() {
        Year from = Year.of(Year.MIN_VALUE);
        Year to = Year.of(Year.MAX_VALUE - 0);
        // Maximum Year range is not divisible by 10
        assertThat((to.getValue() - from.getValue()) % 10).isNotEqualTo(0);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new TemporalValueRange<>(from, to, 1, ChronoUnit.DECADES));
    }

    @Test
    public void getIndexNegative() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> new TemporalValueRange<>(Year.of(0), Year.of(1), 1, ChronoUnit.YEARS).get(-1));
    }

    @Test
    public void getIndexGreaterThanSize() {
        TemporalValueRange<Year> range = new TemporalValueRange<>(Year.of(0), Year.of(1), 1, ChronoUnit.YEARS);
        assertThat(range.getSize()).isEqualTo(1L);
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> range.get(1));
    }

    private static interface TemporalMock extends Temporal, Comparable<Temporal> {

    }
}
