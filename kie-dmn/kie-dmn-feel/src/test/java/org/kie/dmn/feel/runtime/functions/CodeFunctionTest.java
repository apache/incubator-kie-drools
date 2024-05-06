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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.functions.extended.CodeFunction;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

class CodeFunctionTest {

    private CodeFunction codeFunction;

    @BeforeEach
    void setUp() {
        codeFunction = new CodeFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResult(codeFunction.invoke(null), "null");
    }

    @Test
    void invokeString() {
        FunctionTestUtil.assertResult(codeFunction.invoke("test"), "\"test\"");
    }

    @Test
    void invokeBigDecimal() {
        FunctionTestUtil.assertResult(codeFunction.invoke(BigDecimal.valueOf(10.7)), "10.7");
    }

    @Test
    void invokeLocalDate() {
        final LocalDate localDate = LocalDate.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(localDate), "date( \"" + localDate.toString() + "\" )");
    }

    @Test
    void invokeLocalTime() {
        final LocalTime localTime = LocalTime.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(localTime), "time( \"" + TimeFunction.FEEL_TIME.format(localTime) + "\" )");
    }

    @Test
    void invokeOffsetTime() {
        final OffsetTime offsetTime = OffsetTime.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(offsetTime), "time( \"" + TimeFunction.FEEL_TIME.format(offsetTime) + "\" )");
    }

    @Test
    void invokeLocalDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(localDateTime), "date and time( \"" + DateAndTimeFunction.FEEL_DATE_TIME.format(localDateTime) + "\" )");
    }

    @Test
    void invokeOffsetDateTime() {
        final OffsetDateTime offsetDateTime = OffsetDateTime.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(offsetDateTime), "date and time( \"" + DateAndTimeFunction.FEEL_DATE_TIME.format(offsetDateTime) + "\" )");
    }

    @Test
    void invokeZonedDateTime() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        FunctionTestUtil.assertResult(codeFunction.invoke(zonedDateTime), "date and time( \"" + DateAndTimeFunction.REGION_DATETIME_FORMATTER.format(zonedDateTime) + "\" )");
    }

    @Test
    void invokeDurationZero() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ZERO), "duration( \"PT0S\" )");
    }

    @Test
    void invokeDurationDays() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofDays(9)), "duration( \"P9D\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofDays(-9)), "duration( \"-P9D\" )");
    }

    @Test
    void invokeDurationHours() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofHours(9)), "duration( \"PT9H\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofHours(200)), "duration( \"P8DT8H\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofHours(-200)), "duration( \"-P8DT8H\" )");
    }

    @Test
    void invokeDurationMinutes() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMinutes(9)), "duration( \"PT9M\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMinutes(200)), "duration( \"PT3H20M\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMinutes(5000)), "duration( \"P3DT11H20M\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMinutes(-5000)), "duration( \"-P3DT11H20M\" )");
    }

    @Test
    void invokeDurationSeconds() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofSeconds(9)), "duration( \"PT9S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofSeconds(200)), "duration( \"PT3M20S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofSeconds(5000)), "duration( \"PT1H23M20S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofSeconds(90061)), "duration( \"P1DT1H1M1S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofSeconds(-90061)), "duration( \"-P1DT1H1M1S\" )");
    }

    @Test
    void invokeDurationNanosMillis() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofNanos(25)), "duration( \"PT0.000000025S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofNanos(10000)), "duration( \"PT0.00001S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofNanos(10025)), "duration( \"PT0.000010025S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMillis(1500)), "duration( \"PT1.5S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMillis(90061025)), "duration( \"P1DT1H1M1.025S\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Duration.ofMillis(-90061025)), "duration( \"-P1DT1H1M1.025S\" )");
    }

    @Test
    void invokePeriodZero() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ZERO), "duration( \"P0M\" )");
    }

    @Test
    void invokePeriodYears() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ofYears(24)), "duration( \"P24Y\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ofYears(-24)), "duration( \"-P24Y\" )");
    }

    @Test
    void invokePeriodMonths() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ofMonths(2)), "duration( \"P2M\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ofMonths(27)), "duration( \"P2Y3M\" )");
        FunctionTestUtil.assertResult(codeFunction.invoke(Period.ofMonths(-27)), "duration( \"-P2Y3M\" )");
    }

    @Test
    void invokeListEmpty() {
        FunctionTestUtil.assertResult(codeFunction.invoke(Collections.emptyList()), "[ ]");
    }

    @Test
    void invokeListNonEmpty() {
        final List<Object> values = new ArrayList<>();
        values.add(1);
        values.add(BigDecimal.valueOf(10.5));
        values.add("test");
        FunctionTestUtil.assertResult(codeFunction.invoke(values), "[ 1, 10.5, \"test\" ]");
    }

    @Test
    void invokeRangeOpenOpen() {
        FunctionTestUtil.assertResult(
                codeFunction.invoke(new RangeImpl(Range.RangeBoundary.OPEN, 12, 15, Range.RangeBoundary.OPEN)),
                "( 12 .. 15 )");
    }

    @Test
    void invokeRangeOpenClosed() {
        FunctionTestUtil.assertResult(
                codeFunction.invoke(new RangeImpl(Range.RangeBoundary.OPEN, 12, 15, Range.RangeBoundary.CLOSED)),
                "( 12 .. 15 ]");
    }

    @Test
    void invokeRangeClosedOpen() {
        FunctionTestUtil.assertResult(
                codeFunction.invoke(new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.OPEN)),
                "[ 12 .. 15 )");
    }

    @Test
    void invokeRangeClosedClosed() {
        FunctionTestUtil.assertResult(
                codeFunction.invoke(new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED)),
                "[ 12 .. 15 ]");
    }

    @Test
    void invokeContextEmpty() {
        FunctionTestUtil.assertResult(codeFunction.invoke(new HashMap<>()), "{ }");
    }

    @Test
    void invokeContextNonEmpty() {
        final Map<String, Object> childContextMap = new HashMap<>();
        childContextMap.put("childKey1", "childValue1");

        final Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key1", "value1");
        contextMap.put("key2", childContextMap);

        FunctionTestUtil.assertResult(codeFunction.invoke(contextMap), "{ key1 : \"value1\", key2 : { childKey1 : \"childValue1\" } }");
    }
}