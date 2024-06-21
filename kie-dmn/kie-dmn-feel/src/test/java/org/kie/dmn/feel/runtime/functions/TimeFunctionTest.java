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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeFunctionTest {

    private TimeFunction timeFunction;

    @BeforeEach
    void setUp() {
        timeFunction = new TimeFunction();
    }

    @Test
    void invokeStringParamNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke((String) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeStringParamNotDateOrTime() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("test"), InvalidParametersEvent.class);
    }

    @Test
    void invokeStringParamTimeWrongFormat() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("10-15:06"), InvalidParametersEvent.class);
    }

    @Test
    void invokeStringParamNoOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06"), LocalTime.of(10,15,6));
    }

    @Test
    void invokeStringParamWithOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06+01:00"), OffsetTime.of(10,15,6, 0, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(timeFunction.invoke("10:15:06-01:00"), OffsetTime.of(10,15,6, 0, ZoneOffset.ofHours(-1)));
    }

    @Test
    void parseWithZone() {
        final TemporalAccessor parsedResult = timeFunction.invoke("00:01:00@Etc/UTC").getOrElse(null);
        assertThat(parsedResult.query(TemporalQueries.localTime())).isEqualTo(LocalTime.of(0, 1, 0));
        assertThat(parsedResult.query(TemporalQueries.zone())).isEqualTo(ZoneId.of("Etc/UTC"));
    }

    @Test
    void parseWithZoneIANA() {
        final TemporalAccessor parsedResult = timeFunction.invoke("00:01:00@Europe/Paris").getOrElse(null);
        assertThat(parsedResult.query(TemporalQueries.localTime())).isEqualTo(LocalTime.of(0, 1, 0));
        assertThat(parsedResult.query(TemporalQueries.zone())).isEqualTo(ZoneId.of("Europe/Paris"));
    }

    @Test
    void invokeWrongIANAformat() {
        FunctionTestUtil.assertResultError(timeFunction.invoke("13:20:00+02:00@Europe/Paris"), InvalidParametersEvent.class);
    }

    @Test
    void invokeTemporalAccessorParamNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke((TemporalAccessor) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeTemporalAccessorParamUnsupportedAccessor() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(DayOfWeek.MONDAY), InvalidParametersEvent.class);
    }

    @Test
    void invokeTemporalAccessorParamDate() {
        FunctionTestUtil.assertResult(timeFunction.invoke(LocalDate.of(2017, 6, 12)), OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC));
    }

    @Test
    void invokeTemporalAccessorParamTime() {
        FunctionTestUtil.assertResult(timeFunction.invoke(LocalTime.of(11, 43)), LocalTime.of(11, 43, 0));
    }

    @Test
    void invokeTemporalAccessorParamDateTime() {
        FunctionTestUtil.assertResult(timeFunction.invoke(LocalDateTime.of(2017, 6, 12, 11, 43)), LocalTime.of(11, 43, 0));
    }

    @Test
    void invokeTimeUnitsParamsNull() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, null, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, 1, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(null, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, null, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, null, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeTimeUnitsParamsUnsupportedNumber() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(Double.POSITIVE_INFINITY, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(Double.NEGATIVE_INFINITY, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, Double.POSITIVE_INFINITY, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, Double.NEGATIVE_INFINITY, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, Double.POSITIVE_INFINITY, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, Double.NEGATIVE_INFINITY, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeTimeUnitsParamsOutOfBounds() {
        FunctionTestUtil.assertResultError(timeFunction.invoke(40, 1, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 900, 1, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(timeFunction.invoke(1, 1, 900, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeTimeUnitsParamsNoOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, null), LocalTime.of(10, 43, 15));
    }

    @Test
    void invokeTimeUnitsParamsNoOffsetWithNanoseconds() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), null), LocalTime.of(10, 43, 15, 154000000));
    }

    @Test
    void invokeTimeUnitsParamsWithOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, Duration.ofHours(1)), OffsetTime.of(10, 43, 15, 0, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15, Duration.ofHours(-1)), OffsetTime.of(10, 43, 15, 0, ZoneOffset.ofHours(-1)));
    }

    @Test
    void invokeTimeUnitsParamsWithNoOffset() {
        FunctionTestUtil.assertResult(timeFunction.invoke(10, 43, 15), LocalTime.of(10, 43, 15));
    }

    @Test
    void invokeTimeUnitsParamsWithOffsetWithNanoseconds() {
        FunctionTestUtil.assertResult(
                timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), Duration.ofHours(1)),
                OffsetTime.of(10, 43, 15, 154000000, ZoneOffset.ofHours(1)));
        FunctionTestUtil.assertResult(
                timeFunction.invoke(10, 43, BigDecimal.valueOf(15.154), Duration.ofHours(-1)),
                OffsetTime.of(10, 43, 15, 154000000, ZoneOffset.ofHours(-1)));
    }

    @Test
    void timeStringWithSeconds() {
        assertTrue(TimeFunction.timeStringWithSeconds("10:10:00@Australia/Melbourne"));
        assertTrue(TimeFunction.timeStringWithSeconds("10:10:00+10:00"));
        assertTrue(TimeFunction.timeStringWithSeconds("10:10:00:123"));

        assertFalse(TimeFunction.timeStringWithSeconds("10:10@Australia/Melbourne"));
        assertFalse(TimeFunction.timeStringWithSeconds("10:10+10:00"));
    }
}