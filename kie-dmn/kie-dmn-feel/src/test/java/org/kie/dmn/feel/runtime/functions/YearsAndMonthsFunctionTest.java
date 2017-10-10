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

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.chrono.JapaneseDate;
import java.time.temporal.Temporal;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.mockito.cglib.core.Local;

public class YearsAndMonthsFunctionTest {

    private YearsAndMonthsFunction yamFunction;

    @Before
    public void setUp() {
        yamFunction = new YearsAndMonthsFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(yamFunction.invoke((Temporal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(yamFunction.invoke(null, LocalDate.now()), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(yamFunction.invoke(LocalDate.now(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeUnsupportedTemporal() {
        FunctionTestUtil.assertResultError(yamFunction.invoke(Instant.EPOCH, Instant.EPOCH), InvalidParametersEvent.class);
    }

    @Test
    public void invokeYear() {
        FunctionTestUtil.assertResult(yamFunction.invoke(Year.of(2017), Year.of(2020)), Period.of(3, 0, 0));
        FunctionTestUtil.assertResult(yamFunction.invoke(Year.of(2017), Year.of(2014)), Period.of(-3, 0, 0));
    }

    @Test
    public void invokeYearMonth() {
        FunctionTestUtil.assertResult(
                yamFunction.invoke(YearMonth.of(2017, 6), Year.of(2020)),
                Period.of(2, 7, 0));
        FunctionTestUtil.assertResult(
                yamFunction.invoke(YearMonth.of(2017, 6), Year.of(2014)),
                Period.of(-3, -5, 0));
    }

    @Test
    public void invokeYearLocalDate() {
        FunctionTestUtil.assertResult(
                yamFunction.invoke(LocalDate.of(2017, 6, 12), Year.of(2020)),
                Period.of(2, 6, 0));
    }

    @Test
    public void invokeYearMonthLocalDate() {
        FunctionTestUtil.assertResult(
                yamFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        YearMonth.of(2020, 4)),
                Period.of(2, 9, 0));
    }

    @Test
    public void invokeLocalDateLocalDate() {
        FunctionTestUtil.assertResult(
                yamFunction.invoke(
                        LocalDate.of(2017, 6, 12),
                        LocalDate.of(2020, 7, 13)),
                Period.of(3, 1, 0));
    }

    @Test
    public void invokeLocalDateTime() {
        FunctionTestUtil.assertResult(
                yamFunction.invoke(
                        LocalDateTime.of(2017, 6, 12, 12, 43),
                        LocalDate.of(2020, 7, 13)),
                Period.of(3, 1, 0));
    }
}