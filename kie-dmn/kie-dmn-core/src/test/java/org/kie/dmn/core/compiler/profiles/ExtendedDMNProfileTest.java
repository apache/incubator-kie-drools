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

package org.kie.dmn.core.compiler.profiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.AbsFunction;
import org.kie.dmn.feel.runtime.functions.EvenFunction;
import org.kie.dmn.feel.runtime.functions.ExpFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.LogFunction;
import org.kie.dmn.feel.runtime.functions.MedianFunction;
import org.kie.dmn.feel.runtime.functions.ModeFunction;
import org.kie.dmn.feel.runtime.functions.ModuloFunction;
import org.kie.dmn.feel.runtime.functions.OddFunction;
import org.kie.dmn.feel.runtime.functions.ProductFunction;
import org.kie.dmn.feel.runtime.functions.SplitFunction;
import org.kie.dmn.feel.runtime.functions.SqrtFunction;
import org.kie.dmn.feel.runtime.functions.StddevFunction;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;

import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExtendedDMNProfileTest {
    private final DateFunction dateFunction = DateFunction.INSTANCE;
    private final TimeFunction timeFunction = TimeFunction.INSTANCE;
    private final SplitFunction splitFunction = SplitFunction.INSTANCE;
    private final ProductFunction productFunction = ProductFunction.INSTANCE;
    private final MedianFunction medianFunction = MedianFunction.INSTANCE;
    private final StddevFunction stddevFunction = StddevFunction.INSTANCE;
    private final ModeFunction modeFunction = ModeFunction.INSTANCE;
    private final AbsFunction absFunction = AbsFunction.INSTANCE;
    private final ModuloFunction moduloFunction = ModuloFunction.INSTANCE;
    private final SqrtFunction sqrtFunction = SqrtFunction.INSTANCE;
    private final LogFunction logFunction = LogFunction.INSTANCE;
    private final ExpFunction expFunction = ExpFunction.INSTANCE;
    private final EvenFunction evenFunction = EvenFunction.INSTANCE;
    private final OddFunction oddFunction = OddFunction.INSTANCE;

    @Test
    public void testDateFunction_invokeParamStringDateTime() {
        assertResult(dateFunction.invoke("2017-09-07T10:20:30"), LocalDate.of(2017, 9, 7));
    }

    @Test
    public void testDateFunction_invokeExtended() {
        assertResult(dateFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
        assertResult(dateFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
        assertResult(dateFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_DATE.parse( "2016-12-20", LocalDate::from ));
    }

    @Test
    public void testTimeFunction_invokeStringParamDate() {
        assertResult(timeFunction.invoke("2017-10-09"), LocalTime.of(0,0,0));
        assertResult(timeFunction.invoke("2017-10-09T10:15:06"), LocalTime.of(10,15,6));
    }

    @Test
    public void testTimeFunction_invokeExtended() {
        assertResult(timeFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_TIME.parse( "14:30:22", LocalTime::from ));
        assertResult(timeFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_TIME.parse( "14:30:22-05:00", OffsetTime::from ));
        assertResult(timeFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_TIME.parse( "14:30:22z", OffsetTime::from ));
    }

    @Test
    public void testSplitFunction() {
        assertResult(splitFunction.invoke("John Doe", "\\s"), Arrays.asList("John", "Doe"));
        assertResult(splitFunction.invoke("a;b;c;;", ";"), Arrays.asList("a", "b", "c", "", ""));
    }

    @Test
    public void testProductFunction() {
        assertResult(productFunction.invoke(Arrays.asList(valueOf(2), valueOf(3), valueOf(4))), valueOf(24));
    }

    @Test
    public void testMedianFunction() {
        assertResult(medianFunction.invoke(new Object[]{valueOf(8), valueOf(2), valueOf(5), valueOf(3), valueOf(4)}), valueOf(4));
        assertResult(medianFunction.invoke(Arrays.asList(valueOf(6), valueOf(1), valueOf(2), valueOf(3))), valueOf(2.5));
        assertNull(medianFunction.invoke(new Object[]{}));
    }

    @Test
    public void testStddevFunction() {
        assertResultDoublePrecision(stddevFunction.invoke(new Object[]{2, 4, 7, 5}), valueOf(2.0816659994661326));
    }

    @Test
    public void testModeFunction() {
        assertResult(modeFunction.invoke(new Object[]{6, 3, 9, 6, 6}), Collections.singletonList(valueOf(6)));
        assertResult(modeFunction.invoke(Arrays.asList(6, 1, 9, 6, 1)), Arrays.asList(valueOf(1), valueOf(6)));
        assertResult(modeFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void testModuloFunction() {
        assertResult(moduloFunction.invoke(valueOf(12), valueOf(5)), valueOf(2));
    }

    @Test
    public void testSqrtFunction() {
        assertResultDoublePrecision(sqrtFunction.invoke(valueOf(16)), valueOf(4));
        assertResultDoublePrecision(sqrtFunction.invoke(valueOf(2)), valueOf(1.4142135623730951));
    }

    @Test
    public void testLogFunction() {
        assertResultDoublePrecision(logFunction.invoke(valueOf(10)), valueOf(2.302585092994046));
    }

    @Test
    public void testExpFunction() {
        assertResultDoublePrecision(expFunction.invoke(valueOf(5)), valueOf(148.4131591025766));
    }

    @Test
    public void testOddFunction() {
        assertResult(oddFunction.invoke(valueOf(5)), Boolean.TRUE);
        assertResult(oddFunction.invoke(valueOf(2)), Boolean.FALSE);
    }

    @Test
    public void testOddFunction_fractional() {
        assertNull(oddFunction.invoke(valueOf(5.5)));
        assertResult(oddFunction.invoke(valueOf(5.0)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction() {
        assertResult(evenFunction.invoke(valueOf(5)), Boolean.FALSE);
        assertResult(evenFunction.invoke(valueOf(2)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction_fractional() {
        assertNull(evenFunction.invoke(valueOf(5.5)));
        assertResult(evenFunction.invoke(valueOf(2.0)), Boolean.TRUE);
    }

    private static <T> void assertResult(final FEELFnResult<T> result, final T val) {
        assertTrue(result.isRight());
        assertThat(result.getOrElse(null), Matchers.equalTo(val));
    }

    private static void assertResultDoublePrecision(final FEELFnResult<BigDecimal> result, final BigDecimal val) {
        assertTrue(result.isRight());
        assertThat(Double.compare(result.getOrElse(null).doubleValue(), val.doubleValue()), Matchers.equalTo(0));
    }

    private static void assertNull(final FEELFnResult<?> result) {
        Assert.assertNull(result.getOrElse(null));
    }
}
