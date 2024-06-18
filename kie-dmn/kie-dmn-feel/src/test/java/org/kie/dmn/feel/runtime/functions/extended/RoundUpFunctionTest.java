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
package org.kie.dmn.feel.runtime.functions.extended;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;

class RoundUpFunctionTest {

    private RoundUpFunction roundUpFunction;

    @BeforeEach
    void setUp() {
        roundUpFunction = new RoundUpFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(roundUpFunction.invoke(null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(roundUpFunction.invoke((BigDecimal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(roundUpFunction.invoke(BigDecimal.ONE, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(roundUpFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
    }

    @Test
    void invokeRoundingUp() {
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.27)), BigDecimal.valueOf(11));
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.27), BigDecimal.ONE), BigDecimal.valueOf(10.3));
    }

    @Test
    void invokeRoundingDown() {
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.24)), BigDecimal.valueOf(11));
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.24), BigDecimal.ONE), BigDecimal.valueOf(10.3));
    }

    @Test
    void invokeRoundingEven() {
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.25)), BigDecimal.valueOf(11));
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.25), BigDecimal.ONE), BigDecimal.valueOf(10.3));
    }

    @Test
    void invokeRoundingOdd() {
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.35)), BigDecimal.valueOf(11));
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.35), BigDecimal.ONE), BigDecimal.valueOf(10.4));
    }

    @Test
    void invokeLargerScale() {
        FunctionTestUtil.assertResult(roundUpFunction.invoke(BigDecimal.valueOf(10.123456789), BigDecimal.valueOf(6)), BigDecimal.valueOf(10.123457));
    }

    @Test
    void invokeOutRangeScale() {
        FunctionTestUtil.assertResultError(roundUpFunction.invoke(BigDecimal.valueOf(1.5), BigDecimal.valueOf(6177)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(roundUpFunction.invoke(BigDecimal.valueOf(1.5), BigDecimal.valueOf(-6122)), InvalidParametersEvent.class);
    }
}