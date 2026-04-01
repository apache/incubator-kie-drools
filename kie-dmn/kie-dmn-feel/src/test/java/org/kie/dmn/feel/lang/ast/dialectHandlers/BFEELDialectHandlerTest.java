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
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BFEELDialectHandler - BFEEL uses type coercion and returns default values
 */
class BFEELDialectHandlerTest {

    private BFEELDialectHandler handler;
    private EvaluationContext ctx;

    @BeforeEach
    void setUp() {
        handler = new BFEELDialectHandler();
        ctx = EvaluationContextTestUtil.newEmptyEvaluationContext();
    }

    @Test
    void testAddOperations() {
        // String concatenation with type coercion
        assertThat(handler.executeAdd("Hello", "World", ctx)).isEqualTo("HelloWorld");
        assertThat(handler.executeAdd("Hello", 123, ctx)).isEqualTo("Hello123");
        assertThat(handler.executeAdd("Hello", null, ctx)).isEqualTo("Hello");
        
        // Numbers
        assertThat(handler.executeAdd(10, 20, ctx)).isEqualTo(new BigDecimal("30"));
        assertThat(handler.executeAdd(10, null, ctx)).isEqualTo(10);
        
        // Date operations - returns number instead of null
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), 5, ctx)).isEqualTo(5);
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 6));
    }

    @Test
    void testLogicalOperations() {
        // AND with boolean coercion
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.TRUE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeAnd("test", Boolean.TRUE, ctx)).isEqualTo(Boolean.FALSE); // Non-boolean coerced to false
        
        // OR operations
        assertThat(handler.executeOr(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeOr(Boolean.FALSE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void testEqualityOperations() {
        // Equal - returns false for different types (not null)
        assertThat(handler.executeEqual(null, null, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 20, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeEqual("10", 10, ctx)).isEqualTo(Boolean.FALSE); // Different types
        assertThat(handler.executeEqual(null, 10, ctx)).isEqualTo(Boolean.FALSE);
        
        // Not Equal
        assertThat(handler.executeNotEqual(null, null, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeNotEqual(10, 20, ctx)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void testComparisonOperations() {
        // BFEEL returns false for invalid comparisons (not null)
        assertThat(handler.executeGte(Boolean.FALSE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeGte("test", Boolean.TRUE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeGte(20, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeGte("test", 10, ctx)).isEqualTo(Boolean.FALSE); // Incomparable
        
        assertThat(handler.executeGt(20, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeLte(5, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeLt(5, 10, ctx)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void testArithmeticOperations() {
        // Power - returns false for invalid
        assertThat(handler.executePow("2", 3, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executePow(2, 3, ctx)).isEqualTo(new BigDecimal("8"));
        
        // Subtraction - returns empty string for invalid
        assertThat(handler.executeSub("10", 5, ctx)).isEqualTo("");
        assertThat(handler.executeSub(20, 10, ctx)).isEqualTo(new BigDecimal("10"));
        assertThat(handler.executeSub(LocalDate.of(2024, 1, 10), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 5));
        
        // Multiplication - returns zero for invalid
        assertThat(handler.executeMult("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeMult(5, 10, ctx)).isEqualTo(new BigDecimal("50"));
        assertThat(handler.executeMult(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeMult(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        assertThat(handler.executeMult(Duration.ofDays(5), Duration.ofDays(3), ctx)).isNull();
        
        // Division - returns zero for invalid, null for division by zero
        assertThat(handler.executeDivision("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(20, 4, ctx)).isEqualTo(new BigDecimal("5"));
        assertThat(handler.executeDivision(10, Duration.ofDays(5), ctx)).isNull();
        assertThat(handler.executeDivision(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeDivision(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        assertThat(handler.executeDivision(null, 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(10, 0, ctx)).isNull(); // Division by zero
    }

    @Test
    void testCompareMethod() {
        // Returns false for null or incomparable types
        assertThat(handler.compare(null, null, (l, r) -> l.compareTo(r) > 0)).isEqualTo(Boolean.FALSE);
        assertThat(handler.compare(20, 10, (l, r) -> l.compareTo(r) > 0)).isTrue();
        assertThat(handler.compare("test", "test", (l, r) -> l.compareTo(r) == 0)).isTrue();
        assertThat(handler.compare("test", 10, (l, r) -> l.compareTo(r) > 0)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void testBFEELSpecificBehavior() {
        // Type coercion in logical operations
        assertThat(handler.executeAnd("notBoolean", "alsoNotBoolean", ctx)).isEqualTo(Boolean.FALSE);
        
        // Comparison with non-comparable types
        assertThat(handler.executeGt(new Object(), new Object(), ctx)).isEqualTo(Boolean.FALSE);
    }
}

