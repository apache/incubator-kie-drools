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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class AllFunctionTest {

    private AllFunction allFunction;

    @BeforeEach
    void setUp() {
        allFunction = new AllFunction();
    }

    @Test
    void invokeBooleanParamNull() {
        FunctionTestUtil.assertResultNull(allFunction.invoke((Boolean) null));
    }

    @Test
    void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(true), true);
    }

    @Test
    void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(false), false);
    }

    @Test
    void invokeArrayParamNull() {
        FunctionTestUtil.assertResultError(allFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{}), true);
    }

    @Test
    void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
    }

    @Test
    void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), false);
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), false);
    }

    @Test
    void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResultNull(allFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.TRUE}));
    }

    @Test
    void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamNull() {
        FunctionTestUtil.assertResultError(allFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(allFunction.invoke(Collections.emptyList()), true);
    }

    @Test
    void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
    }

    @Test
    void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), false);
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), false);
    }

    @Test
    void invokeListParamReturnNull() {
        FunctionTestUtil.assertResultNull(allFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.TRUE)));
    }

    @Test
    void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}