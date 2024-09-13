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
package org.kie.dmn.feel.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XQueryImplUtilTest {

    @Test
    void executeMatchesFunctionTest() {
        String input = "test";
        String pattern = "^test";
        String flags = "i";
        Object retrieved = XQueryImplUtil.executeMatchesFunction(input, pattern,
                flags);
        boolean expected = true;
        assertThat(retrieved).isNotNull().isEqualTo(expected);

        input = "fo\nbar";
        pattern = "o.b";
        flags = null;
        retrieved = XQueryImplUtil.executeMatchesFunction(input, pattern, flags);
        expected = false;
        assertThat(retrieved).isNotNull().isEqualTo(expected);

        input = "TEST";
        pattern = "test";
        flags = "i";
        retrieved = XQueryImplUtil.executeMatchesFunction(input, pattern, flags);
        expected = true;
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

    @Test
    void executeMatchesFunctionInvokingException(){
        String input = "test";
        String pattern = "^test";
        String flags = "g";
        assertThatThrownBy(() -> XQueryImplUtil.executeMatchesFunction(input, pattern,
                flags)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Unrecognized flag");
    }

    @Test
    void executeReplaceFunctionTest() {
        String input = "testString";
        String pattern = "^test";
        String replacement = "ttt";
        String flags = "";
        Object retrieved = XQueryImplUtil.executeReplaceFunction(input, pattern, replacement,
                flags);
        String expected = "tttString";
        assertThat(retrieved).isNotNull().isEqualTo(expected);

        input = "fo\nbar";
        pattern = "o.b";
        replacement = "ttt";
        flags = "s";
        retrieved = XQueryImplUtil.executeReplaceFunction(input, pattern, replacement, flags);
        expected = "ftttar";
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

}