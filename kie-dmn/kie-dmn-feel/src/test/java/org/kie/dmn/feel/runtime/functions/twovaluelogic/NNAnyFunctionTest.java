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
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

public class NNAnyFunctionTest {

    private NNAnyFunction anyFunction;

    @Before
    public void setUp() {
        anyFunction = new NNAnyFunction();
    }

    @Test
    public void invokeBooleanParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((Boolean) null), false);
    }

    @Test
    public void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(true), true);
    }

    @Test
    public void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(false), false);
    }

    @Test
    public void invokeArrayParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((Object[]) null), false);
    }

    @Test
    public void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{}), false);
    }

    @Test
    public void invokeArrayParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null}), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.FALSE}), true);
    }

    @Test
    public void invokeArrayParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.FALSE, Boolean.FALSE}), false);
    }

    @Test
    public void invokeArrayParamReturnNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke(new Object[]{Boolean.FALSE, null, Boolean.FALSE}), false);
    }

    @Test
    public void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke((List) null), false);
    }

    @Test
    public void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Collections.emptyList()), false);
    }

    @Test
    public void invokeListParamReturnTrue() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null)), true);
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.FALSE)), true);
    }

    @Test
    public void invokeListParamReturnFalse() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.FALSE, Boolean.FALSE)), false);
    }

    @Test
    public void invokeListParamReturnNull() {
        FunctionTestUtil.assertResult(anyFunction.invoke(Arrays.asList(Boolean.FALSE, null, Boolean.FALSE)), false);
    }

    @Test
    public void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(anyFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}