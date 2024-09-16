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

class XQueryImplUtilTest {

    @Test
    void executeMatchesFunctionTest() {
        Boolean retrieved = XQueryImplUtil.executeMatchesFunction("test", "^test", "i");
        assertThat(retrieved).isTrue();

        retrieved = XQueryImplUtil.executeMatchesFunction("fo\nbar", "o.b", null);
        assertThat(retrieved).isFalse();

        retrieved = XQueryImplUtil.executeMatchesFunction("TEST", "test", "i");
        assertThat(retrieved).isTrue();
    }

    @Test
    void executeMatchesFunctionInvokingException(){
        assertThatThrownBy(() -> XQueryImplUtil.executeMatchesFunction("test", "^test", "g"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unrecognized flag");

        assertThatThrownBy(() -> XQueryImplUtil.executeMatchesFunction("test", "(?=\\s)", null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No expression before quantifier");

        assertThatThrownBy(() -> XQueryImplUtil.executeMatchesFunction("test", "(.)\\2", null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("invalid backreference \\2");
    }

    @Test
    void executeReplaceFunctionTest() {
        String retrieved = XQueryImplUtil.executeReplaceFunction("testString", "^test", "ttt", "");
        assertThat(retrieved).isNotNull().isEqualTo("tttString");

        retrieved = XQueryImplUtil.executeReplaceFunction("fo\nbar", "o.b", "ttt", "s");
        assertThat(retrieved).isNotNull().isEqualTo("ftttar");
    }

    @Test
    void executeReplaceFunctionInvokingException(){
        assertThatThrownBy(() -> XQueryImplUtil.executeReplaceFunction("fo\nbar", "o.b", "ttt", "g"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unrecognized flag");

        assertThatThrownBy(() -> XQueryImplUtil.executeReplaceFunction("test", "(?=\\s)", "ttt", null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No expression before quantifier");

        assertThatThrownBy(() -> XQueryImplUtil.executeReplaceFunction("test", "(.)\\2", "ttt", null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("invalid backreference \\2");
    }

}