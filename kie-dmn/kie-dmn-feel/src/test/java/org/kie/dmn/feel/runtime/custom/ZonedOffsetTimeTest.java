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

class ZonedOffsetTimeTest {

    private static final String REFERENCED_TIME = "10:15:00";
    private static final String REFERENCED_ZONE = "Australia/Melbourne";
    private static LocalTime localTime ;
    private static ZoneId zoneId;
    private static OffsetTime offsetTime;
    private static ZonedOffsetTime  zonedOffsetTime;

    @BeforeAll
    static void setUpClass() {
        localTime = DateTimeFormatter.ISO_TIME.parse(REFERENCED_TIME, LocalTime::from );
        zoneId = ZoneId.of(REFERENCED_ZONE);
        offsetTime = getCorrectOffsetTime();
        zonedOffsetTime = ZonedOffsetTime.of(localTime, zoneId);
    }

    @Test
    void of() {
        ZonedOffsetTime retrieved = ZonedOffsetTime.of(localTime, zoneId);
        assertNotNull(retrieved);
        assertEquals(offsetTime, retrieved.getOffset());
        assertEquals(zoneId, retrieved.getZoneId());
    }


    @Test
    void getTimezone() {
        assertEquals(REFERENCED_ZONE, zonedOffsetTime.getTimezone());
    }

    @Test
    void compareTo() {
        ZonedOffsetTime toCompare = ZonedOffsetTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from ), zoneId);
        OffsetTime comparison = toCompare.getOffset();
        assertEquals(offsetTime.compareTo(comparison), zonedOffsetTime.compareTo(toCompare));

    }

    @Test
    void withTemporalField() {
        assertEquals(offsetTime.with(ChronoField.HOUR_OF_DAY, 3), zonedOffsetTime.with(ChronoField.HOUR_OF_DAY, 3));
    }

    @Test
    void withTemporalAdjuster() {
        TemporalAdjuster adjuster = ZonedOffsetTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from ), zoneId);
        assertEquals(offsetTime.with(adjuster), zonedOffsetTime.with(adjuster));
        adjuster = DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from );
        assertEquals(offsetTime.with(adjuster), zonedOffsetTime.with(adjuster));
    }


    @Test
    void plusLong() {
        assertEquals(offsetTime.plus(3, ChronoUnit.HOURS), zonedOffsetTime.plus(3, ChronoUnit.HOURS));
    }

    @Test
    void plusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        assertEquals(offsetTime.plus(amount), zonedOffsetTime.plus(amount));
    }

    @Test
    void minusLong() {
        assertEquals(offsetTime.minus(3, ChronoUnit.HOURS), zonedOffsetTime.minus(3, ChronoUnit.HOURS));
    }

    @Test
    void minusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        assertEquals(offsetTime.minus(amount), zonedOffsetTime.minus(amount));
    }

    @Test
    void until() {
        ZonedOffsetTime endExclusive = ZonedOffsetTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from ), zoneId);
        assertEquals(offsetTime.until(endExclusive, ChronoUnit.SECONDS), zonedOffsetTime.until(endExclusive, ChronoUnit.SECONDS));
    }

    @Test
    void isSupportedTemporalUnit() {
        for (ChronoUnit unit : ChronoUnit.values()) {
            assertEquals(offsetTime.isSupported(unit), zonedOffsetTime.isSupported(unit));
        }
    }

    @Test
    void isSupportedTemporalField() {
        for (ChronoField field : ChronoField.values()) {
            assertEquals(offsetTime.isSupported(field), zonedOffsetTime.isSupported(field));
        }
    }

    @Test
    void getLong() {
        Arrays.stream(ChronoField.values()).filter(offsetTime::isSupported)
                .forEach(field -> assertEquals(offsetTime.getLong(field), zonedOffsetTime.getLong(field)));
    }

    @Test
    void adjustInto() {
        ZonedOffsetTime temporal = ZonedOffsetTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from ), zoneId);
        assertEquals(offsetTime.adjustInto(temporal), zonedOffsetTime.adjustInto(temporal));
    }

    @Test
    void query() {
        assertEquals(zoneId, zonedOffsetTime.query(TemporalQueries.zoneId() ));
        assertEquals(zoneId, zonedOffsetTime.query(TemporalQueries.zone() ));
        assertEquals(offsetTime.query(TemporalQueries.localTime()), zonedOffsetTime.query(TemporalQueries.localTime()));
        assertEquals(offsetTime.query(TemporalQueries.offset()), zonedOffsetTime.query(TemporalQueries.offset()));
    }

    @Test
    void range() {
        Arrays.stream(ChronoField.values()).filter(offsetTime::isSupported)
                .forEach(field -> assertEquals(offsetTime.range(field), zonedOffsetTime.range(field)));
    }

    @Test
    void get() {
        Arrays.stream(ChronoField.values())
                .filter(offsetTime::isSupported)
                .filter(field -> field != ChronoField.NANO_OF_DAY && field != ChronoField.MICRO_OF_DAY) // Unsupported by offsettime.get()
                .forEach(field -> assertEquals(offsetTime.get(field), zonedOffsetTime.get(field)));
    }

    @Test
    void testEquals() {
        ZonedOffsetTime toCompare = ZonedOffsetTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from ), zoneId);
        assertFalse(zonedOffsetTime.equals(toCompare));
        toCompare = ZonedOffsetTime.of(localTime, zoneId);
        assertTrue(zonedOffsetTime.equals(toCompare));
    }

    @Test
    void testToString() {
        assertEquals(offsetTime.toString(), zonedOffsetTime.toString());
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