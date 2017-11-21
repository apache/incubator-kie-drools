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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

import org.junit.Before;
import org.junit.Test;

public class ComposingDifferentFunctionsTest {

    private DateAndTimeFunction dateTimeFunction;
    private TimeFunction timeFunction;

    @Before
    public void setUp() {
        dateTimeFunction = new DateAndTimeFunction();
        timeFunction = new TimeFunction();
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
}