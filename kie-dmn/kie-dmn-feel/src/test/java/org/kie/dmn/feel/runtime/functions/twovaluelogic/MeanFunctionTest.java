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

package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MeanFunctionTest {

    private NNMeanFunction meanFunction;

    @Before
    public void setUp() {
        meanFunction = new NNMeanFunction();
    }

    @Test
    public void invokeNumberNull() {
        FunctionTestUtil.assertResult(meanFunction.invoke((Number) null), null);
    }

    @Test
    public void invokeNumberBigDecimal() {
        FunctionTestUtil.assertResult(meanFunction.invoke(BigDecimal.TEN), BigDecimal.TEN);
    }

    @Test
    public void invokeNumberInteger() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10), BigDecimal.TEN);
    }

    @Test
    public void invokeNumberDoubleWithoutDecimalPart() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10d), BigDecimal.valueOf(10));
    }

    @Test
    public void invokeNumberDoubleWithDecimalPart() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10.1d), BigDecimal.valueOf(10.1));
    }

    @Test
    public void invokeNumberFloat() {
        FunctionTestUtil.assertResult(meanFunction.invoke(10.1f), BigDecimal.valueOf(10.1));
    }

    @Test
    public void invokeUnconvertableNumber() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.POSITIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.NEGATIVE_INFINITY), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(meanFunction.invoke(Double.NaN), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResult(meanFunction.invoke((List) null), null);
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(Arrays.asList(1, "test")), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(20, 30, null, (long) 40, null, BigDecimal.TEN)), BigDecimal.valueOf(25));
    }

    @Test
    public void invokeListWithIntegers() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10, 20, 30)), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10, 20, 30, -10, -20, -30)), BigDecimal.ZERO);
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(0, 0, 1)), new BigDecimal("0.3333333333333333333333333333333333"));
    }

    @Test
    public void invokeListWithDoubles() {
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10.0d, 20.0d, 30.0d)), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(Arrays.asList(10.2d, 20.2d, 30.2d)), BigDecimal.valueOf(20.2));
    }

    @Test
    public void invokeArrayNull() {
        FunctionTestUtil.assertResult(meanFunction.invoke((Object[]) null), null);
    }

    @Test
    public void invokeArrayEmpty() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{}), null);
    }

    @Test
    public void invokeArrayTypeHeterogenous() {
        FunctionTestUtil.assertResultError(meanFunction.invoke(new Object[]{1, "test"}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayWithIntegers() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10, 20, 30}), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10, 20, 30, -10, -20, -30}), BigDecimal.ZERO);
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{0, 0, 1}), new BigDecimal("0.3333333333333333333333333333333333"));
    }

    @Test
    public void invokeArrayWithDoubles() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10.0d, 20.0d, 30.0d}), BigDecimal.valueOf(20));
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{10.2d, 20.2d, 30.2d}), BigDecimal.valueOf(20.2));
    }

    @Test
    public void invokeArrayParamSupportedTypesWithNull() {
        FunctionTestUtil.assertResult(meanFunction.invoke(new Object[]{20, 30, null, (long) 40, null, BigDecimal.TEN}), BigDecimal.valueOf(25));
    }

}