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

class NNAllFunctionTest {

    private NNAllFunction NNAllFunction;

    @BeforeEach
    void setUp() {
        NNAllFunction = new NNAllFunction();
    }

    @Test
    void invokeBooleanParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((Boolean) null), true);
    }

    @Test
    void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(true), true);
    }

    @Test
    void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(false), false);
    }

    @Test
    void invokeArrayParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((Object[]) null), true);
    }

    @Test
    void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{}), true);
    }

    @Test
    void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
    }

    @Test
    void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), false);
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), false);
    }

    @Test
    void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.TRUE}), true);
    }

    @Test
    void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke((List) null), true);
    }

    @Test
    void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Collections.emptyList()), true);
    }

    @Test
    void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
    }

    @Test
    void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), false);
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), false);
    }

    @Test
    void invokeListParamReturnNull() {
        FunctionTestUtil.assertResult(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.TRUE)), true);
    }

    @Test
    void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(NNAllFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}