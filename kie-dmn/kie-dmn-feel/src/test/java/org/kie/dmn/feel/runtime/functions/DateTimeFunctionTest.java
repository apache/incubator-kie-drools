/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DateTimeFunctionTest {

    private DateAndTimeFunction dateTimeFunction;

    @Before
    public void setUp() {
        dateTimeFunction = new DateAndTimeFunction();
    }

    @Test
    public void invokeParamStringNull() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamStringNotDateOrTime() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("2017-09-test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke("2017-09-T89"), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamStringDateTime() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07T10:20:30"), LocalDateTime.of(2017, 9, 7, 10, 20, 30));
    }

    @Test
    public void invokeParamStringDate() {
        FunctionTestUtil.assertResult(dateTimeFunction.invoke("2017-09-07"), LocalDateTime.of(2017, 9, 7, 0, 0, 0));
    }

    @Test
    public void invokeParamTemporalNulls() {
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke((Temporal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(null, LocalTime.of(10, 6, 20)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateTimeFunction.invoke(LocalDate.of(2017, 6, 12), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamTemporalWrongTemporal() {
        FunctionTestUtil.assertResultError(
                dateTimeFunction.invoke(
                        LocalDateTime.of(2017, 6, 12, 0, 0),
                        LocalTime.of(10, 6, 20)), InvalidParametersEvent.class);
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
    public void invokeParamTemporalLocalTime() {
        FunctionTestUtil.assertResult(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        LocalTime.of(10, 6, 20)),
                LocalDateTime.of(2017, 6, 12, 10, 6, 20));
    }

    @Test
    public void invokeParamTemporalOffsetTime() {
        FunctionTestUtil.assertResult(
                dateTimeFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        OffsetTime.of(10, 6, 20, 0, ZoneOffset.UTC)),
                ZonedDateTime.of(2017, 6, 12, 10, 6, 20, 0, ZoneOffset.UTC));
    }
}