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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class SumFunctionTest {

    private SumFunction sumFunction;

    @BeforeEach
    void setUp() {
        sumFunction = new SumFunction();
    }

    @Test
    void invokeNumberParamNull() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((Number) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeNumberParamUnsupportedNumber() {
        FunctionTestUtil.assertResultError(sumFunction.invoke(Double.NaN), InvalidParametersEvent.class);
    }

    @Test
    void invokeNumberParamSupportedNumber() {
        FunctionTestUtil.assertResult(sumFunction.invoke(BigDecimal.TEN), BigDecimal.TEN);
        FunctionTestUtil.assertResult(sumFunction.invoke(10), BigDecimal.TEN);
        FunctionTestUtil.assertResult(sumFunction.invoke(-10), BigDecimal.valueOf(-10));
        FunctionTestUtil.assertResult(sumFunction.invoke(10.12), BigDecimal.valueOf(10.12));
    }

    @Test
    void invokeListParam() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamContainsUnsupportedNumber() {
        FunctionTestUtil.assertResultError(sumFunction.invoke(Arrays.asList(10, 2, Double.NaN)), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamContainsUnsupportedType() {
        FunctionTestUtil.assertResultError(sumFunction.invoke(Arrays.asList(10, "test", 2)), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamSupportedTypes() {
        FunctionTestUtil.assertResult(sumFunction.invoke(Arrays.asList(4, -1, 12.1, (long) 5, BigDecimal.TEN)), BigDecimal.valueOf(30.1));
    }

    @Test
    void invokeArrayParam() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayParamContainsUnsupportedNumber() {
        FunctionTestUtil.assertResultError(sumFunction.invoke(new Object[]{10, 2, Double.NaN}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayParamContainsUnsupportedType() {
        FunctionTestUtil.assertResultError(sumFunction.invoke(new Object[]{10, "test", 2}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayParamSupportedTypes() {
        FunctionTestUtil.assertResult(sumFunction.invoke(new Object[]{4, -1, 12.1, (long) 5, BigDecimal.TEN}), BigDecimal.valueOf(30.1));
    }
}