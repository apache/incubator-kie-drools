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
import org.mockito.MockedStatic;

import java.security.InvalidParameterException;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MatchesFunctionTest {

    @Test
    void invokeNull() {
        assertThrows(InvalidParameterException.class, () -> MatchesFunction.matchFunctionWithFlags(null, null, null));
        assertThrows(InvalidParameterException.class, () -> MatchesFunction.matchFunctionWithFlags(null, "test",null));
        assertThrows(InvalidParameterException.class, () -> MatchesFunction.matchFunctionWithFlags("test", null,null));
    }

    @Test
    void invokeUnsupportedFlags() {
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("foobar", "fo.bar", "g"));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "p"));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "X"));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", " "));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "iU"));
    }

    @Test
    void invokeWithoutFlagsMatch() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("test", "test",null), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*b",null), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", ""), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra",null), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("(?xi)[hello world()]", "hello",null), true);
    }

    @Test
    void invokeWithoutFlagsNotMatch() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("test", "testt",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*bb",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "fo.bar",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\3",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\2",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("input", "\3",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?iU)(?iU)(ab)[|cd]",null), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?x)(?i)hello world","i"), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?xi)hello world",null), false);
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
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("\u212A", "k","i"), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("\u212A", "K","i"), true);
    }

    @Test
    void invokeWithFlagComments() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("hello world", "hello"+"\"+ sworld", "x"), false);
    }

    @Test
    void invokeWithAllFlags() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "Fo.^bar", "smi"), true);
    }

    @Test
    void checkForPatternTest() {
        assertThrows(PatternSyntaxException.class, () -> MatchesFunction.matchFunctionWithFlags("foobar",  "(abc|def(ghi", "i"));
    }

   /* @Test
    void checkMatchFunctionWithFlagsInvocation() {
        MatchesFunction matchesFunctionSpied = spy(MatchesFunction.INSTANCE);
        matchesFunctionSpied.invoke("input", "pattern");
        verify(matchesFunctionSpied, times(1)).invoke("input", "pattern", null);
        try (MockedStatic<MatchesFunction> matchesFunctionMocked = mockStatic(MatchesFunction.class)) {
            matchesFunctionSpied.invoke("input", "pattern");
            matchesFunctionMocked.verify(() -> MatchesFunction.matchFunctionWithFlags("input", "pattern", null));
            matchesFunctionSpied.invoke("input", "pattern", "flags");
            matchesFunctionMocked.verify(() -> MatchesFunction.matchFunctionWithFlags("input", "pattern", "flags"));
        }
    }*/

}