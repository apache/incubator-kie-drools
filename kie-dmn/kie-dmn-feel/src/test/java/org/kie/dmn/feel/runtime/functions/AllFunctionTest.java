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

package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AllFunctionTest {

    private AllFunction allFunction;

    @Before
    public void setUp() {
        allFunction = new AllFunction();
    }

    @Test
    public void invokeBooleanParamNull() {
        FunctionTestUtil.assertResultNull(allFunction.invoke((Boolean) null));
    }

    @Test
    public void invokeBooleanParamTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(true), true);
    }

    @Test
    public void invokeBooleanParamFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(false), false);
    }

    @Test
    public void invokeArrayParamNull() {
        FunctionTestUtil.assertResultError(allFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayParamEmptyArray() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{}), true);
    }

    @Test
    public void invokeArrayParamTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE}), true);
    }

    @Test
    public void invokeArrayParamFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, Boolean.FALSE}), false);
        FunctionTestUtil.assertResult(allFunction.invoke(new Object[]{Boolean.TRUE, null, Boolean.TRUE}), false);
    }

    @Test
    public void invokeArrayParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.TRUE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.FALSE, 1}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(new Object[]{Boolean.TRUE, null, 1}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamNull() {
        FunctionTestUtil.assertResultError(allFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListParamEmptyList() {
        FunctionTestUtil.assertResult(allFunction.invoke(Collections.emptyList()), true);
    }

    @Test
    public void invokeListParamTrue() {
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.TRUE)), true);
    }

    @Test
    public void invokeListParamFalse() {
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), false);
        FunctionTestUtil.assertResult(allFunction.invoke(Arrays.asList(Boolean.TRUE, null, Boolean.TRUE)), false);
    }

    @Test
    public void invokeListParamTypeHeterogenousArray() {
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.TRUE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.FALSE, 1)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(allFunction.invoke(Arrays.asList(Boolean.TRUE, null, 1)), InvalidParametersEvent.class);
    }
}