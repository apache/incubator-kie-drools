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
package org.drools.drl.parser.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserStringUtilsTest {

    @Test
    public void appendPrefix() {
        String prefix = "prefix.";
        String expr = "a == 1";
        String result = ParserStringUtils.appendPrefix(prefix, expr);
        assertThat(result).isEqualToIgnoringWhitespace("prefix.a == 1");
    }

    @Test
    public void appendPrefixWithAnd() {
        String prefix = "prefix.";
        String expr = "a == 1 && b.equals(\"foo\")";
        String result = ParserStringUtils.appendPrefix(prefix, expr);
        assertThat(result).isEqualToIgnoringWhitespace("prefix.a == 1 && prefix.b.equals(\"foo\")");
    }

    @Test
    public void appendPrefixWithOr() {
        String prefix = "prefix.";
        String expr = "a == 1 || b.equals(\"foo\")";
        String result = ParserStringUtils.appendPrefix(prefix, expr);
        assertThat(result).isEqualToIgnoringWhitespace("prefix.a == 1 || prefix.b.equals(\"foo\")");
    }

    @Test
    public void appendPrefixWithANDOr() {
        String prefix = "prefix.";
        String expr = "a == 1 && b.equals(\"foo\") || c == 2 && d.equals(\"bar\")";
        String result = ParserStringUtils.appendPrefix(prefix, expr);
        assertThat(result).isEqualToIgnoringWhitespace("prefix.a == 1 && prefix.b.equals(\"foo\") || prefix.c == 2 && prefix.d.equals(\"bar\")");
    }

    @Test
    public void appendPrefixWithBindingVariable() {
        String prefix = "prefix.";
        String expr = "$a : a == 1 && $b : b.equals(\"foo\")";
        String result = ParserStringUtils.appendPrefix(prefix, expr);
        assertThat(result).isEqualToIgnoringWhitespace("$a : prefix.a == 1 && $b : prefix.b.equals(\"foo\")");
    }
}
