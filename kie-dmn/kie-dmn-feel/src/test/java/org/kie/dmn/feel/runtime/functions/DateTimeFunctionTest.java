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
package org.kie.dmn.feel.runtime.functions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class DateTimeFunctionTest {

    private DateAndTimeFunction dateTimeFunction;

    @BeforeEach
    void setUp() {
        dateTimeFunction = new DateAndTimeFunction();
    }

    @Test
    void invokeParamStringNull() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringNotDateOrTime() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("2017-09-test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("2017-09-T89"), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringDateTime() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07T10:20:30"), LocalDateTime.of(2017, 9, 7, 10, 20, 30));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("99999-12-31T11:22:33"), LocalDateTime.of(99999, 12, 31, 11, 22, 33));
    }

    @Test
    void invokeParamStringDateTimeZoned() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30@Europe/Paris"), ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 0, ZoneId.of("Europe/Paris")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30.987@Europe/Paris"), ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 987_000_000, ZoneId.of("Europe/Paris")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2011-12-31T10:15:30.123456789@Europe/Paris"), ZonedDateTime.of(2011, 12, 31, 10, 15, 30, 123_456_789, ZoneId.of("Europe/Paris")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("999999999-12-31T23:59:59.999999999@Europe/Paris"), ZonedDateTime.of(999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneId.of("Europe/Paris")));
    }

    @Test
    void invokeParamStringDateOffset() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-12-31T23:59:59.999999999+02:00"), ZonedDateTime.of(2017, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of("+02:00")));
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999+02:00"), ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of("+02:00")));
    }

    @Test
    void invokeParamStringDate() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07"), LocalDateTime.of(2017, 9, 7, 0, 0, 0));
    }

    @Test
    void invokeParamTemporalNulls() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke((Temporal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(null, LocalTime.of(10, 6, 20)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(LocalDate.of(2017, 6, 12), null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamTemporalWrongTemporal() {
        // reminder: 1st parameter accordingly to FEEL Spec Table 58 "date is a date or date time [...] creates a date time from the given date (ignoring any time component)" [that means ignoring any TZ from `date` parameter, too]
        FunctionTestUtil.assertResultError(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        LocalDateTime.of(2017, 6, 12, 0, 0)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(
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
}