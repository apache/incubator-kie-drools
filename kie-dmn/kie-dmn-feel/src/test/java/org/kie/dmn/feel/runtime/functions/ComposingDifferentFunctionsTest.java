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
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.junit.Before;
import org.junit.Test;

public class ComposingDifferentFunctionsTest {

    private DateAndTimeFunction dateTimeFunction;
    private DateFunction dateFunction;
    private TimeFunction timeFunction;
    private StringFunction stringFunction;

    @Before
    public void setUp() {
        dateTimeFunction = new DateAndTimeFunction();
        dateFunction = new DateFunction();
        timeFunction = new TimeFunction();
        stringFunction = new StringFunction();
    }

    @Test
    public void testComposite1() {
        FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("2017-08-10T10:20:00+02:00");
        FEELFnResult<TemporalAccessor> p2 = timeFunction.invoke("23:59:01");

        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(2017, 8, 10, 10, 20, 0, 0, ZoneId.of("+02:00")));
        FunctionTestUtil.assertResult(p2, LocalTime.of(23, 59, 1));

        FEELFnResult<TemporalAccessor> result = dateTimeFunction.invoke(p1.getOrElse(null), p2.getOrElse(null));
        FunctionTestUtil.assertResult(result, LocalDateTime.of(2017, 8, 10, 23, 59, 1));
    }

    @Test
    public void testComposite2() {
        FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999+02:00");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.of("+02:00")));

        FunctionTestUtil.assertResult(stringFunction.invoke(p1.getOrElse(null)), "-999999999-12-31T23:59:59.999999999+02:00");
    }

    @Test
    public void testComposite3() {
        FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("-999999999-12-31T23:59:59.999999999@Europe/Paris");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(-999999999, 12, 31, 23, 59, 59, 999_999_999, ZoneId.of("Europe/Paris")));

        FunctionTestUtil.assertResult(stringFunction.invoke(p1.getOrElse(null)), "-999999999-12-31T23:59:59.999999999@Europe/Paris");
    }

    @Test
    public void testComposite4() {
        FEELFnResult<TemporalAccessor> p1 = dateFunction.invoke("2017-01-01");
        FEELFnResult<TemporalAccessor> p2 = timeFunction.invoke("23:59:01@Europe/Paris");

        FunctionTestUtil.assertResult(p1, LocalDate.of(2017, 1, 1));

        TemporalAccessor p2TA = p2.getOrElse(null);
        assertNotNull(p2TA);
        assertEquals(LocalTime.of(23, 59, 1), p2TA.query(TemporalQueries.localTime()));
        assertEquals(ZoneId.of("Europe/Paris"), p2TA.query(TemporalQueries.zone()));

        FEELFnResult<TemporalAccessor> result = dateTimeFunction.invoke(p1.getOrElse(null), p2.getOrElse(null));
        FunctionTestUtil.assertResult(result, ZonedDateTime.of(2017, 1, 1, 23, 59, 1, 0, ZoneId.of("Europe/Paris")));
    }

    @Test
    public void testComposite5() {
        FEELFnResult<TemporalAccessor> p1 = dateTimeFunction.invoke("2017-08-10T10:20:00@Europe/Paris");
        FunctionTestUtil.assertResult(p1, ZonedDateTime.of(2017, 8, 10, 10, 20, 0, 0, ZoneId.of("Europe/Paris")));

        TemporalAccessor timeOnDateTime = timeFunction.invoke(p1.getOrElse(null)).getOrElse(null);
        assertNotNull(timeOnDateTime);
        assertEquals(LocalTime.of(10, 20, 0), timeOnDateTime.query(TemporalQueries.localTime()));
        assertEquals(ZoneId.of("Europe/Paris"), timeOnDateTime.query(TemporalQueries.zone()));

        FunctionTestUtil.assertResult(stringFunction.invoke(timeOnDateTime), "10:20:00@Europe/Paris");
    }
}