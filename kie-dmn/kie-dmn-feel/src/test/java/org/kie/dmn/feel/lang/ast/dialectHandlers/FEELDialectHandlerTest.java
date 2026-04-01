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
package org.kie.dmn.feel.lang.ast.dialectHandlers;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FEELDialectHandler - FEEL returns null for invalid operations
 */
class FEELDialectHandlerTest {

    private FEELDialectHandler handler;
    private EvaluationContext ctx;

    @BeforeEach
    void setUp() {
        handler = new FEELDialectHandler();
        ctx = EvaluationContextTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void testAddOperations() {
        // Valid operations
        assertThat(handler.executeAdd("Hello", "World", ctx)).isEqualTo("HelloWorld");
        assertThat(handler.executeAdd(10, 20, ctx)).isEqualTo(new BigDecimal("30"));
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 6));
        
        // Invalid operations return null
        assertThat(handler.executeAdd("Hello", 123, ctx)).isNull();
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), 5, ctx)).isNull();
        assertThat(handler.executeAdd(10, null, ctx)).isNull();
    }

    @Test
    void testLogicalOperations() {
        // AND operations
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.TRUE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeAnd(Boolean.TRUE, null, ctx)).isNull();
        
        // OR operations
        assertThat(handler.executeOr(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeOr(Boolean.FALSE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeOr(Boolean.FALSE, null, ctx)).isNull();
    }

    @Test
    void testEqualityOperations() {
        // Equal
        assertThat(handler.executeEqual(null, null, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 20, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeEqual("10", 10, ctx)).isNull(); // Different types
        
        // Not Equal
        assertThat(handler.executeNotEqual(null, null, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeNotEqual(10, 20, ctx)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void testComparisonOperations() {
        // Greater than or equal
        assertThat(handler.executeGte(Boolean.TRUE, null, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeGte(Boolean.FALSE, null, ctx)).isNull();
        assertThat(handler.executeGte(20, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeGte(10, 10, ctx)).isEqualTo(Boolean.TRUE);
        
        // Greater than
        assertThat(handler.executeGt(20, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeGt(10, 10, ctx)).isEqualTo(Boolean.FALSE);
        
        // Less than or equal
        assertThat(handler.executeLte(5, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeLte(10, 10, ctx)).isEqualTo(Boolean.TRUE);
        
        // Less than
        assertThat(handler.executeLt(5, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeLt(10, 10, ctx)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void testArithmeticOperations() {
        // Power
        assertThat(handler.executePow("2", 3, ctx)).isNull(); // Invalid
        assertThat(handler.executePow(2, 3, ctx)).isEqualTo(new BigDecimal("8"));
        
        // Subtraction
        assertThat(handler.executeSub("10", 5, ctx)).isNull(); // Invalid
        assertThat(handler.executeSub(20, 10, ctx)).isEqualTo(new BigDecimal("10"));
        assertThat(handler.executeSub(LocalDate.of(2024, 1, 10), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 5));
        
        // Multiplication
        assertThat(handler.executeMult("test", 5, ctx)).isNull(); // Invalid
        assertThat(handler.executeMult(5, 10, ctx)).isEqualTo(new BigDecimal("50"));
        assertThat(handler.executeMult(Duration.ofDays(5), null, ctx)).isNull();
        assertThat(handler.executeMult(Duration.ofDays(5), Duration.ofDays(3), ctx)).isNull();
        assertThat(handler.executeMult(LocalDate.of(2024, 1, 1), 5, ctx)).isNull();
        
        // Division
        assertThat(handler.executeDivision("test", 5, ctx)).isNull(); // Invalid
        assertThat(handler.executeDivision(20, 4, ctx)).isEqualTo(new BigDecimal("5"));
        assertThat(handler.executeDivision(10, Duration.ofDays(5), ctx)).isNull();
        assertThat(handler.executeDivision(Duration.ofDays(5), null, ctx)).isNull();
        assertThat(handler.executeDivision(null, 5, ctx)).isNull();
    }

    @Test
    void testCompareMethod() {
        assertThat(handler.compare(null, null, (l, r) -> l.compareTo(r) > 0)).isNull();
        assertThat(handler.compare(20, 10, (l, r) -> l.compareTo(r) > 0)).isTrue();
        assertThat(handler.compare("test", "test", (l, r) -> l.compareTo(r) == 0)).isTrue();
    }
}
