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

import static org.assertj.core.api.Assertions.assertThat;

class ReplaceFunctionTest {

    private static final ReplaceFunction replaceFunction = ReplaceFunction.INSTANCE;

    @Test
    void invokeNull() {
        assertThat(replaceFunction.invoke(null, null, null) == null);
        assertThat(replaceFunction.invoke("testString", null, null) == null);
        assertThat(replaceFunction.invoke("testString", "test", null) == null);
        assertThat(replaceFunction.invoke(null, "test", null) == null);
        assertThat(replaceFunction.invoke(null, "test", "ttt") == null);
        assertThat(replaceFunction.invoke(null, null, "ttt") == null);
    }

    @Test
    void invokeNullWithFlags() {
        assertThat(replaceFunction.invoke(null, null, null, null) == null);
        assertThat(replaceFunction.invoke("testString", null, null, null) == null);
        assertThat(replaceFunction.invoke("testString", "test", null, null) == null);
        assertThat(replaceFunction.invoke(null, "test", null, null) == null);
        assertThat(replaceFunction.invoke(null, "test", "ttt", null) == null);
        assertThat(replaceFunction.invoke(null, null, "ttt", null) == null);
        assertThat(replaceFunction.invoke(null, null, null, "s") == null);
        assertThat(replaceFunction.invoke("testString", null, null, "s") == null);
        assertThat(replaceFunction.invoke("testString", "test", null, "s") == null);
        assertThat(replaceFunction.invoke(null, "test", null, "s") == null);
        assertThat(replaceFunction.invoke(null, "test", "ttt", "s") == null);
        assertThat(replaceFunction.invoke(null, null, "ttt", "s") == null);
    }

    @Test
    void invokeWithoutFlagsPatternMatches() {
        assertThat(replaceFunction.invoke("testString", "^test", "ttt").equals("tttString"));
        assertThat(replaceFunction.invoke("testStringtest", "^test", "ttt").equals("tttStringtest"));
    }

    @Test
    void invokeWithoutFlagsPatternNotMatches() {
        assertThat(replaceFunction.invoke("testString", "ttest", "ttt").equals("testString"));
        assertThat(replaceFunction.invoke("testString", "$test", "ttt").equals("testString"));
    }

    @Test
    void invokeWithFlagDotAll() {
        assertThat(replaceFunction.invoke("fo\nbar", "o.b", "ttt", "s").equals("ftttar"));
    }

    @Test
    void invokeWithFlagMultiline() {
        assertThat(replaceFunction.invoke("foo\nbar", "^b", "ttt", "m").equals("foo\ntttar"));
    }

    @Test
    void invokeWithFlagCaseInsensitive() {
        assertThat(replaceFunction.invoke("foobar", "^fOO", "ttt", "i").equals("tttbar"));
    }

    @Test
    void invokeWithAllFlags() {
        assertThat(replaceFunction.invoke("fo\nbar", "O.^b", "ttt", "smi").equals("ftttar"));
        assertThat(replaceFunction.invoke("banana","a","o").equals("bonono"));
        assertThat(replaceFunction.invoke("abcd","(ab)|(a)","[1=$1][2=$2]").equals("\"[1=ab][2=]cd\""));
    }
}