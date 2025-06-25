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
package org.kie.dmn.feel.runtime.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.runtime.functions.FunctionTestUtil.assertResultError;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class DateAndTimeFunctionTest {

    private final DateAndTimeFunction dateTimeFunction = DateAndTimeFunction.INSTANCE;

    @Test
    void invokeFromString() {
        FEELFnResult<TemporalAccessor> retrievedResult = dateTimeFunction.invoke("2017-08-10T10:20:00@Europe/Paris");
        assertThat(retrievedResult).isNotNull();
        assertThat(retrievedResult.isRight()).isTrue();
        TemporalAccessor retrieved = retrievedResult.getOrElse(null);
        assertThat(retrieved).isNotNull().isInstanceOf(ZonedDateTime.class);
        ZonedDateTime retrievedZonedDateTime = (ZonedDateTime) retrieved;
        assertThat(retrievedZonedDateTime.getYear()).isEqualTo(2017);
        assertThat(retrievedZonedDateTime.getMonthValue()).isEqualTo(8);
        assertThat(retrievedZonedDateTime.getDayOfMonth()).isEqualTo(10);
        assertThat(retrievedZonedDateTime.getHour()).isEqualTo(10);
        assertThat(retrievedZonedDateTime.getMinute()).isEqualTo(20);
        assertThat(retrievedZonedDateTime.getSecond()).isZero();
        assertThat(retrievedZonedDateTime.getZone()).isEqualTo(ZoneId.of("Europe/Paris"));
    }

    @Test
    void invokeParamStringNull() {
        assertResultError(dateTimeFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringNotDateOrTime() {
        assertResultError(dateTimeFunction.invoke("test"), InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke("2017-09-test"), InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke("2017-09-T89"), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringDateTime() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07T10:20:30"), LocalDateTime.of(2017, 9, 7, 10
                , 20, 30));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("99999-12-31T11:22:33"), LocalDateTime.of(99999, 12, 31
                , 11, 22, 33));
    }

    @Test
    void invokeParamStringDateTimeZoned() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30@Europe/Paris"),
                ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 0, ZoneId.of("Europe/Paris")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30.987@Europe/Paris"),
                ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 987_000_000, ZoneId.of("Europe/Paris"
                )));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30.123456789@Europe/Paris"),
                ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 123_456_789, ZoneId.of("Europe/Paris"
                )));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("999999999-12-31T23:59:59.999999999@Europe/Paris"),
                ZonedDateTime.of(999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneId.of("Europe/Paris")));
    }

    @Test
    void invokeParamStringDateOffset() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-12-31T23:59:59.999999999+02:00"),
                ZonedDateTime.of(2017, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of("+02:00")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999+02:00"),
                ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of(
                        "+02:00")));
    }

    @Test
    void invokeParamStringDate() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07"), LocalDateTime.of(2017, 9, 7, 0, 0, 0));
    }

    @Test
    void invokeParamTemporalNulls() {
        assertResultError(dateTimeFunction.invoke((Temporal) null, null),
                InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke(null, LocalTime.of(10, 6, 20)),
                InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke(LocalDate.of(2017, 6, 12), null),
                InvalidParametersEvent.class);
    }

    @Test
    void invokeParamTemporalWrongTemporal() {
        // reminder: 1st parameter accordingly to FEEL Spec Table 58 "date is a date or date time [...] creates a
        // date time from the given date (ignoring any time component)" [that means ignoring any TZ from `date`
        // parameter, too]
        assertResultError(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        LocalDateTime.of(2017, 6, 12, 0, 0)), InvalidParametersEvent.class);
        assertResultError(
                dateTimeFunction.invoke(
                        LocalDateTime.of(2017, 6, 12, 0, 0),
                        LocalDateTime.of(2017, 6, 12, 0, 0)), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamTemporalLocalTime() {
        FunctionTestUtil.assertResult(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        LocalTime.of(10, 6, 20)),
                LocalDateTime.of(2017, 6, 12, 10, 6, 20));
    }

    @Test
    void invokeParamTemporalOffsetTime() {
        FunctionTestUtil.assertResult(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        OffsetTime.of(10, 6, 20, 0, ZoneOffset.UTC)),
                ZonedDateTime.of(2017, 6, 12, 10, 6, 20, 0, ZoneOffset.UTC));
    }

    @Test
    void invokeParamStringDateTimeZone() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke(LocalDate.of(2024, 12, 24),
                        LocalTime.of(23, 59, 0), "America/Costa_Rica"),
                ZonedDateTime.of(2024, 12, 24, 23, 59, 0, 0, ZoneId.of("America/Costa_Rica")));
        FEELFnResult<TemporalAccessor> expectedResult = dateTimeFunction.invoke(LocalDate.of(2024, 12, 24), LocalTime.of(23, 59, 0), "America/Costa_Rica");
        assertThat(expectedResult.isRight()).isTrue();
        assertThat(expectedResult.getOrElse(null)).isNotNull();
        FEELFnResult<TemporalAccessor> retrievedResult = dateTimeFunction.invoke("2024-12-24T23:59:00@America/Costa_Rica");
        assertThat(retrievedResult.isRight()).isTrue();
        assertThat(retrievedResult.getOrElse(null)).isNotNull();
        assertThat(expectedResult.getOrElse(null)).isEqualTo(retrievedResult.getOrElse(null));
    }

    @Test
    void testParamStringDateTimeZone() {
        FEELFnResult<TemporalAccessor> result = dateTimeFunction.invoke(LocalDate.of(2024, 12, 24), LocalTime.of(23, 59, 0), "Z");
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isNotNull();
        ZonedDateTime actualDateTime = (ZonedDateTime) result.getOrElse(null);
        ZonedDateTime expectedDateTime = ZonedDateTime.of(2024, 12, 24, 23, 59, 0, 0, ZoneOffset.UTC);
        assertThat(expectedDateTime).isEqualTo(actualDateTime);
        FEELFnResult<TemporalAccessor> retrievedResult = dateTimeFunction.invoke("2024-12-24T23:59:00Z");
        assertThat(retrievedResult.isRight()).isTrue();
        assertThat(retrievedResult.getOrElse(null)).isNotNull();
        assertThat(actualDateTime).isEqualTo(retrievedResult.getOrElse(null));
    }

    @Test
    void testInvalidDateTimeAndTimezone() {
        assertResultError(dateTimeFunction.invoke(null, LocalTime.of(23, 59, 0), "Z"), InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke(LocalDate.of(2024, 12, 24), null, "Z"), InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke(LocalDate.of(2024, 12, 24), LocalTime.of(23, 59, 0), null), InvalidParametersEvent.class);
        assertResultError(dateTimeFunction.invoke(LocalDate.of(2024, 12, 24), LocalTime.of(23, 59, 0), "Foo/Bar"), InvalidParametersEvent.class);
    }

    @Test
    void testValidateDate() {
        LocalDate date = LocalDate.of(2023, 6, 23);
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidDate(date);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(date);
    }

    @Test
    void testValidateTime() {
        LocalTime time = LocalTime.of(23, 59, 0, 0);
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidTime(time);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(time);
    }

    @Test
    void testValidateTimeZone() {
        String timeZone = "Europe/Paris";
        Optional<ZoneId> result = DateAndTimeFunction.getValidTimeZone(timeZone);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(ZoneId.of("Europe/Paris"));
    }

    @Test
    void testValidateNullDate() {
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidDate(null);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateNullTime() {
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidTime(null);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidateNullTimeZone() {
        Optional<ZoneId> result = DateAndTimeFunction.getValidTimeZone(null);
        assertThat(result).isEmpty();
    }

    @Test
    void testInvalidDate() {
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidDate(DayOfWeek.MONDAY);
        assertThat(result).isEmpty();
    }

    @Test
    void testInvalidTime() {
        Optional<TemporalAccessor> result = DateAndTimeFunction.getValidTime(DayOfWeek.MONDAY);
        assertThat(result).isEmpty();
    }

    @Test
    void testInvalidTimeZone() {
        Optional<ZoneId> result = DateAndTimeFunction.getValidTimeZone("Foo/Bar");
        assertThat(result).isEmpty();
    }
}
