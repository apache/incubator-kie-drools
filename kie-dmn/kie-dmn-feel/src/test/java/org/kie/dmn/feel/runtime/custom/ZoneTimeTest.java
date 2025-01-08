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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getOffsetTime()).isEqualTo(offsetTime);
        assertThat(retrieved.getZoneId()).isEqualTo(zoneId);
    }


    @Test
    void getTimezone() {
        assertThat(zoneTime.getTimezone()).isEqualTo(REFERENCED_ZONE);
    }

    @Test
    void compareTo() {
        ZoneTime toCompare = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        OffsetTime comparison = toCompare.getOffsetTime();
        assertThat(zoneTime.compareTo(toCompare)).isEqualTo(offsetTime.compareTo(comparison));

    }

    @Test
    void withTemporalField() {
        ZoneTime expected = new ZoneTime(offsetTime.with(ChronoField.HOUR_OF_DAY, 3), zoneId, false);
        assertThat(zoneTime.with(ChronoField.HOUR_OF_DAY, 3)).isEqualTo(expected);
    }

    @Test
    void withTemporalAdjuster() {
        TemporalAdjuster adjuster = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId
                , false);
        ZoneTime expected = new ZoneTime(offsetTime.with(adjuster), zoneId, false);
        assertThat(zoneTime.with(adjuster)).isEqualTo(expected);
        adjuster = DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from );
        expected = new ZoneTime(offsetTime.with(adjuster), zoneId, false);
        assertThat(zoneTime.with(adjuster)).isEqualTo(expected);
    }


    @Test
    void plusLong() {
        ZoneTime expected = new ZoneTime(offsetTime.plus(3, ChronoUnit.HOURS), zoneId, false);
        assertThat(zoneTime.plus(3, ChronoUnit.HOURS)).isEqualTo(expected);
    }

    @Test
    void plusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        ZoneTime expected = new ZoneTime(offsetTime.plus(amount), zoneId, false);
        assertThat(zoneTime.plus(amount)).isEqualTo(expected);
    }

    @Test
    void minusLong() {
        ZoneTime expected = new ZoneTime(offsetTime.minus(3, ChronoUnit.HOURS), zoneId, false);
        assertThat(zoneTime.minus(3, ChronoUnit.HOURS)).isEqualTo(expected);
    }

    @Test
    void minusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        ZoneTime expected = new ZoneTime(offsetTime.minus(amount), zoneId, false);
        assertThat(zoneTime.minus(amount)).isEqualTo(expected);
    }

    @Test
    void until() {
        ZoneTime endExclusive = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId,
                                            false);
        long expected = offsetTime.until(endExclusive, ChronoUnit.SECONDS);
        long retrieved = zoneTime.until(endExclusive, ChronoUnit.SECONDS);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void isSupportedTemporalUnit() {
    	assertThat(ChronoUnit.values()).allMatch(unit -> zoneTime.isSupported(unit) == offsetTime.isSupported(unit));
    }

    @Test
    void isSupportedTemporalField() {
    	assertThat(ChronoField.values()).allMatch(field -> zoneTime.isSupported(field) == offsetTime.isSupported(field));
    }

    @Test
    void getLong() {
        assertThat(ChronoField.values()).filteredOn(offsetTime::isSupported)
                .allMatch(field -> offsetTime.getLong(field) == (zoneTime.getLong(field)));
    }

    @Test
    void adjustInto() {
        ZoneTime temporal = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        assertThat(zoneTime.adjustInto(temporal)).isEqualTo(offsetTime.adjustInto(temporal));
    }

    @Test
    void query() {
        assertThat(zoneTime.query(TemporalQueries.zoneId())).isEqualTo(zoneId);
        assertThat(zoneTime.query(TemporalQueries.zone())).isEqualTo(zoneId);
        assertThat(zoneTime.query(TemporalQueries.localTime())).isEqualTo(offsetTime.query(TemporalQueries.localTime()));
        assertThat(zoneTime.query(TemporalQueries.offset())).isEqualTo(offsetTime.query(TemporalQueries.offset()));
    }

    @Test
    void range() {
        assertThat(ChronoField.values()).filteredOn(offsetTime::isSupported)
                .allMatch(field -> offsetTime.range(field).equals(zoneTime.range(field)));
    }

    @Test
    void get() {
    	assertThat(ChronoField.values())
                .filteredOn(offsetTime::isSupported)
                .filteredOn(field -> field != ChronoField.NANO_OF_DAY && field != ChronoField.MICRO_OF_DAY) // Unsupported by offsettime.get()
                .allMatch(field -> offsetTime.get(field) == zoneTime.get(field));
    }

    @Test
    void testEquals() {
        ZoneTime toCompare = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse("09:34:31", LocalTime::from), zoneId, false);
        assertThat(toCompare).isNotEqualTo(zoneTime);
        toCompare = ZoneTime.of(localTime, zoneId, false);
        assertThat(toCompare).isEqualTo(zoneTime);
    }

    @Test
    void testZONED_OFFSET_WITHOUT_SECONDS() {
        String timeString = "09:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, false);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(ZONED_OFFSET_WITHOUT_SECONDS.format(toFormat)).isEqualTo(expected);
    }

    @Test
    void testZONED_OFFSET_WITH_SECONDS() {
        String timeString = "09:34:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(ZONED_OFFSET_WITH_SECONDS.format(toFormat)).isEqualTo(expected);

        timeString = "09:34:00";
        toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(ZONED_OFFSET_WITH_SECONDS.format(toFormat)).isEqualTo(expected);
    }

    @Test
    void testFormatWithoutSeconds() {
        String timeString = "09:34";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, false);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(toFormat.format()).isEqualTo(expected);
    }

    @Test
    void testFormatWithSeconds() {
        String timeString = "09:34:00";
        ZoneTime toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        String expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(toFormat.format()).isEqualTo(expected);

        timeString = "09:34:34";
        toFormat = ZoneTime.of(DateTimeFormatter.ISO_TIME.parse(timeString, LocalTime::from), zoneId, true);
        expected = String.format("%s@%s", timeString, REFERENCED_ZONE);
        assertThat(toFormat.format()).isEqualTo(expected);
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