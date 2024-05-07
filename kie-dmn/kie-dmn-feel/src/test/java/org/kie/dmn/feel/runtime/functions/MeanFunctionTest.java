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
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class MeanFunctionTest {

    private MeanFunction meanFunction;

    @BeforeEach
    void setUp() {
        meanFunction = new MeanFunction();
    }

    @Test
    void invokeNumberNull() {
        FunctionTestUtil.assertResultError(meanFunction.invoke((Number) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeNumberBigDecimal() {
        FunctionTestUtil.assertResult(meanFunction.invoke(BigDecimal.TEN), BigDecimal.TEN);
    }

    @Test
    void invokeNumberInteger() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10), BigDecimal.TEN);
    }

    @Test
    void invokeNumberDoubleWithoutDecimalPart() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10d), BigDecimal.valueOf(10));
    }

    @Test
    void invokeNumberDoubleWithDecimalPart() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10.1d), BigDecimal.valueOf(10.1));
    }

    @Test
    void invokeNumberFloat() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10.1f), BigDecimal.valueOf(10.1));
    }

    @Test
    void invokeUnconvertableNumber() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.POSITIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.NEGATIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.NaN), InvalidParametersEvent.class);
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResultError(meanFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeListEmpty() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(Collections.emptyList()), InvalidParametersEvent.class);
    }

    @Test
    void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    void invokeListWithIntegers() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10, 20, 30)), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10, 20, 30, -10, -20, -30)), BigDecimal.ZERO);
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(0, 0, 1)), new BigDecimal("0.3333333333333333333333333333333333"));
    }

    @Test
    void invokeListWithDoubles() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10.0d, 20.0d, 30.0d)), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10.2d, 20.2d, 30.2d)), BigDecimal.valueOf(20.2));
    }

    @Test
    void invokeArrayNull() {
        FunctionTestUtil.assertResultError(meanFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayEmpty() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(new Object[]{}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayWithIntegers() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10, 20, 30}), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10, 20, 30, -10, -20, -30}), BigDecimal.ZERO);
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{0, 0, 1}), new BigDecimal("0.3333333333333333333333333333333333"));
    }

    @Test
    void invokeArrayWithDoubles() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10.0d, 20.0d, 30.0d}), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10.2d, 20.2d, 30.2d}), BigDecimal.valueOf(20.2));
    }
}