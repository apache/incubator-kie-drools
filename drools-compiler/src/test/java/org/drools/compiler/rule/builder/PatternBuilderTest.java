/*
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
package org.drools.compiler.rule.builder;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatternBuilderTest {

    @Test
    void ternaryOperator() {
        assertThat(PatternBuilder.containsTernaryOperator("\"foo\" == \"foo\" ? \"foo\" == flag1 : \"foo\" == flag2")).isTrue();
    }

    @Test
    void simpleTernary() {
        assertThat(PatternBuilder.containsTernaryOperator("x > 0 ? y : z")).isTrue();
    }

    @Test
    void noTernary() {
        assertThat(PatternBuilder.containsTernaryOperator("age > 10")).isFalse();
    }

    @Test
    void equalityExpression() {
        assertThat(PatternBuilder.containsTernaryOperator("length == 4")).isFalse();
    }

    @Test
    void nullSafeOperator() {
        assertThat(PatternBuilder.containsTernaryOperator("address?.city == \"London\"")).isFalse();
    }

    @Test
    void questionMarkInsideStringLiteral() {
        assertThat(PatternBuilder.containsTernaryOperator("name == \"what?\"")).isFalse();
    }

    @Test
    void escapedQuoteBeforeTernary() {
        assertThat(PatternBuilder.containsTernaryOperator("\"val\\\"ue\" == flag ? x : y")).isTrue();
    }

    @Test
    void questionMarkInsideStringWithEscapedQuote() {
        assertThat(PatternBuilder.containsTernaryOperator("name == \"is\\\"this?real\"")).isFalse();
    }

    @Test
    void singleQuotedStringWithQuestionMark() {
        assertThat(PatternBuilder.containsTernaryOperator("name == 'what?'")).isFalse();
    }

    @Test
    void ternaryAtEndOfExpression() {
        assertThat(PatternBuilder.containsTernaryOperator("x?")).isTrue();
    }

    @Test
    void emptyExpression() {
        assertThat(PatternBuilder.containsTernaryOperator("")).isFalse();
    }

    @Test
    void complexTernaryWithLogicalOperators() {
        assertThat(PatternBuilder.containsTernaryOperator("a > 0 && b < 10 ? c : d")).isTrue();
    }

    @Test
    void nullSafeChain() {
        assertThat(PatternBuilder.containsTernaryOperator("person?.address?.city == \"London\"")).isFalse();
    }
}
