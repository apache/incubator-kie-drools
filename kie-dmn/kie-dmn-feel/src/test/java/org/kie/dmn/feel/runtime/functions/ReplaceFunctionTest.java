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
    void invokeNullTest(String input, String pattern, String replacement) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement), InvalidParametersEvent.class);
    }

    private static Object[][] invokeNullTestData() {
        return new Object[][] {
                { null, null, null },
                { "testString", null, null },
                { "testString", "test", null },
                { null, "test", null },
                { null, "test", "ttt" },
                { null, null, "ttt" }
        };
    }

    @ParameterizedTest
    @MethodSource("invokeNullWithFlagsTestData")
    void invokeNullWithFlagsTest(String input, String pattern, String replacement, String flags) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement, flags), InvalidParametersEvent.class);
    }

    private static Object[][] invokeNullWithFlagsTestData() {
        return new Object[][] {
                { null, null, null, null },
                { "testString", null, null, null },
                { "testString", "test", null, null },
                { null, "test", null, null },
                { null, "test", "ttt", null },
                { null, null, "ttt", null },
                { null, null, null, "s" },
                { "testString", null, null, "s" },
                { "testString", "test", null, "s"  },
                { null, "test", null, "s" },
                { null, "test", "ttt", "s" },
                { null, null, "ttt", "s" },
        };
    }

    @ParameterizedTest
    @MethodSource("invokeUnsupportedFlagsTestData")
    void invokeUnsupportedFlagsTest(String input, String pattern, String replacement, String flags) {
        FunctionTestUtil.assertResultError(replaceFunction.invoke(input, pattern, replacement, flags), InvalidParametersEvent.class);
  }

    private static Object[][] invokeUnsupportedFlagsTestData() {
        return new Object[][] {
                { "testString", "^test", "ttt", "g" },
                { "testString", "^test", "ttt", "p" },
                { "testString", "^test", "ttt", "X" },
                { "testString", "^test", "ttt", "iU" },
                { "testString", "^test", "ttt", "iU asd" },
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

    @ParameterizedTest
    @MethodSource("invokeWithXmlCharacterReferencesData")
    void invokeWithXmlCharacterReferencesTest(String input, String pattern, String replacement, String expectedResult) {
        FunctionTestUtil.assertResult(replaceFunction.invoke(input, pattern, replacement), expectedResult);
    }

    private static Object[][] invokeWithXmlCharacterReferencesData() {
        return new Object[][] {
                { "abc&123", "abc", "123", "123&123" },
                { "abc'123", "abc'", "123", "123123" },
                { "abc\"123", "abc\"", "123<>", "123<>123" }
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