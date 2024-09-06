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
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MatchesFunctionTest {

    @Test
    void invokeNull() {
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags(null, "test",null));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("test", null,null));
    }

    @Test
    void invokeUnsupportedFlags() {
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("foobar", "fo.bar", "g"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "X"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", " "), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "iU"), InvalidParametersEvent.class);
    }

    @Test
    void invokeWithoutFlagsMatch() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("test", "test",""), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*b",""), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", ""), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra",""), true);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("(?xi)[hello world()]", "hello",""), true);
    }

    @Test
    void invokeWithoutFlagsNotMatch() {
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("test", "testt",""), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*bb",""), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("fo\nbar", "fo.bar",""), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\3",""), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("h", "(.)\2",""), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("input", "\3",""), false);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?iU)(?iU)(ab)[|cd]",""), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?x)(?i)hello world","i"),  InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?xi)hello world",""), InvalidParametersEvent.class);
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
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("O", "[A-Z&&[^OI]]", "i"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("i", "[A-Z&&[^OI]]", "i"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("O", "[A-Z-[OI]]", "i"), false);
        FunctionTestUtil.assertResult(MatchesFunction.matchFunctionWithFlags("i", "[A-Z--[OI]]", "i"), false);
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
        FunctionTestUtil.assertResultError(MatchesFunction.matchFunctionWithFlags("foobar",  "(abc|def(ghi", "i"),InvalidParametersEvent.class);
    }
    @Test
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
    }
/*
    @Test
    void checkSaxonTest(){
        assertTrue(MatchesFunction.SaxonTest("TEST ", "test","ix"));
        assertTrue(MatchesFunction.SaxonTest("TEST ", "test","smi"));
        //assertTrue(MatchesFunction.SaxonTest("abracadabra", "bra", "p"));
        assertTrue(MatchesFunction.SaxonTest("\u212A", "k", "i"));
        //assertTrue(MatchesFunction.SaxonTest("input", "pattern", " "));
        // assertTrue(MatchesFunction.SaxonTest("input", "pattern", "X"));
        assertTrue(MatchesFunction.SaxonTest("x", "[A-Z-[OI]]", "i"));
        assertFalse(MatchesFunction.SaxonTest("i", "[A-Z-[OI]]", "i"));
        assertTrue(MatchesFunction.SaxonTest("hello world", "hello\\ sworld", "x"));
        assertFalse(MatchesFunction.SaxonTest("h", "(.)\3",""));
        assertFalse(MatchesFunction.SaxonTest(null, "test","ix"));
        assertFalse(MatchesFunction.SaxonTest("test", null,"ix"));
        assertTrue(MatchesFunction.SaxonTest("test", "test",""));
        assertTrue(MatchesFunction.SaxonTest("This is a characte","This is a characte",""));
        assertTrue(MatchesFunction.SaxonTest("\nabcd\ndefg\n", "^$", "m"));
        assertTrue(MatchesFunction.SaxonTest("abcd\n\ndefg\n ", "^$", "m"));
        assertFalse(MatchesFunction.SaxonTest("abracadabra", "(?:abra(?:cad)?)*", "q"));
        assertTrue(MatchesFunction.SaxonTest("x[y-z]", "x[y-z]", "q"));
        assertTrue(MatchesFunction.SaxonTest("x[Y-z]", "X[y-Z]", "qi"));
        assertFalse(MatchesFunction.SaxonTest("Mary\\u000DJones", "Mary.Jones",""));
        assertTrue(MatchesFunction.SaxonTest("abc", "ABC", "i"));
        assertTrue(MatchesFunction.SaxonTest("abZ", "[A-Z]*", "i"));
        assertTrue(MatchesFunction.SaxonTest("abZ", "[a-z]*", "i"));
        assertTrue(MatchesFunction.SaxonTest("X", "[A-Z-[OI]]", "i"));
        assertFalse(MatchesFunction.SaxonTest("O", "[A-Z-[OI]]", "i"));
        assertFalse(MatchesFunction.SaxonTest("Q", "[^Q]", "i"));
        assertFalse(MatchesFunction.SaxonTest( "q", "[^Q]", "i"));
        assertFalse(MatchesFunction.SaxonTest("input", "[]",""));
        assertFalse(MatchesFunction.SaxonTest("input", null,""));
        assertFalse(MatchesFunction.SaxonTest("input", "pattern","[]"));
        assertFalse(MatchesFunction.SaxonTest("input", "pattern", " "));
        assertTrue(MatchesFunction.SaxonTest("hello world", " hello[ ]world", "x"));
        assertTrue(MatchesFunction.SaxonTest("hello world", "he ll o[ ]worl d", "x"));
        assertTrue(MatchesFunction.SaxonTest("hello world", "\\p{ IsBasicLatin}+", "x"));
        assertTrue(MatchesFunction.SaxonTest("hello world", "\\p{ I s B a s i c L a t i n }+", "x"));
        assertFalse(MatchesFunction.SaxonTest("hello world", "\\p{ IsBasicLatin}+",""));
        assertFalse(MatchesFunction.SaxonTest("h", "(.)\3",""));
        assertFalse(MatchesFunction.SaxonTest("h", "(.)\2",""));
        assertFalse(MatchesFunction.SaxonTest("input", "\3",""));
        assertFalse(MatchesFunction.SaxonTest("abcd", "(asd)[\1]",""));
        assertFalse(MatchesFunction.SaxonTest( "abcd", "(asd)[asd\1]",""));
        assertFalse(MatchesFunction.SaxonTest("abcd", "(asd)[asd\0]",""));
        assertFalse(MatchesFunction.SaxonTest("abcd", "1[asd\0]",""));
        assertFalse(MatchesFunction.SaxonTest("a", "a[^b]",""));
        assertTrue(MatchesFunction.SaxonTest("a ", "a[^b]",""));
        assertFalse(MatchesFunction.SaxonTest("input", "[0-9-.]",""));
        assertTrue(MatchesFunction.SaxonTest("aA", "(a)\\1", "i")); */
    }