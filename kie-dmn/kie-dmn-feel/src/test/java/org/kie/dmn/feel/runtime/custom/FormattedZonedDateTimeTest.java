/*
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormattedZonedDateTimeTest {

    private static final String REFERENCED_DATE = "2024-08-10";
    private static final String REFERENCED_TIME = "10:15:00";
    private static final String REFERENCED_ZONE = "Europe/Paris";
    private static LocalDate localDate;
    private static LocalTime localTime;
    private static ZoneId zoneId;
    private static ZonedDateTime zonedDateTime;
    private static FormattedZonedDateTime formattedZonedDateTime;

    @BeforeAll
    static void setUpClass() {
        localDate = DateTimeFormatter.ISO_DATE.parse(REFERENCED_DATE, LocalDate::from);
        localTime = DateTimeFormatter.ISO_TIME.parse(REFERENCED_TIME, LocalTime::from);
        zoneId = ZoneId.of(REFERENCED_ZONE);
        zonedDateTime = ZonedDateTime.of(localDate, localTime, zoneId);
        formattedZonedDateTime = FormattedZonedDateTime.of(localDate, localTime, zoneId);
    }

    @Test
    void ofLocalDateLocalTimeZoneId() {
        FormattedZonedDateTime retrieved = FormattedZonedDateTime.of(localDate, localTime, zoneId);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getZonedDateTime()).isEqualTo(zonedDateTime);
        assertThat(retrieved.getZone()).isEqualTo(zoneId);
    }

    @Test
    void ofIntegers() {
        FormattedZonedDateTime retrieved = FormattedZonedDateTime.of(2024, 8, 10, 10, 15, 0, 0, zoneId);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getZonedDateTime()).isEqualTo(zonedDateTime);
        assertThat(retrieved.getZone()).isEqualTo(zoneId);
    }

    @Test
    void from() {
        FormattedZonedDateTime retrieved = FormattedZonedDateTime.from(zonedDateTime);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getZonedDateTime()).isEqualTo(zonedDateTime);
    }

    @Test
    void getZone() {
        assertThat(formattedZonedDateTime.getZone()).isEqualTo(zoneId);
    }

    @Test
    void getOffset() {
        assertThat(formattedZonedDateTime.getOffset()).isEqualTo(zonedDateTime.getOffset());
    }

    @Test
    void toLocalDateTime() {
        assertThat(formattedZonedDateTime.toLocalDateTime()).isEqualTo(zonedDateTime.toLocalDateTime());
    }

    @Test
    void compareTo() {
        FormattedZonedDateTime toCompare = FormattedZonedDateTime.of(2024, 8, 10, 9, 30, 0, 0, zoneId);
        assertThat(formattedZonedDateTime.compareTo(toCompare)).isEqualTo(zonedDateTime.compareTo(toCompare.getZonedDateTime()));
    }

    @Test
    void withTemporalField() {
        FormattedZonedDateTime expected = FormattedZonedDateTime.from(zonedDateTime.with(ChronoField.HOUR_OF_DAY, 3));
        assertThat(formattedZonedDateTime.with(ChronoField.HOUR_OF_DAY, 3)).isEqualTo(expected);
    }

    @Test
    void plusLong() {
        FormattedZonedDateTime expected = FormattedZonedDateTime.from(zonedDateTime.plus(3, ChronoUnit.HOURS));
        assertThat(formattedZonedDateTime.plus(3, ChronoUnit.HOURS)).isEqualTo(expected);
    }

    @Test
    void plusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        FormattedZonedDateTime expected = FormattedZonedDateTime.from(zonedDateTime.plus(amount));
        assertThat(formattedZonedDateTime.plus(amount)).isEqualTo(expected);
    }

    @Test
    void minusLong() {
        FormattedZonedDateTime expected = FormattedZonedDateTime.from(zonedDateTime.minus(3, ChronoUnit.HOURS));
        assertThat(formattedZonedDateTime.minus(3, ChronoUnit.HOURS)).isEqualTo(expected);
    }

    @Test
    void minusTemporalAmount() {
        TemporalAmount amount = Duration.of(23, ChronoUnit.MINUTES);
        FormattedZonedDateTime expected = FormattedZonedDateTime.from(zonedDateTime.minus(amount));
        assertThat(formattedZonedDateTime.minus(amount)).isEqualTo(expected);
    }

    @Test
    void until() {
        FormattedZonedDateTime endExclusive = FormattedZonedDateTime.of(2024, 8, 10, 9, 30, 0, 0, zoneId);
        long expected = zonedDateTime.until(endExclusive.getZonedDateTime(), ChronoUnit.SECONDS);
        long retrieved = formattedZonedDateTime.until(endExclusive, ChronoUnit.SECONDS);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void isSupportedTemporalUnit() {
        assertThat(ChronoUnit.values()).allMatch(unit -> formattedZonedDateTime.isSupported(unit) == zonedDateTime.isSupported(unit));
    }

    @Test
    void isSupportedTemporalField() {
        assertThat(ChronoField.values()).allMatch(field -> formattedZonedDateTime.isSupported(field) == zonedDateTime.isSupported(field));
    }

    @Test
    void getLong() {
        assertThat(ChronoField.values()).filteredOn(zonedDateTime::isSupported)
                .allMatch(field -> zonedDateTime.getLong(field) == formattedZonedDateTime.getLong(field));
    }

    @Test
    void query() {
        assertThat(formattedZonedDateTime.query(TemporalQueries.zoneId())).isEqualTo(zoneId);
        assertThat(formattedZonedDateTime.query(TemporalQueries.zone())).isEqualTo(zoneId);
        assertThat(formattedZonedDateTime.query(TemporalQueries.localDate())).isEqualTo(zonedDateTime.query(TemporalQueries.localDate()));
        assertThat(formattedZonedDateTime.query(TemporalQueries.localTime())).isEqualTo(zonedDateTime.query(TemporalQueries.localTime()));
        assertThat(formattedZonedDateTime.query(TemporalQueries.offset())).isEqualTo(zonedDateTime.query(TemporalQueries.offset()));
    }

    @Test
    void testEquals() {
        FormattedZonedDateTime toCompare = FormattedZonedDateTime.of(2024, 8, 10, 9, 30, 0, 0, zoneId);
        assertThat(toCompare).isNotEqualTo(formattedZonedDateTime);
        toCompare = FormattedZonedDateTime.of(localDate, localTime, zoneId);
        assertThat(toCompare).isEqualTo(formattedZonedDateTime);
    }

    @Test
    void testToStringWithZeroSecondsZoneOffset() {
        // Test that toString() preserves seconds even when they are 0 with ZoneOffset
        ZonedDateTime zdt = ZonedDateTime.of(2024, 3, 15, 10, 10, 0, 0, ZoneId.of("+05:30"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = "2024-03-15T10:10:00+05:30";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithZeroSecondsZoneRegion() {
        // Test that toString() preserves seconds even when they are 0 with ZoneRegion
        ZonedDateTime zdt = ZonedDateTime.of(2024, 6, 20, 14, 30, 0, 0, ZoneId.of("America/New_York"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = "2024-06-20T14:30:00@America/New_York";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithZeroMinutesAndZeroSeconds() {
        // Test that toString() preserves both minutes and seconds when they are 0
        ZonedDateTime zdt = ZonedDateTime.of(2024, 8, 10, 10, 0, 0, 0, ZoneId.of("Europe/Paris"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = "2024-08-10T10:00:00@Europe/Paris";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithNonZeroSeconds() {
        // Test with non-zero seconds to ensure normal behavior
        ZonedDateTime zdt = ZonedDateTime.of(2024, 12, 25, 18, 45, 30, 0, ZoneId.of("Europe/London"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = "2024-12-25T18:45:30@Europe/London";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithDifferentTimezones() {
        // Test various timezone formats to ensure seconds are always preserved

        // UTC - uses ZoneRegion format with @
        ZonedDateTime utc = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneId.of("UTC"));
        FormattedZonedDateTime formattedUtc = FormattedZonedDateTime.from(utc);
        assertThat(formattedUtc.toString()).isEqualTo("2024-07-04T09:15:00@UTC");

        // ZoneOffset.UTC - uses ISO format with Z
        ZonedDateTime zdtWithZoneOffsetUTC = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneOffset.UTC);
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdtWithZoneOffsetUTC);
        assertThat(formatted.toString()).isEqualTo("2024-07-04T09:15:00Z");

        // Positive offset - uses ISO format
        ZonedDateTime plusOffset = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneId.of("+10:00"));
        FormattedZonedDateTime formattedPlus = FormattedZonedDateTime.from(plusOffset);
        assertThat(formattedPlus.toString()).isEqualTo("2024-07-04T09:15:00+10:00");

        // Negative offset - uses ISO format
        ZonedDateTime minusOffset = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneId.of("-05:00"));
        FormattedZonedDateTime formattedMinus = FormattedZonedDateTime.from(minusOffset);
        assertThat(formattedMinus.toString()).isEqualTo("2024-07-04T09:15:00-05:00");

        // Named timezone - uses @ format
        ZonedDateTime named = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneId.of("Asia/Tokyo"));
        FormattedZonedDateTime formattedNamed = FormattedZonedDateTime.from(named);
        assertThat(formattedNamed.toString()).isEqualTo("2024-07-04T09:15:00@Asia/Tokyo");
    }

    @Test
    void testToStringWithNanoseconds() {
        ZonedDateTime zdt = ZonedDateTime.of(2024, 8, 10, 10, 15, 30, 123456789, zoneId);
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = "2024-08-10T10:15:30.123456789@Europe/Paris";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

}