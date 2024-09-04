/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZonedDateTimeRangeIteratorTest {

    private static ZonedDateTime before;
    private static ZonedDateTime after;

    @BeforeAll
    static void setup() {
        // Tests using on those variables relies on the exact hour/minute, because the ZonedDateTimeRangeIterator is meant to iterate on a full-day base
        before = getZonedDateTime(2021, 1, 1, 10, 15);
        after = getZonedDateTime(2021, 1, 3, 10, 15);
    }

    @Test
    void hasNextAscendantTest() {
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(before, after);
        assertThat(iterator).hasNext();
        ZonedDateTime next = iterator.next();
        while (next.isBefore(after)) {
            assertThat(iterator).hasNext();
            next = iterator.next();
        }
        assertThat(iterator).isExhausted();
    }

    @Test
    void hasNextDescendantTest() {
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(after, before);
        assertThat(iterator).hasNext();
        ZonedDateTime next = iterator.next();
        while (!next.equals(before)) {
            assertThat(iterator).hasNext();
            next = iterator.next();
        }
        assertThat(iterator).isExhausted();
    }

    @Test
    void nextAscendantTest() {
        List<ZonedDateTime> expected = Arrays.asList(getZonedDateTime(2021, 1, 1, 10, 15),
                getZonedDateTime(2021, 1, 2, 10, 15),
                getZonedDateTime(2021, 1, 3, 10, 15));
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(before, after);
        IntStream.range(0, 3).forEach(i -> assertThat(expected.get(i)).isEqualTo(iterator.next()));
    }

    @Test
    void nextDescendantTest() {
        List<ZonedDateTime> expected = Arrays.asList(getZonedDateTime(2021, 1, 3, 10, 15),
                getZonedDateTime(2021, 1, 2, 10, 15),
                getZonedDateTime(2021, 1, 1, 10, 15));
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(after, before);
        IntStream.range(0, 3).forEach(i -> assertThat(expected.get(i)).isEqualTo(iterator.next()));
    }


    @Test
    void hasNotNextAscendantTest() {
        ZonedDateTime start = getZonedDateTime(2021, 1, 1, 10, 15);
        ZonedDateTime end = getZonedDateTime(2021, 1, 2, 10, 14);
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(start, end);
        ZonedDateTime retrieved = iterator.next();
        assertThat(retrieved).isEqualTo(start);
        assertThat(iterator).isExhausted();
    }

    @Test
    void hasNotNextDescendantTest() {
        ZonedDateTime start = getZonedDateTime(2021, 1, 2, 10, 14);
        ZonedDateTime end = getZonedDateTime(2021, 1, 1, 10, 15);
        ZonedDateTimeRangeIterator iterator = new ZonedDateTimeRangeIterator(start, end);
        ZonedDateTime retrieved = iterator.next();
        assertThat(retrieved).isEqualTo(start);
        assertThat(iterator).isExhausted();
    }

    private static ZonedDateTime getZonedDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        return ZonedDateTime.of(startDateTime, ZoneId.systemDefault());
    }


}