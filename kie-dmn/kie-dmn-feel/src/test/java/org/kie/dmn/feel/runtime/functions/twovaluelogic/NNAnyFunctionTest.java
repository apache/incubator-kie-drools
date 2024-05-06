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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

class NNAnyFunctionTest {

    private NNAnyFunction anyFunction;

    @BeforeEach
    void setUp() {
        anyFunction = new NNAnyFunction();
    }

    @Test
    void invokeBooleanParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((Boolean) null), false);
    }

    @Test
    void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(true), true);
    }

    @Test
    void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(false), false);
    }

    @Test
    void invokeArrayParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((Object[]) null), false);
    }

    @Test
    void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{}), false);
    }

    @Test
    void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), true);
    }

    @Test
    void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.FALSE, Boolean.FALSE}), false);
    }

    @Test
    void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.FALSE, null, Boolean.FALSE}), false);
    }

    @Test
    void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((List) null), false);
    }

    @Test
    void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Collections.emptyList()), false);
    }

    @Test
    void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), true);
    }

    @Test
    void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.FALSE, Boolean.FALSE)), false);
    }

    @Test
    void invokeListParamReturnNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.FALSE, null, Boolean.FALSE)), false);
    }

    @Test
    void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}