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
import static org.kie.dmn.feel.util.StringEvalHelper.normalizeVariableName;

class StringEvalHelperTest {

    @Test
    void normalizeSpace() {
        assertThat(normalizeVariableName(null)).isNull();
        assertThat(normalizeVariableName("")).isEqualTo("");
        assertThat(normalizeVariableName(" ")).isEqualTo("");
        assertThat(normalizeVariableName("\t")).isEqualTo("");
        assertThat(normalizeVariableName("\n")).isEqualTo("");
        assertThat(normalizeVariableName("\u0009")).isEqualTo("");
        assertThat(normalizeVariableName("\u000B")).isEqualTo("");
        assertThat(normalizeVariableName("\u000C")).isEqualTo("");
        assertThat(normalizeVariableName("\u001C")).isEqualTo("");
        assertThat(normalizeVariableName("\u001D")).isEqualTo("");
        assertThat(normalizeVariableName("\u001E")).isEqualTo("");
        assertThat(normalizeVariableName("\u001F")).isEqualTo("");
        assertThat(normalizeVariableName("\f")).isEqualTo("");
        assertThat(normalizeVariableName("\r")).isEqualTo("");
        assertThat(normalizeVariableName("  a  ")).isEqualTo("a");
        assertThat(normalizeVariableName("  a  b   c  ")).isEqualTo("a b c");
        assertThat(normalizeVariableName("a\t\f\r  b\u000B   c\n")).isEqualTo("a b c");
        assertThat(normalizeVariableName("a\t\f\r  \u00A0\u00A0b\u000B   c\n")).isEqualTo("a b c");
        assertThat(normalizeVariableName(" b")).isEqualTo("b");
        assertThat(normalizeVariableName("b ")).isEqualTo("b");
        assertThat(normalizeVariableName("ab c  ")).isEqualTo("ab c");
        assertThat(normalizeVariableName("a\u00A0b")).isEqualTo("a b");
    }

}
