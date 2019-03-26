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

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringFunctionTest {

    private SubstringFunction substringFunction;

    @Before
    public void setUp() {
        substringFunction = new SubstringFunction();
    }

    @Test
    public void invokeNull2ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0), InvalidParametersEvent.class);
    }

    @Test
    public void invokeNull3ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, 2), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartZero() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartOutOfListBounds() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10, null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthNegative() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 1, -3), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthOutOfListBounds() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 3), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 3), "est");
    }

    @Test
    public void invokeStartPositive() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 1), "test");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 4), "t");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 1), "e");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 2), "es");
    }

    @Test
    public void invokeStartNegative() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -1), "t");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2), "st");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -4), "test");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2, 1), "s");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 2), "es");
    }
}