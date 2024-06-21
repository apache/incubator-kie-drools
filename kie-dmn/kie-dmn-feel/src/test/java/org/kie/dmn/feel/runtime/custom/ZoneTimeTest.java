/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.custom;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.kie.dmn.feel.runtime.custom.ZoneTime.ZONED_OFFSET_WITH_SECONDS;
import static org.kie.dmn.feel.runtime.custom.ZoneTime.ZONED_OFFSET_WITHOUT_SECONDS;

class ZoneTimeTest {

    private static final String REFERENCED_TIME = "10:15:00";
    private static final String REFERENCED_ZONE = "Australia/Melbourne";
    private static LocalTime localTime ;
    private static ZoneId zoneId;
    private static OffsetTime offsetTime;
    private static ZoneTime zoneTime;

    @BeforeAll
    static void setUpClass() {
        localTime = DateTimeFormatter.ISO_TIME.parse(REFERENCED_TIME, LocalTime::from );
        zoneId = ZoneId.of(REFERENCED_ZONE);
        offsetTime = getCorrectOffsetTime();
        zoneTime = ZoneTime.of(localTime, zoneId, true);
    }

    @Test
    void of() {
        ZoneTime retrieved = ZoneTime.of(localTime, zoneId, true);
        assertNotNull(retrieved);
        assertEquals(offsetTime, retrieved.getOffsetTime());
        assertEquals(zoneId, retrieved.getZoneId());
    }


    @Test
    void getTimezone() {
        assertEquals(REFERENCED_ZONE, zoneTime.getTimezone());
    }

    @Test
    void compareTo() {
        ZoneTime toCompare = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        OffsetTime comparison = toCompare.getOffsetTime();
        assertEquals(offsetTime.compareTo(comparison), zoneTime.compareTo(toCompare));

    }

    @Test
    void withTemporalField() {
        ZoneTime expected = new ZoneTime(offsetTime.with(ChronoField.HOUR_OF_DAY, 3), zoneId, false);
        assertEquals(expected, zoneTime.with(ChronoField.HOUR_OF_DAY, 3));
    }

    @Test
    void withTemporalAdjuster() {
        TemporalAdjuster adjuster = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId
                , false);
        ZoneTime expected = new ZoneTime(offsetTime.with(adjuster), zoneId, false);
        assertEquals(expected, zoneTime.with(adjuster));
        adjuster = DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from );
        expected = new ZoneTime(offsetTime.with(adjuster), zoneId, false);
        assertEquals(expected, zoneTime.with(adjuster));
    }


    @Test
    void plusLong() {
        ZoneTime expected = new ZoneTime(offsetTime.plus(3, ChronoUnit.HOURS), zoneId, false);
        assertEquals(expected, zoneTime.plus(3, ChronoUnit.HOURS));
    }

    @Test
    void plusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        ZoneTime expected = new ZoneTime(offsetTime.plus(amount), zoneId, false);
        assertEquals(expected, zoneTime.plus(amount));
    }

    @Test
    void minusLong() {
        ZoneTime expected = new ZoneTime(offsetTime.minus(3, ChronoUnit.HOURS), zoneId, false);
        assertEquals(expected, zoneTime.minus(3, ChronoUnit.HOURS));
    }

    @Test
    void minusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        ZoneTime expected = new ZoneTime(offsetTime.minus(amount), zoneId, false);
        assertEquals(expected, zoneTime.minus(amount));
    }

    @Test
    void until() {
        ZoneTime endExclusive = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId,
                                            false);
        long expected = offsetTime.until(endExclusive, ChronoUnit.SECONDS);
        long retrieved = zoneTime.until(endExclusive, ChronoUnit.SECONDS);
        assertEquals(expected, retrieved);
    }

    @Test
    void isSupportedTemporalUnit() {
        for (ChronoUnit unit : ChronoUnit.values()) {
            assertEquals(offsetTime.isSupported(unit), zoneTime.isSupported(unit));
        }
    }

    @Test
    void isSupportedTemporalField() {
        for (ChronoField field : ChronoField.values()) {
            assertEquals(offsetTime.isSupported(field), zoneTime.isSupported(field));
        }
    }

    @Test
    void getLong() {
        Arrays.stream(ChronoField.values()).filter(offsetTime::isSupported)
                .forEach(field -> assertEquals(offsetTime.getLong(field), zoneTime.getLong(field)));
    }

    @Test
    void adjustInto() {
        ZoneTime temporal = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        assertEquals(offsetTime.adjustInto(temporal), zoneTime.adjustInto(temporal));
    }

    @Test
    void query() {
        assertEquals(zoneId, zoneTime.query(TemporalQueries.zoneId()));
        assertEquals(zoneId, zoneTime.query(TemporalQueries.zone()));
        assertEquals(offsetTime.query(TemporalQueries.localTime()), zoneTime.query(TemporalQueries.localTime()));
        assertEquals(offsetTime.query(TemporalQueries.offset()), zoneTime.query(TemporalQueries.offset()));
    }

    @Test
    void range() {
        Arrays.stream(ChronoField.values()).filter(offsetTime::isSupported)
                .forEach(field -> assertEquals(offsetTime.range(field), zoneTime.range(field)));
    }

    @Test
    void get() {
        Arrays.stream(ChronoField.values())
                .filter(offsetTime::isSupported)
                .filter(field -> field != ChronoField.NANO_OF_DAY && field != ChronoField.MICRO_OF_DAY) // Unsupported by offsettime.get()
                .forEach(field -> assertEquals(offsetTime.get(field), zoneTime.get(field)));
    }

    @Test
    void testEquals() {
        ZoneTime toCompare = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        assertFalse(zoneTime.equals(toCompare));
        toCompare = ZoneTime.of(localTime, zoneId, false);
        assertTrue(zoneTime.equals(toCompare));
    }

    @Test
    void testZONED_OFFSET_WITHOUT_SECONDS() {
        String timeString = "09:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, false);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, ZONED_OFFSET_WITHOUT_SECONDS.format(toFormat));
    }

    @Test
    void testZONED_OFFSET_WITH_SECONDS() {
        String timeString = "09:34:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, ZONED_OFFSET_WITH_SECONDS.format(toFormat));

        timeString = "09:34:00";
        toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, ZONED_OFFSET_WITH_SECONDS.format(toFormat));
    }

    @Test
    void testFormatWithoutSeconds() {
        String timeString = "09:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, false);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, toFormat.format());
    }

    @Test
    void testFormatWithSeconds() {
        String timeString = "09:34:00";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, toFormat.format());

        timeString = "09:34:34";
        toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertEquals(expected, toFormat.format());
    }

    private static OffsetTime getCorrectOffsetTime() {
        String correctOffsetTimeString = getCorrectOffsetTimeString(REFERENCED_TIME, REFERENCED_ZONE);
        return DateTimeFormatter.ISO_TIME.parse( correctOffsetTimeString, OffsetTime::from );
    }

    private static String getCorrectOffsetTimeString(String baseTime, String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        ZoneOffset offset = zoneId.getRules().getOffset(LocalDateTime.now());
        String diffOffset = offset.getId();
        return String.format("%s%s",baseTime, diffOffset);
    }
}