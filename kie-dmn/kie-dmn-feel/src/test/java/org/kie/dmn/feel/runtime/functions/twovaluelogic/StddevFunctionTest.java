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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

class StddevFunctionTest {

    private NNStddevFunction stddevFunction;

    @BeforeEach
    void setUp() {
        stddevFunction = NNStddevFunction.INSTANCE;
    }

    @Test
    void invokeNumberNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((Number) null), null);
    }

    @Test
    void invokeSingleNumber() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(BigDecimal.TEN), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10d), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10.1d), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(10.1f), InvalidParametersEvent.class);
    }

    @Test
    void invokeUnconvertableNumber() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.POSITIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.NEGATIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Double.NaN), InvalidParametersEvent.class);
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((List) null), null);
    }

    @Test
    void invokeListEmpty() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(20, 30, null, (long) 40, null, BigDecimal.TEN)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    void invokeListWithIntegers() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(10, 20, 30, 40)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    void invokeListWithDoubles() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(Arrays.asList(10.0d, 20.0d, 30.0d, 40.0d)),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    void invokeArrayNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke((Object[]) null), null);
    }

    @Test
    void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{}), null);
    }

    @Test
    void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(stddevFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayWithIntegers() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{10, 20, 30, 40}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    void invokeArrayWithDoubles() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{10.0d, 20.0d, 30.0d, 40.0d}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

    @Test
    void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(stddevFunction.invoke(new Object[]{20, 30, null, (long) 40, null, BigDecimal.TEN}),
                new BigDecimal("12.90994448735805628393088466594133", MathContext.DECIMAL128));
    }

}