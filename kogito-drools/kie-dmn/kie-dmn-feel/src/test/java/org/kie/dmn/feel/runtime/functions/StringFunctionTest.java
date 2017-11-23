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

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class StringFunctionTest {

    private StringFunction stringFunction;

    @Before
    public void setUp() throws Exception {
        stringFunction = new StringFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResult(stringFunction.invoke(null), null);
    }

    @Test
    public void invokeMaskNull() {
        FunctionTestUtil.assertResultError(stringFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stringFunction.invoke((String) null, new Object[]{}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeString() {
        FunctionTestUtil.assertResult(stringFunction.invoke("test"), "test");
    }

    @Test
    public void invokeBigDecimal() {
        FunctionTestUtil.assertResult(stringFunction.invoke(BigDecimal.valueOf(10.7)), "10.7");
    }

    @Test
    public void invokeLocalDate() {
        final LocalDate localDate = LocalDate.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(localDate), localDate.toString());
    }

    @Test
    public void invokeLocalTime() {
        final LocalTime localTime = LocalTime.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(localTime), TimeFunction.FEEL_TIME.format(localTime));
    }

    @Test
    public void invokeOffsetTime() {
        final OffsetTime offsetTime = OffsetTime.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(offsetTime), TimeFunction.FEEL_TIME.format(offsetTime));
    }

    @Test
    public void invokeLocalDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(localDateTime), DateAndTimeFunction.FEEL_DATE_TIME.format(localDateTime));
    }

    @Test
    public void invokeOffsetDateTime() {
        final OffsetDateTime offsetDateTime = OffsetDateTime.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(offsetDateTime), DateAndTimeFunction.FEEL_DATE_TIME.format(offsetDateTime));
    }

    @Test
    public void invokeZonedDateTime() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        FunctionTestUtil.assertResult(stringFunction.invoke(zonedDateTime), DateAndTimeFunction.REGION_DATETIME_FORMATTER.format(zonedDateTime));
    }

    @Test
    public void invokeDurationZero() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ZERO), "PT0S");
    }

    @Test
    public void invokeDurationDays() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofDays(9)), "P9D");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofDays(-9)), "-P9D");
    }

    @Test
    public void invokeDurationHours() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofHours(9)), "PT9H");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofHours(200)), "P8DT8H");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofHours(-200)), "-P8DT8H");
    }

    @Test
    public void invokeDurationMinutes() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMinutes(9)), "PT9M");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMinutes(200)), "PT3H20M");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMinutes(5000)), "P3DT11H20M");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMinutes(-5000)), "-P3DT11H20M");
    }

    @Test
    public void invokeDurationSeconds() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofSeconds(9)), "PT9S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofSeconds(200)), "PT3M20S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofSeconds(5000)), "PT1H23M20S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofSeconds(90061)), "P1DT1H1M1S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofSeconds(-90061)), "-P1DT1H1M1S");
    }

    @Test
    public void invokeDurationNanosMillis() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofNanos(25)), "PT0.000000025S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofNanos(10000)), "PT0.00001S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofNanos(10025)), "PT0.000010025S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMillis(1500)), "PT1.5S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMillis(90061025)), "P1DT1H1M1.025S");
        FunctionTestUtil.assertResult(stringFunction.invoke(Duration.ofMillis(-90061025)), "-P1DT1H1M1.025S");
    }

    @Test
    public void invokePeriodZero() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ZERO), "P0M");
    }

    @Test
    public void invokePeriodYears() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ofYears(24)), "P24Y");
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ofYears(-24)), "-P24Y");
    }

    @Test
    public void invokePeriodMonths() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ofMonths(2)), "P2M");
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ofMonths(27)), "P2Y3M");
        FunctionTestUtil.assertResult(stringFunction.invoke(Period.ofMonths(-27)), "-P2Y3M");
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResult(stringFunction.invoke(Collections.emptyList()), "[ ]");
    }

    @Test
    public void invokeListNonEmpty() {
        final List<Object> values = new ArrayList<>();
        values.add(1);
        values.add(BigDecimal.valueOf(10.5));
        values.add("test");
        FunctionTestUtil.assertResult(stringFunction.invoke(values), "[ 1, 10.5, test ]");
    }

    @Test
    public void invokeRangeOpenOpen() {
        FunctionTestUtil.assertResult(
                stringFunction.invoke(new RangeImpl(Range.RangeBoundary.OPEN, 12, 15, Range.RangeBoundary.OPEN)),
                "( 12 .. 15 )");
    }

    @Test
    public void invokeRangeOpenClosed() {
        FunctionTestUtil.assertResult(
                stringFunction.invoke(new RangeImpl(Range.RangeBoundary.OPEN, 12, 15, Range.RangeBoundary.CLOSED)),
                "( 12 .. 15 ]");
    }

    @Test
    public void invokeRangeClosedOpen() {
        FunctionTestUtil.assertResult(
                stringFunction.invoke(new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.OPEN)),
                "[ 12 .. 15 )");
    }

    @Test
    public void invokeRangeClosedClosed() {
        FunctionTestUtil.assertResult(
                stringFunction.invoke(new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED)),
                "[ 12 .. 15 ]");
    }

    @Test
    public void invokeContextEmpty() {
        FunctionTestUtil.assertResult(stringFunction.invoke(new HashMap<>()), "{ }");
    }

    @Test
    public void invokeContextNonEmpty() {
        final Map<String, Object> childContextMap = new HashMap<>();
        childContextMap.put("childKey1", "childValue1");

        final Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key1", "value1");
        contextMap.put("key2", childContextMap);

        FunctionTestUtil.assertResult(stringFunction.invoke(contextMap), "{ key1 : value1, key2 : { childKey1 : childValue1 } }");
    }

    @Test
    public void invokeMaskedFormat() {
        FunctionTestUtil.assertResult(stringFunction.invoke("%s is here!", new Object[]{"Gorgonzola"}), "Gorgonzola is here!");
    }
}