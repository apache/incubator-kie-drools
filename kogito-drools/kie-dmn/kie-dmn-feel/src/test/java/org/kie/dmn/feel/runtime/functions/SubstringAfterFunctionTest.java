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

public class SubstringAfterFunctionTest {

    private SubstringAfterFunction substringAfterFunction;

    @Before
    public void setUp() {
        substringAfterFunction = new SubstringAfterFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(substringAfterFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeMatchExists() {
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "ob"), "ar");
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "o"), "obar");
    }

    @Test
    public void invokeMatchNotExists() {
        FunctionTestUtil.assertResult(substringAfterFunction.invoke("foobar", "oook"), "foobar");
    }
}