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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class DateFunctionTest {

    private DateFunction dateFunction;

    @BeforeEach
    void setUp() {
        dateFunction = new DateFunction();
    }

    @Test
    void invokeParamStringNull() {
        FunctionTestUtil.assertResultError(dateFunction.invoke((String) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringNotDateOrTime() {
        FunctionTestUtil.assertResultError(dateFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke("2017-09-test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke("2017-09-89"), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamStringDate() {
        FunctionTestUtil.assertResult(dateFunction.invoke("2017-09-07"), LocalDate.of(2017, 9, 7));
    }

    @Test
    void invokeParamStringPaddingYearsDate() {
        FunctionTestUtil.assertResult(dateFunction.invoke("0001-12-31"), LocalDate.of(1, 12, 31));
        FunctionTestUtil.assertResult(dateFunction.invoke("0012-12-31"), LocalDate.of(12, 12, 31));
        FunctionTestUtil.assertResult(dateFunction.invoke("0123-12-31"), LocalDate.of(123, 12, 31));
        FunctionTestUtil.assertResult(dateFunction.invoke("1234-12-31"), LocalDate.of(1234, 12, 31));
        FunctionTestUtil.assertResultError(dateFunction.invoke("01211-12-31"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke("012117-12-31"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke("001211-12-31"), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamYearMonthDayNulls() {
        FunctionTestUtil.assertResultError(dateFunction.invoke(null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(10, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(null, 10, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(null, null, 10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(10, 10, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(10, null, 10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(null, 10, 10), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamYearMonthDayInvalidDate() {
        FunctionTestUtil.assertResultError(dateFunction.invoke(2017, 6, 59), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(2017, 59, 12), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(dateFunction.invoke(Integer.MAX_VALUE, 6, 12), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamYearMonthDay() {
        FunctionTestUtil.assertResult(dateFunction.invoke(2017, 6, 12), LocalDate.of(2017, 6, 12));
    }

    @Test
    void invokeParamTemporalNull() {
        FunctionTestUtil.assertResultError(dateFunction.invoke((TemporalAccessor) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamTemporalWrongTemporal() {
        FunctionTestUtil.assertResultError(dateFunction.invoke(DayOfWeek.MONDAY), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamTemporal() {
        FunctionTestUtil.assertResult(dateFunction.invoke(LocalDate.of(2017, 6, 12)), LocalDate.of(2017, 6, 12));
    }
}