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

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormattedZonedDateTimeTest {

    @Test
    void testToStringWithZeroSecondsZoneOffset() {
        // Test that toString() preserves seconds even when they are 0 with ZoneOffset
        String dateTimeString = "2024-03-15T10:10:00";
        ZonedDateTime zdt = ZonedDateTime.of(2024, 3, 15, 10, 10, 0, 0, ZoneId.of("+05:30"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = dateTimeString + "+05:30";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithZeroSecondsZoneRegion() {
        // Test that toString() preserves seconds even when they are 0 with ZoneRegion
        String dateTimeString = "2024-06-20T14:30:00";
        ZonedDateTime zdt = ZonedDateTime.of(2024, 6, 20, 14, 30, 0, 0, ZoneId.of("America/New_York"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = dateTimeString + "@America/New_York";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithNonZeroSeconds() {
        // Test with non-zero seconds to ensure normal behavior
        String dateTimeString = "2024-12-25T18:45:30";
        ZonedDateTime zdt = ZonedDateTime.of(2024, 12, 25, 18, 45, 30, 0, ZoneId.of("Europe/London"));
        FormattedZonedDateTime formatted = FormattedZonedDateTime.from(zdt);
        String expected = dateTimeString + "@Europe/London";
        assertThat(formatted.toString()).isEqualTo(expected);
    }

    @Test
    void testToStringWithDifferentTimezones() {
        // Test various timezone formats to ensure seconds are always preserved

        // UTC - uses ZoneRegion format with @
        ZonedDateTime utc = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, ZoneId.of("UTC"));
        FormattedZonedDateTime formattedUtc = FormattedZonedDateTime.from(utc);
        assertThat(formattedUtc.toString()).isEqualTo("2024-07-04T09:15:00@UTC");

        ZonedDateTime zdtWithZoneOffsetUTC = ZonedDateTime.of(2024, 7, 4, 9, 15, 0, 0, java.time.ZoneOffset.UTC);
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
}