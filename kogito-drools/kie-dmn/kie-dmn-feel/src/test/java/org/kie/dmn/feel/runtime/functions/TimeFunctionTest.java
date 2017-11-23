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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class TimeFunctionTest {

    private TimeFunction timeFunction;

    @Before
    public void setUp() {
        timeFunction = new TimeFunction();
    }

    @Test
    public void invokeStringParamNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke((String) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStringParamNotDateOrTime() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("test"), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStringParamDate() {
        FunctionTestUtil.assertResult(timeFunction.invoke("2017-10-09"), LocalTime.of(0,0,0));
        FunctionTestUtil.assertResult(timeFunction.invoke("2017-10-09T10:15:06"), LocalTime.of(10,15,6));
    }

    @Test
    public void invokeStringParamTimeWrongFormat() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("10-15:06"), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStringParamNoOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06"), LocalTime.of(10,15,6));
    }

    @Test
    public void invokeStringParamWithOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06+01:00"), OffsetTime.of(10,15,6, 0, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06-01:00"), OffsetTime.of(10,15,6, 0, ZoneOffset.ofHours(-1)));
    }

    @Test
    public void parseWithZone() {
        TemporalAccessor parsedResult = timeFunction.invoke("00:01:00@Etc/UTC").getOrElse(null);
        assertEquals(LocalTime.of(0, 1, 0), parsedResult.query(TemporalQueries.localTime()));
        assertEquals(ZoneId.of("Etc/UTC"), parsedResult.query(TemporalQueries.zone()));
    }

    @Test
    public void parseWithZoneIANA() {
        TemporalAccessor parsedResult = timeFunction.invoke("00:01:00@Europe/Paris").getOrElse(null);
        assertEquals(LocalTime.of(0, 1, 0), parsedResult.query(TemporalQueries.localTime()));
        assertEquals(ZoneId.of("Europe/Paris"), parsedResult.query(TemporalQueries.zone()));
    }

    @Test
    public void invokeWrongIANAformat() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("13:20:00+02:00@Europe/Paris"), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTemporalAccessorParamNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke((TemporalAccessor) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTemporalAccessorParamUnsupportedAccessor() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(DayOfWeek.MONDAY), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTemporalAccessorParamDate() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(LocalDate.of(2017, 6, 12)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTemporalAccessorParamTime() {
        FunctionTestUtil.assertResult(timeFunction.invoke(LocalTime.of(11, 43)), LocalTime.of(11, 43, 0));
    }

    @Test
    public void invokeTemporalAccessorParamDateTime() {
        FunctionTestUtil.assertResult(timeFunction.invoke(LocalDateTime.of(2017, 6, 12, 11, 43)), LocalTime.of(11, 43, 0));
    }

    @Test
    public void invokeTimeUnitsParamsNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, null, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, 1, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, null, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, null, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTimeUnitsParamsUnsupportedNumber() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(Double.POSITIVE_INFINITY, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(Double.NEGATIVE_INFINITY, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, Double.POSITIVE_INFINITY, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, Double.NEGATIVE_INFINITY, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, Double.POSITIVE_INFINITY, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, Double.NEGATIVE_INFINITY, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTimeUnitsParamsOutOfBounds() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(40, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 900, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, 900, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTimeUnitsParamsNoOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, null), LocalTime.of(10, 43, 15));
    }

    @Test
    public void invokeTimeUnitsParamsNoOffsetWithNanoseconds() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), null), LocalTime.of(10, 43, 15, 154000000));
    }

    @Test
    public void invokeTimeUnitsParamsWithOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, Duration.ofHours(1)), OffsetTime.of(10, 43, 15, 0, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, Duration.ofHours(-1)), OffsetTime.of(10, 43, 15, 0, ZoneOffset.ofHours(-1)));
    }

    @Test
    public void invokeTimeUnitsParamsWithOffsetWithNanoseconds() {
        FunctionTestUtil.assertResult(
                timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), Duration.ofHours(1)),
                OffsetTime.of(10, 43, 15, 154000000, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(
                timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), Duration.ofHours(-1)),
                OffsetTime.of(10, 43, 15, 154000000, ZoneOffset.ofHours(-1)));
    }
}