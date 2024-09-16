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

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class MatchesFunctionTest {

    private final static MatchesFunction matchesFunction = MatchesFunction.INSTANCE;

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeUnsupportedFlags() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke("foobar", "fo.bar", "g"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "p"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "X"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "X"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "iU"), InvalidParametersEvent.class);
   }

    @Test
    void invokeWithoutFlagsMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "test"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*b"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("(?xi)[hello world()]", "hello"), true);
    }

    @Test
    void invokeNullOrEmptyFlagsMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "test",null), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*b",null), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra", ""), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra",null), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("(?xi)[hello world()]", "hello",null), true);
    }

    @Test
    void invokeWithoutFlagsNotMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "testt",null), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*bb",null), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "fo.bar",null), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("h", "(.)\3",null), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("h", "(.)\2",null), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("input", "\3",null), false);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("fo\nbar", "(?iU)(?iU)(ab)[|cd]",null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("fo\nbar", "(?x)(?i)hello world","i"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("fo\nbar", "(?xi)hello world",null), InvalidParametersEvent.class);
    }


    @Test
    void invokeWithFlagDotAll() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "fo.bar", "s"), true);
    }

    @Test
    void invokeWithFlagMultiline() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "^bar", "m"), true);
    }

    @Test
    void invokeWithFlagCaseInsensitive() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^Fo*bar", "i"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^Fo*bar", "i"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("\u212A", "k","i"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("\u212A", "K","i"), true);
    }

    @Test
    void invokeWithFlagComments() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("hello world", "hello"+"\"+ sworld", "x"), false);
    }

    @Test
    void invokeWithAllFlags() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "Fo.^bar", "smi"), true);
    }

    @Test
    void checkForPatternTest() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke("foobar", "(abc|def(ghi", "i"), InvalidParametersEvent.class);
    }

}