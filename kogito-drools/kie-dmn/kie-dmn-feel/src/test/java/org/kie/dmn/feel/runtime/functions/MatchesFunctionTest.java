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

public class MatchesFunctionTest {

    private MatchesFunction matchesFunction;

    @Before
    public void setUp() {
        matchesFunction = new MatchesFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeUnsupportedFlags() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "fo.bar", "g"), true);
    }

    @Test
    public void invokeWithoutFlagsMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "test"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*b"), true);
    }

    @Test
    public void invokeWithoutFlagsNotMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "testt"), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*bb"), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "fo.bar"), false);
    }

    @Test
    public void invokeWithFlagDotAll() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "fo.bar", "s"), true);
    }

    @Test
    public void invokeWithFlagMultiline() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "^bar", "m"), true);
    }

    @Test
    public void invokeWithFlagCaseInsensitive() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^Fo*bar", "i"), true);
    }

    @Test
    public void invokeWithAllFlags() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "Fo.^bar", "smi"), true);
    }
}