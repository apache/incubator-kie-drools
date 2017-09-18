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

public class EndsWithFunctionTest {

    private EndsWithFunction endsWithFunction;

    @Before
    public void setUp() {
        endsWithFunction = new EndsWithFunction();
    }

    @Test
    public void invokeParamsNull() {
        FunctionTestUtil.assertResultError(endsWithFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(endsWithFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(endsWithFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEndsWith() {
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "t"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "st"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "est"), true);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "test"), true);
    }

    @Test
    public void invokeNotEndsWith() {
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "es"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "ttttt"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "estt"), false);
        FunctionTestUtil.assertResult(endsWithFunction.invoke("test", "tt"), false);
    }
}