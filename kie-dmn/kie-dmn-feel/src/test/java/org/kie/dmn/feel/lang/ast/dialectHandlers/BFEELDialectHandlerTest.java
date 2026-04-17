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
        // Power - B-FEEL implicit coercion: string "2" can be coerced to number 2
        assertThat(handler.executePow("2", 3, ctx)).isEqualTo(new BigDecimal("8")); // "2" coerced to 2, then 2^3 = 8
        assertThat(handler.executePow(2, 3, ctx)).isEqualTo(new BigDecimal("8"));
        assertThat(handler.executePow(2, "3", ctx)).isEqualTo(new BigDecimal("8")); // "3" coerced to 3
        assertThat(handler.executePow(2, "invalid", ctx)).isEqualTo(BigDecimal.ZERO); // Invalid string returns 0
        assertThat(handler.executePow(null, 3, ctx)).isEqualTo(BigDecimal.ZERO); // Null returns 0
        assertThat(handler.executePow(2, null, ctx)).isEqualTo(BigDecimal.ZERO); // Null returns 0
        
        // Subtraction - returns empty string for invalid
        assertThat(handler.executeSub("10", 5, ctx)).isEqualTo("");
        assertThat(handler.executeSub(20, 10, ctx)).isEqualTo(new BigDecimal("10"));
        assertThat(handler.executeSub(LocalDate.of(2024, 1, 10), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 5));
        
        // Multiplication - B-FEEL returns default values (never null)
        assertThat(handler.executeMult("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeMult(5, 10, ctx)).isEqualTo(new BigDecimal("50"));
        assertThat(handler.executeMult(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeMult(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        
        // Case A: Same Duration Types - Duration × Duration (same type) → zero duration (B-FEEL default)
        assertThat(handler.executeMult(Duration.ofDays(5), Duration.ofDays(3), ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeMult(Period.ofMonths(6), Period.ofMonths(2), ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        
        // Case B: Mixed Duration Types - B-FEEL implicit coercion
        // Period × Duration: Duration converted to seconds (PT1S = 1 second), result is Period
        // P1Y (12 months) × PT1S (1 second) = P12M
        assertThat(handler.executeMult(Period.ofYears(1), Duration.ofSeconds(1), ctx)).isEqualTo(ComparablePeriod.ofMonths(12));
        
        // Duration × Period: Period converted to months (P1Y = 12 months), result is Duration
        // PT1S (1 second) × P1Y (12 months) = PT12S
        assertThat(handler.executeMult(Duration.ofSeconds(1), Period.ofYears(1), ctx)).isEqualTo(Duration.ofSeconds(12));
        
        // Division - B-FEEL implicit coercion: converts duration to numeric value before division
        assertThat(handler.executeDivision("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(20, 4, ctx)).isEqualTo(new BigDecimal("5"));
        
        // Number ÷ Duration: Duration converted to seconds (5 days = 432000 seconds)
        // 10 ÷ 432000 = 0.000023148...
        assertThat(handler.executeDivision(10, Duration.ofDays(5), ctx)).isInstanceOf(BigDecimal.class);
        assertThat(((BigDecimal) handler.executeDivision(10, Duration.ofDays(5), ctx)).compareTo(BigDecimal.ZERO)).isGreaterThan(0);
        
        // Number ÷ Period: Period converted to months (P2Y = 24 months)
        // 26 ÷ 24 = 1.0833...
        assertThat(handler.executeDivision(26, Period.ofYears(2), ctx)).isInstanceOf(BigDecimal.class);
        BigDecimal result = (BigDecimal) handler.executeDivision(26, Period.ofYears(2), ctx);
        assertThat(result.compareTo(new BigDecimal("1.08"))).isGreaterThan(0);
        assertThat(result.compareTo(new BigDecimal("1.09"))).isLessThan(0);
        
        // Division by zero duration returns BigDecimal.ZERO (B-FEEL never returns null)
        assertThat(handler.executeDivision(10, Duration.ZERO, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(10, Period.ofMonths(0), ctx)).isEqualTo(BigDecimal.ZERO);
        
        // Duration ÷ Duration → number (valid operation, returns ratio)
        // PT10S ÷ PT5S = 2
        assertThat(handler.executeDivision(Duration.ofSeconds(10), Duration.ofSeconds(5), ctx)).isEqualTo(new BigDecimal("2"));
        // Division by zero duration returns BigDecimal.ZERO in B-FEEL
        assertThat(handler.executeDivision(Duration.ofSeconds(10), Duration.ZERO, ctx)).isEqualTo(BigDecimal.ZERO);
        
        // Period ÷ Period → number (valid operation, returns ratio)
        // P2Y ÷ P1Y = 24 months ÷ 12 months = 2
        assertThat(handler.executeDivision(Period.ofYears(2), Period.ofYears(1), ctx)).isEqualTo(new BigDecimal("2"));
        // Division by zero period returns BigDecimal.ZERO in B-FEEL
        assertThat(handler.executeDivision(Period.ofMonths(12), Period.ofMonths(0), ctx)).isEqualTo(BigDecimal.ZERO);
        
        assertThat(handler.executeDivision(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeDivision(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        assertThat(handler.executeDivision(null, 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(10, 0, ctx)).isEqualTo(BigDecimal.ZERO); // Division by zero returns 0 in B-FEEL
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

    @Test
    void testBFEELExponentiationRules() {
        // Basic exponentiation
        assertThat(handler.executePow(2, 3, ctx)).isEqualTo(new BigDecimal("8"));
        assertThat(handler.executePow(4, 2, ctx)).isEqualTo(new BigDecimal("16"));
        
        // Negative base - Note: -4 ** 2 is parsed as (-4) ** 2 = 16 by the parser
        // This test verifies the handler correctly computes (-4) ** 2
        assertThat(handler.executePow(-4, 2, ctx)).isEqualTo(new BigDecimal("16"));
        assertThat(handler.executePow(-2, 3, ctx)).isEqualTo(new BigDecimal("-8"));
        
        // Implicit coercion: Duration to number (seconds)
        assertThat(handler.executePow(Duration.ofSeconds(2), 3, ctx)).isEqualTo(new BigDecimal("8"));
        
        // Error handling: Returns 0 instead of null
        assertThat(handler.executePow(null, 2, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, null, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow("invalid", 2, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, "invalid", ctx)).isEqualTo(BigDecimal.ZERO);
        
        // Exponent range validation: [-999,999,999..999,999,999]
        assertThat(handler.executePow(2, 999999999, ctx)).isInstanceOf(BigDecimal.class);
        assertThat(handler.executePow(2, -999999999, ctx)).isInstanceOf(BigDecimal.class);
        
        // Out of range exponent returns 0
        assertThat(handler.executePow(2, new BigDecimal("1000000000"), ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, new BigDecimal("-1000000000"), ctx)).isEqualTo(BigDecimal.ZERO);
        
        // Fractional exponents
        BigDecimal sqrtResult = (BigDecimal) handler.executePow(4, 0.5, ctx);
        assertThat(sqrtResult.compareTo(new BigDecimal("2"))).isEqualTo(0);
        assertThat(handler.executePow(8, new BigDecimal("0.333333333333333"), ctx))
                .isInstanceOf(BigDecimal.class);
        
        // Zero and one cases
        assertThat(handler.executePow(0, 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(5, 0, ctx)).isEqualTo(BigDecimal.ONE);
        assertThat(handler.executePow(1, 1000, ctx)).isEqualTo(BigDecimal.ONE);
    }
}

