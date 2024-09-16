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

class ReplaceFunctionTest {

    private static final ReplaceFunction replaceFunction = ReplaceFunction.INSTANCE;

    @ParameterizedTest
    @MethodSource("invokeNullTestData")
    void invokeNullTest(String input, String pattern, String replacement, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement), expectedErrorEventClass);
    }

    private static Object[][] invokeNullTestData() {
        return new Object[][] {
                { null, null, null, InvalidParametersEvent.class },
                { "testString", null, null, InvalidParametersEvent.class },
                { "testString", "test", null, InvalidParametersEvent.class },
                { null, "test", null, InvalidParametersEvent.class },
                { null, "test", "ttt", InvalidParametersEvent.class },
                { null, null, "ttt", InvalidParametersEvent.class }
        };
    }

    @ParameterizedTest
    @MethodSource("invokeNullWithFlagsTestData")
    void invokeNullWithFlagsTest(String input, String pattern, String replacement, String flags, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement, flags), expectedErrorEventClass);
    }

    private static Object[][] invokeNullWithFlagsTestData() {
        return new Object[][] {
                { null, null, null, null, InvalidParametersEvent.class },
                { "testString", null, null, null, InvalidParametersEvent.class },
                { "testString", "test", null, null, InvalidParametersEvent.class },
                { null, "test", null, null, InvalidParametersEvent.class },
                { null, "test", "ttt", null, InvalidParametersEvent.class },
                { null, null, "ttt", null, InvalidParametersEvent.class },
                { null, null, null, "s", InvalidParametersEvent.class },
                { "testString", null, null, "s", InvalidParametersEvent.class },
                { "testString", "test", null, "s", InvalidParametersEvent.class },
                { null, "test", null, "s", InvalidParametersEvent.class },
                { null, "test", "ttt", "s", InvalidParametersEvent.class },
                { null, null, "ttt", "s", InvalidParametersEvent.class },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeUnsupportedFlagsTestData")
    void invokeUnsupportedFlagsTest(String input, String pattern, String replacement, String flags, Class<?> expectedErrorEventClass) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement, flags), expectedErrorEventClass);
  }

    private static Object[][] invokeUnsupportedFlagsTestData() {
        return new Object[][] {
                { "testString", "^test", "ttt", "g", InvalidParametersEvent.class },
                { "testString", "^test", "ttt", "p", InvalidParametersEvent.class },
                { "testString", "^test", "ttt", "X", InvalidParametersEvent.class },
                { "testString", "^test", "ttt", "iU", InvalidParametersEvent.class },
                { "testString", "^test", "ttt", "iU asd", InvalidParametersEvent.class },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeWithoutFlagsPatternTestData")
    void invokeWithoutFlagsPatternTest(String input, String pattern, String replacement, String expectedResult) {
        FunctionTestUtil.assertResult(replaceFunction.invoke(input, pattern, replacement), expectedResult);
    }

    private static Object[][] invokeWithoutFlagsPatternTestData() {
        return new Object[][] {
                { "testString", "^test", "ttt", "tttString" },
                { "testStringtest", "^test", "ttt", "tttStringtest" },
                { "testString", "ttest", "ttt", "testString" },
                { "testString", "$test", "ttt", "testString" }
        };
    }

    @Test
    void invokeInvalidRegExPattern() {
        FunctionTestUtil.assertResultError(replaceFunction.invoke("testString", "(?=\\s)", "ttt"), InvalidParametersEvent.class);
    }

    @Test
    void invokeWithFlagDotAll() {
        FunctionTestUtil.assertResult(replaceFunction.invoke("fo\nbar", "o.b", "ttt", "s"), "ftttar");
    }

    @Test
    void invokeWithFlagMultiline() {
        FunctionTestUtil.assertResult(replaceFunction.invoke("foo\nbar", "^b", "ttt", "m"), "foo\ntttar");
    }

    @Test
    void invokeWithFlagCaseInsensitive() {
        FunctionTestUtil.assertResult(replaceFunction.invoke("foobar", "^fOO", "ttt", "i"), "tttbar");
    }

    @Test
    void invokeWithAllFlags() {
        FunctionTestUtil.assertResult(replaceFunction.invoke("fo\nbar", "O.^b", "ttt", "smi"), "ftttar");
    }
}