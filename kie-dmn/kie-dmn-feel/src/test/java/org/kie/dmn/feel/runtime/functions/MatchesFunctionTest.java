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

import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

class MatchesFunctionTest {

    private final static MatchesFunction matchesFunction = MatchesFunction.INSTANCE;

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("test", null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", " "), InvalidParametersEvent.class);
    }

    @Test
    void invokeUnsupportedFlags() {
        FunctionTestUtil.assertResultError(matchesFunction.invoke("foobar", "fo.bar", "g"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "p"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(matchesFunction.invoke("abracadabra", "bra", "X"), InvalidParametersEvent.class);
    }

    @Test
    void invokeWithoutFlagsMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "test"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*b"), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra", ""), true);
        FunctionTestUtil.assertResult(matchesFunction.invoke("abracadabra", "bra",null), true);
    }

    @Test
    void invokeWithoutFlagsNotMatch() {
        FunctionTestUtil.assertResult(matchesFunction.invoke("test", "testt"), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("foobar", "^fo*bb"), false);
        FunctionTestUtil.assertResult(matchesFunction.invoke("fo\nbar", "fo.bar"), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\3",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\2",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("input", "\3",null), false);
    }

    @Test
    void invokeWithFlagDotAll() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "fo.bar", "s"), true);
    }

    @Test
    void invokeWithFlagMultiline() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "^bar", "m"), true);
    }

    @Test
    void invokeWithFlagCaseInsensitive() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^Fo*bar", "i"), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^Fo*bar", "i"), true);
        //FunctionTestUtil.assertResult(matchesFunction.invoke("O", "[A-Z-[OI]]", "i"), false);//need to check
        //FunctionTestUtil.assertResult(matchesFunction.invoke("i", "[A-Z-[OI]]", "i"), false);//need to check
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("\u212A", "K","iU"), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("\u212A", "k","iU"), true);
    }

    @Test
    void invokeWithFlagComments() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("hello world", "hello"+"\"+ sworld", "x"), false);
    }

    @Test
    void invokeWithAllFlags() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "Fo.^bar", "smi"), true);
    }
}