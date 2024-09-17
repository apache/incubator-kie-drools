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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class MatchesFunctionTest {

    private static final MatchesFunction matchesFunction = MatchesFunction.INSTANCE;

    @ParameterizedTest
    @MethodSource("invokeNullTestData")
    void invokeNullTest(String input, String pattern, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(matchesFunction.invoke(input, pattern), expectedErrorEventClass);
    }

    private static Object[][] invokeNullTestData() {
        return new Object[][] {
                { null, null, InvalidParametersEvent.class },
                { null, "test", InvalidParametersEvent.class },
                { "test", null, InvalidParametersEvent.class },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeUnsupportedFlagsTestData")
    void invokeUnsupportedFlagsTest(String input, String pattern, String flags, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(matchesFunction.invoke(input, pattern, flags), expectedErrorEventClass);
   }

    private static Object[][] invokeUnsupportedFlagsTestData() {
        return new Object[][] {
                { "foobar", "fo.bar", "g", InvalidParametersEvent.class },
                { "abracadabra", "bra", "p", InvalidParametersEvent.class },
                { "abracadabra", "bra", "X", InvalidParametersEvent.class },
                { "abracadabra", "bra", "iU", InvalidParametersEvent.class },
                { "abracadabra", "bra", "iU asd", InvalidParametersEvent.class },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeWithoutFlagsMatchTestData")
    void invokeWithoutFlagsMatchTest(String input, String pattern, Boolean expectedResult) {
        FunctionTestUtil.assertResult(matchesFunction.invoke(input, pattern), expectedResult);
    }

    private static Object[][] invokeWithoutFlagsMatchTestData() {
        return new Object[][] {
                { "test", "test", true },
                { "foobar", "^fo*b", true },
                { "abracadabra", "bra", true },
                { "abracadabra", "bra", true },
                { "(?xi)[hello world()]", "hello", true }
        };
    }

    @ParameterizedTest
    @MethodSource("invokeNullOrEmptyFlagsMatchTestData")
    void invokeNullOrEmptyFlagsMatchTest(String input, String pattern, String flags, Boolean expectedResult) {
        FunctionTestUtil.assertResult(matchesFunction.invoke(input, pattern, flags), expectedResult);
    }

    private static Object[][] invokeNullOrEmptyFlagsMatchTestData() {
        return new Object[][] {
                { "test", "test", null, true },
                { "foobar", "^fo*b", null, true },
                { "abracadabra", "bra", null, true },
                { "abracadabra", "bra", "", true },
                { "(?xi)[hello world()]", "hello", null, true }
        };
    }

    @ParameterizedTest
    @MethodSource("invokeInvalidRegexTestData")
    void invokeInvalidRegexTest(String input, String pattern, String flags, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(matchesFunction.invoke(input, pattern, flags), expectedErrorEventClass);
    }

    private static Object[][] invokeInvalidRegexTestData() {
        return new Object[][] {
                { "testString", "(?=\\\\s)", null, InvalidParametersEvent.class },
                { "fo\nbar", "(?iU)(?iU)(ab)[|cd]", null, InvalidParametersEvent.class },
                { "fo\nbar", "(?x)(?i)hello world", "i", InvalidParametersEvent.class },
                { "fo\nbar", "(?xi)hello world", null, InvalidParametersEvent.class },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeWithoutFlagsNotMatchTestData")
    void invokeWithoutFlagsNotMatchTest(String input, String pattern, String flags, Boolean expectedResult) {
        FunctionTestUtil.assertResult(matchesFunction.invoke(input, pattern, flags), expectedResult);
    }

    private static Object[][] invokeWithoutFlagsNotMatchTestData() {
        return new Object[][] {
                { "test", "testt", null, false },
                { "foobar", "^fo*bb", null, false },
                { "h", "(.)\3", null, false },
                { "h", "(.)\2", null, false },
                { "input", "\3", null, false }
        };
    }

    @ParameterizedTest
    @MethodSource("invokeWithFlagCaseInsensitiveTestData")
    void invokeWithoutFlagsPatternTest(String input, String pattern, String flags, Boolean expectedResult) {
        FunctionTestUtil.assertResult(matchesFunction.invoke(input, pattern, flags), expectedResult);
    }

    private static Object[][] invokeWithFlagCaseInsensitiveTestData() {
        return new Object[][] {
                { "foobar", "^Fo*bar", "i", true },
                { "foobar", "^Fo*bar", "i", true },
                { "\u212A", "k", "i", true },
                { "\u212A", "K", "i", true }
        };
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