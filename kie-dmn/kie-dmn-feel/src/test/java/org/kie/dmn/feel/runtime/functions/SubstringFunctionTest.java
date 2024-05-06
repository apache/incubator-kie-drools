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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class SubstringFunctionTest {

    private SubstringFunction substringFunction;

    @BeforeEach
    void setUp() {
        substringFunction = new SubstringFunction();
    }

    @Test
    void invokeNull2ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0), InvalidParametersEvent.class);
    }

    @Test
    void invokeNull3ParamsMethod() {
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, null, 2), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke(null, 0, 2), InvalidParametersEvent.class);
    }

    @Test
    void invokeStartZero() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 0, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeStartOutOfListBounds() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 10, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", -10, null), InvalidParametersEvent.class);
    }

    @Test
    void invokeLengthNegative() {
        FunctionTestUtil.assertResultError(substringFunction.invoke("test", 1, -3), InvalidParametersEvent.class);
    }

    @Test
    void invokeLengthOutOfListBounds() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 3), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 3), "est");
    }

    @Test
    void invokeStartPositive() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 1), "test");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2), "est");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 4), "t");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 1), "e");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", 2, 2), "es");
    }

    @Test
    void invokeStartNegative() {
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -1), "t");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2), "st");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -4), "test");

        FunctionTestUtil.assertResult(substringFunction.invoke("test", -2, 1), "s");
        FunctionTestUtil.assertResult(substringFunction.invoke("test", -3, 2), "es");
    }
}