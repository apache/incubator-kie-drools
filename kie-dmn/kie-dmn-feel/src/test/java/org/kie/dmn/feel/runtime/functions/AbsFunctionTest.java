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

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.valueOf;

public class AbsFunctionTest {

    private AbsFunction absFunction;

    @Before
    public void setUp() {
        absFunction = AbsFunction.INSTANCE;
    }

    @Test
    public void testAbsFunctionNumber() {
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(10)), valueOf(10));
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(-10)), valueOf(10));
        FunctionTestUtil.assertResultError(absFunction.invoke((BigDecimal) null), InvalidParametersEvent.class);
    }

    @Test
    public void testAbsFunctionDuration() {
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, 50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, 50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, -50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, -50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResultError(absFunction.invoke((Duration)null),
                InvalidParametersEvent.class);
    }

    @Test
    public void testAbsFunctionPeriod() {
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, 50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, 50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, -50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, -50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -1, 30, 0 ) ),
                Period.of(-1, 30, 0));
        FunctionTestUtil.assertResultError(absFunction.invoke((Period) null ),
                InvalidParametersEvent.class);
    }


}