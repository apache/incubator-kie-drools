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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MatchesFunctionTest {

    @Test
    void invokeNull() {
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags(null, "test", null));
        assertThrows(IllegalArgumentException.class, () -> MatchesFunction.matchFunctionWithFlags("test", null, null));
    }

    @Test
    void invokeUnsupportedFlags() {
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "fo.bar", "g") == null);
        assertThat(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "X") == null);
        assertThat(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", " ") == null);
        assertThat(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "iU") == null);
    }

    @Test
    void invokeWithoutFlagsMatch() {
        assertThat(MatchesFunction.matchFunctionWithFlags("test", "test", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*b", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("abracadabra", "bra", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("(?xi)[hello world()]", "hello", "") == null);
    }

    @Test
    void invokeWithoutFlagsNotMatch() {
        assertThat(MatchesFunction.matchFunctionWithFlags("test", "testt", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "^fo*bb", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "fo.bar", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("h", "(.)\3", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("h", "(.)\2", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("input", "\3", "").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?iU)(?iU)(ab)[|cd]", "").equals(false));
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?x)(?i)hello world", "i").equals(false));
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "(?xi)hello world", "").equals(false));
    }

    @Test
    void invokeWithFlagDotAll() {
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "fo.bar", "s").equals(true));
    }

    @Test
    void invokeWithFlagMultiline() {
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "^bar", "m").equals(true));
    }

    @Test
    void invokeWithFlagCaseInsensitive() {
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "^Fo*bar", "i").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "^Fo*bar", "i").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("\u212A", "k", "i").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("\u212A", "K", "i").equals(true));
        assertThat(MatchesFunction.matchFunctionWithFlags("O", "[A-Z&&[^OI]]", "i") == null);
        assertThat(MatchesFunction.matchFunctionWithFlags("i", "[A-Z&&[^OI]]", "i") == null);
        assertThat(MatchesFunction.matchFunctionWithFlags("O", "[A-Z-[OI]]", "i").equals(false));
        assertThat(MatchesFunction.matchFunctionWithFlags("i", "[A-Z--[OI]]", "i").equals(false));
    }

    @Test
    void invokeWithFlagComments() {
        assertThat(MatchesFunction.matchFunctionWithFlags("hello world", "hello" + "\"+ sworld", "x").equals(false));
    }

    @Test
    void invokeWithAllFlags() {
        assertThat(MatchesFunction.matchFunctionWithFlags("fo\nbar", "Fo.^bar", "smi").equals(true));
    }

    @Test
    void checkForPatternTest() {
        assertThat(MatchesFunction.matchFunctionWithFlags("foobar", "(abc|def(ghi", "i") == null);
    }

    @Test
    void checkMatchFunctionWithFlagsInvocation() {
        MatchesFunction matchesFunctionSpied = spy(MatchesFunction.INSTANCE);
        matchesFunctionSpied.invoke("input", "pattern", "flags");
        verify(matchesFunctionSpied, times(1)).invoke("input", "pattern", "flags");
        try (MockedStatic<MatchesFunction> matchesFunctionMocked = mockStatic(MatchesFunction.class)) {
            matchesFunctionSpied.invoke("input", "pattern","flags");
            matchesFunctionMocked.verify(() -> MatchesFunction.matchFunctionWithFlags("input", "pattern", "flags"));
        }
    }
}