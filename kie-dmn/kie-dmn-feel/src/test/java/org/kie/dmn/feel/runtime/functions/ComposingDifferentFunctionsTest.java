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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComposingDifferentFunctionsTest {

    private DateAndTimeFunction dateTimeFunction;
    private DateFunction dateFunction;
    private TimeFunction timeFunction;
    private StringFunction stringFunction;

    @BeforeEach
    void setUp() {
        dateTimeFunction = new DateAndTimeFunction();
        dateFunction = new DateFunction();
        timeFunction = new TimeFunction();
        stringFunction = new StringFunction();
    }

    @Test
    void composite1() {
        final FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("2017-08-10T10:20:00+02:00");
        final FEELFnResult<TemporalAccessor> p2 = timeFunction.invoke("23:59:01");

        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(2017, 8, 10, 10, 20, 0, 0, ZoneId.of("+02:00")));
        FunctionTestUtil.assertResult(p2, LocalTime.of(23, 59, 1));

        final FEELFnResult<TemporalAccessor> result = dateTimeFunction.invoke(p1.getOrElse(null), p2.getOrElse(null));
        FunctionTestUtil.assertResult(result, LocalDateTime.of(2017, 8, 10, 23, 59, 1));
    }

    @Test
    void composite2() {
        final FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999+02:00");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of("+02:00")));

        FunctionTestUtil.assertResult(stringFunction.invoke(p1.getOrElse(null)), "-999999999-12-31T23:59:59.999999999+02:00");
    }

    @Test
    void composite3() {
        final FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999@Europe/Paris");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneId.of("Europe/Paris")));

        FunctionTestUtil.assertResult(stringFunction.invoke(p1.getOrElse(null)), "-999999999-12-31T23:59:59.999999999@Europe/Paris");
    }

    @Test
    void composite4() {
        final FEELFnResult<TemporalAccessor> p1 = dateFunction.invoke("2017-01-01");
        final FEELFnResult<TemporalAccessor> p2 = timeFunction.invoke("23:59:01@Europe/Paris");

        FunctionTestUtil.assertResult(p1, LocalDate.of(2017, 1, 1));

        final TemporalAccessor p2TA = p2.getOrElse(null);
        assertThat(p2TA).isNotNull();
        assertThat(p2TA.query(TemporalQueries.localTime())).isEqualTo(LocalTime.of(23, 59, 1));
        assertThat(p2TA.query(TemporalQueries.zone())).isEqualTo(ZoneId.of("Europe/Paris"));

        final FEELFnResult<TemporalAccessor> result = dateTimeFunction.invoke(p1.getOrElse(null), p2.getOrElse(null));
        FunctionTestUtil.assertResult(result, ZonedDateTime.of(2017, 1, 1, 23, 59, 1, 0, ZoneId.of("Europe/Paris")));
    }

    @Test
    void composite5() {
        final FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("2017-08-10T10:20:00@Europe/Paris");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(2017, 8, 10, 10, 20, 0, 0, ZoneId.of("Europe/Paris")));

        final TemporalAccessor timeOnDateTime = timeFunction.invoke(p1.getOrElse(null)).getOrElse(null);
        assertThat(timeOnDateTime).isNotNull();
        assertThat(timeOnDateTime.query(TemporalQueries.localTime())).isEqualTo(LocalTime.of(10, 20, 0));
        assertThat(timeOnDateTime.query(TemporalQueries.zone())).isEqualTo(ZoneId.of("Europe/Paris"));

        FunctionTestUtil.assertResult(stringFunction.invoke(timeOnDateTime), "10:20@Europe/Paris");
    }
}