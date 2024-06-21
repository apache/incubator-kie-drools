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

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class FloorFunctionTest {

    private FloorFunction floorFunction;

    @BeforeEach
    void setUp() {
        floorFunction = new FloorFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(floorFunction.invoke(null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(floorFunction.invoke((BigDecimal) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(floorFunction.invoke(BigDecimal.ONE, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(floorFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
    }

    @Test
    void invokeZero() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.ZERO), BigDecimal.ZERO);
    }

    @Test
    void invokePositive() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(10.2)), BigDecimal.valueOf(10));
    }

    @Test
    void invokeNegative() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(-10.2)), BigDecimal.valueOf(-11));
    }

    @Test
    void invokeOutRangeScale() {
        FunctionTestUtil.assertResultError(floorFunction.invoke(BigDecimal.valueOf(1.5), BigDecimal.valueOf(6177)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(floorFunction.invoke(BigDecimal.valueOf(1.5), BigDecimal.valueOf(-6122)), InvalidParametersEvent.class);
    }
}