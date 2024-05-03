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

class NumberFunctionTest {

    private NumberFunction numberFunction;

    @BeforeEach
    void setUp() {
        numberFunction = new NumberFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(numberFunction.invoke(null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(numberFunction.invoke(null, " ", null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(numberFunction.invoke(null, null, "."), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(numberFunction.invoke(null, " ", "."), InvalidParametersEvent.class);
    }

    @Test
    void invokeIllegalNumber() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("test", null, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeNumberWithLeadingZeros() {
        FunctionTestUtil.assertResult(numberFunction.invoke("009876", null, null), BigDecimal.valueOf(9876));
    }

    @Test
    void invokeNumberWithoutDecimalPart() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9876", null, null), BigDecimal.valueOf(9876));
    }

    @Test
    void invokeNumberWithGroupCharSpace() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9 876", " ", null), BigDecimal.valueOf(9876));
        FunctionTestUtil.assertResult(numberFunction.invoke("9 876 000", " ", null), BigDecimal.valueOf(9876000));
    }

    @Test
    void invokeNumberWithGroupCharComma() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9,876", ",", null), BigDecimal.valueOf(9876));
        FunctionTestUtil.assertResult(numberFunction.invoke("9,876,000", ",", null), BigDecimal.valueOf(9876000));
    }

    @Test
    void invokeNumberWithGroupCharDot() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9.876", ".", null), BigDecimal.valueOf(9876));
        FunctionTestUtil.assertResult(numberFunction.invoke("9.876.000", ".", null), BigDecimal.valueOf(9876000));
    }

    @Test
    void invokeNumberWithDecimalCharComma() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9,876", null, ","), BigDecimal.valueOf(9.876));
    }

    @Test
    void invokeNumberWithDecimalCharDot() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9.876", null, "."), BigDecimal.valueOf(9.876));
    }

    @Test
    void invokeNumberWithGroupAndDecimalChar() {
        FunctionTestUtil.assertResult(numberFunction.invoke("9 876.124", " ", "."), BigDecimal.valueOf(9876.124));
        FunctionTestUtil.assertResult(numberFunction.invoke("9 876 000.124", " ", "."), BigDecimal.valueOf(9876000.124));
        FunctionTestUtil.assertResult(numberFunction.invoke("9.876.000,124", ".", ","), BigDecimal.valueOf(9876000.124));
    }

    @Test
    void invokeIncorrectGroup() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1 000", ".", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeInvalidGroup() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1 000", "test", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyGroup() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1 000", "", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeIncorrectDecimal() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1,1", null, "."), InvalidParametersEvent.class);
    }

    @Test
    void invokeInvalidDecimal() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1.1", null, "test"), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyDecimal() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1.1", null, ""), InvalidParametersEvent.class);
    }

    @Test
    void invokeGroupEqualsDecimal() {
        FunctionTestUtil.assertResultError(numberFunction.invoke("1 000.1", ".", "."), InvalidParametersEvent.class);
    }
}