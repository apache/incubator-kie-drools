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

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DurationFunctionTest {

    private DurationFunction durationFunction;

    @Before
    public void setUp() {
        durationFunction = new DurationFunction();
    }

    @Test
    public void invokeParamStringNull() {
        FunctionTestUtil.assertResultError(durationFunction.invoke((String) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamStringInvalid() {
        FunctionTestUtil.assertResultError(durationFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("test HHH"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("testP2Y3D"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("test P2Y3D"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("P2Y3DD"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("P3DD"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("PT3HH"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(durationFunction.invoke("P2Y3M4DT3H"), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamStringDuration() {
        FunctionTestUtil.assertResult(durationFunction.invoke("P2D"), Duration.of(2, ChronoUnit.DAYS));
        FunctionTestUtil.assertResult(durationFunction.invoke("P2DT3H"), Duration.of(2, ChronoUnit.DAYS).plusHours(3));
        FunctionTestUtil.assertResult(
                durationFunction.invoke("P2DT3H28M"),
                Duration.of(2, ChronoUnit.DAYS).plusHours(3).plusMinutes(28));
        FunctionTestUtil.assertResult(
                durationFunction.invoke("P2DT3H28M15S"),
                Duration.of(2, ChronoUnit.DAYS).plusHours(3).plusMinutes(28).plusSeconds(15));
    }

    @Test
    public void invokeParamStringPeriod() {
        FunctionTestUtil.assertResult(durationFunction.invoke("P2Y3M"), Period.of(2, 3, 0));
        FunctionTestUtil.assertResult(durationFunction.invoke("P2Y3M4D"), Period.of(2, 3, 4));
    }

    @Test
    public void invokeParamTemporalAmountNull() {
        FunctionTestUtil.assertResultError(durationFunction.invoke((TemporalAmount) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamTemporalDuration() {
        FunctionTestUtil.assertResult(
                durationFunction.invoke(Duration.parse("P2DT3H28M15S")),
                Duration.of(2, ChronoUnit.DAYS).plusHours(3).plusMinutes(28).plusSeconds(15));
    }

    @Test
    public void invokeParamTemporalPeriod() {
        FunctionTestUtil.assertResult(durationFunction.invoke(Period.parse("P2Y3M4D")), Period.of(2, 3, 4));
    }

}