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
    void testAdditionWithPrecedenceOrder() {
        //Basic addition operation
        assertThat(handler.executeAdd(10, 20, ctx)).isEqualTo(new BigDecimal("30"));
        assertThat(handler.executeAdd(10, null, ctx)).isEqualTo(10);
        // Case 1 : STRING - If either operand is a string, convert non-string to string
        // Left: string + other
        assertThat(handler.executeAdd("Hello", 123, ctx)).isEqualTo("Hello123");
        assertThat(handler.executeAdd("Date:", LocalDate.of(2024, 1, 1), ctx)).isEqualTo("Date:2024-01-01");
        assertThat(handler.executeAdd("Duration:", Duration.ofDays(5), ctx)).isEqualTo("Duration:PT120H");
        assertThat(handler.executeAdd("Period:", Period.ofMonths(3), ctx)).isEqualTo("Period:P3M");
        
        // Right: other + string
        assertThat(handler.executeAdd(123, "World", ctx)).isEqualTo("123World");
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), " is the date", ctx)).isEqualTo("2024-01-01 is the date");
        assertThat(handler.executeAdd(Duration.ofDays(5), " duration", ctx)).isEqualTo("PT120H duration");
        assertThat(handler.executeAdd(Period.ofMonths(3), " period", ctx)).isEqualTo("P3M period");
        
        // Case 2 : NUMBER - If either operand is a number (and neither is string), convert non-number to number
        // Per BFEEL Section 11.3: number(date) = 0, number(duration) = seconds, number(period) = months
        
        // Left: date + number → 0 + number = number
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), 5, ctx)).isEqualTo(5);
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), new BigDecimal("10.5"), ctx))
                .isEqualTo(new BigDecimal("10.5"));
        
        // Right: number + date → number + 0 = number
        assertThat(handler.executeAdd(5, LocalDate.of(2024, 1, 1), ctx)).isEqualTo(5);
        assertThat(handler.executeAdd(new BigDecimal("10.5"), LocalDate.of(2024, 1, 1), ctx))
                .isEqualTo(new BigDecimal("10.5"));
        
        // Duration + number → seconds + number (Row 2: NUMBER precedence)
        assertThat(handler.executeAdd(Duration.ofSeconds(10), 5, ctx)).isEqualTo(new BigDecimal("15"));
        assertThat(handler.executeAdd(5, Duration.ofSeconds(10), ctx)).isEqualTo(new BigDecimal("15"));
        
        // Period + number → months + number (Row 2: NUMBER precedence)
        assertThat(handler.executeAdd(Period.ofMonths(12), 3, ctx)).isEqualTo(new BigDecimal("15"));
        assertThat(handler.executeAdd(3, Period.ofMonths(12), ctx)).isEqualTo(new BigDecimal("15"));
        
        // Case 3: DATE - If either operand is a date, convert non-date to duration
        // Left: date + duration
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 6));
        assertThat(handler.executeAdd(LocalDate.of(2024, 1, 1), Period.ofMonths(2), ctx))
                .isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(handler.executeAdd("Hello", null, ctx)).isEqualTo("Hello");
    }

    @Test
    void testSubtractionWithPrecedenceOrder() {
        // Basic subtraction operation
        assertThat(handler.executeSub(20, 10, ctx)).isEqualTo(new BigDecimal("10"));
        
        // Case 1: STRING - If either operand is a string, subtraction returns empty string ""
        // Left: string - other
        assertThat(handler.executeSub("Hello", 123, ctx)).isEqualTo("");
        assertThat(handler.executeSub("Date", LocalDate.of(2024, 1, 1), ctx)).isEqualTo("");
        assertThat(handler.executeSub("Duration", Duration.ofDays(5), ctx)).isEqualTo("");
        
        // Right: other - string
        assertThat(handler.executeSub(123, "World", ctx)).isEqualTo("");
        assertThat(handler.executeSub(LocalDate.of(2024, 1, 1), "date", ctx)).isEqualTo("");
        assertThat(handler.executeSub(Duration.ofDays(5), "duration", ctx)).isEqualTo("");
        
        // Case 2: NUMBER - If either operand is a number (and neither is string), convert non-number to number
        // Standard numeric subtraction applies after conversion
        assertThat(handler.executeSub(10, 5, ctx)).isEqualTo(new BigDecimal("5"));
        assertThat(handler.executeSub(new BigDecimal("20.5"), new BigDecimal("10.5"), ctx))
                .isEqualTo(new BigDecimal("10.0"));
        
        // Case 3: DATE - If either operand is a date, convert non-date to duration
        // Left: date - duration
        assertThat(handler.executeSub(LocalDate.of(2024, 1, 10), Duration.ofDays(5), ctx))
                .isEqualTo(LocalDate.of(2024, 1, 5));
        assertThat(handler.executeSub(LocalDate.of(2024, 3, 1), Period.ofMonths(1), ctx))
                .isEqualTo(LocalDate.of(2024, 2, 1));
        
        //Case 4 : DAYS AND TIME DURATION - Duration subtraction
        assertThat(handler.executeSub(Duration.ofDays(10), Duration.ofDays(3), ctx))
                .isEqualTo(Duration.ofDays(7));
    }

    @Test
    void testMultiplication() {
        // basic multiplication operation
        assertThat(handler.executeMult(5, 10, ctx)).isEqualTo(new BigDecimal("50"));

        // multiplication - invalid types
        assertThat(handler.executeMult("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeMult(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeMult(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));

        // Duration × Duration (same type) → zero duration (B-FEEL default)
        assertThat(handler.executeMult(Duration.ofDays(5), Duration.ofDays(3), ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeMult(Period.ofMonths(6), Period.ofMonths(2), ctx)).isEqualTo(ComparablePeriod.ofMonths(0));
        
        // YEARS AND MONTHS DURATION (Period) × Number
        // Left: period × number
        assertThat(handler.executeMult(Period.ofMonths(12), 2, ctx))
                .isEqualTo(ComparablePeriod.ofMonths(24));
        // Right: number × period
        assertThat(handler.executeMult(2, Period.ofMonths(12), ctx))
                .isEqualTo(ComparablePeriod.ofMonths(24));
        
        // DAYS AND TIME DURATION (Duration) × Number
        // Left: duration × number
        assertThat(handler.executeMult(Duration.ofDays(5), 2, ctx))
                .isEqualTo(Duration.ofDays(10));
        // Right: number × duration
        assertThat(handler.executeMult(2, Duration.ofDays(5), ctx))
                .isEqualTo(Duration.ofDays(10));
        
        // Period × Duration: B-FEEL Section 11.10 - YMD has higher precedence than DTD
        // Duration converted to seconds (as number), result is Period
        // P1Y (12 months) × PT1S (1 second) = P12M
        assertThat(handler.executeMult(Period.ofYears(1), Duration.ofSeconds(1), ctx))
                .isEqualTo(ComparablePeriod.ofMonths(12));
        
        // Duration × Period: B-FEEL Section 11.10 - YMD has higher precedence than DTD
        // Duration converted to seconds (as number), result is Period
        // PT1S (1 second) × P1Y (12 months) = P12M (or P1Y)
        assertThat(handler.executeMult(Duration.ofSeconds(1), Period.ofYears(1), ctx))
                .isEqualTo(ComparablePeriod.ofMonths(12));
    }
    
    @Test
    void testDivision() {
        // basic division operation
        assertThat(handler.executeDivision(20, 4, ctx)).isEqualTo(new BigDecimal("5"));
        assertThat(handler.executeDivision(10, 0, ctx)).isEqualTo(BigDecimal.ZERO); // Division by zero returns 0

        // null or non-numeric left operand
        assertThat(handler.executeDivision("test", 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(null, 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(Duration.ofDays(5), null, ctx)).isEqualTo(Duration.ZERO);
        assertThat(handler.executeDivision(Period.ofMonths(3), null, ctx)).isEqualTo(ComparablePeriod.ofMonths(0));

        // Number ÷ Duration: Duration converted to seconds
        assertThat(handler.executeDivision(10, Duration.ofDays(5), ctx)).isInstanceOf(BigDecimal.class);
        assertThat(((BigDecimal) handler.executeDivision(10, Duration.ofDays(5), ctx)).compareTo(BigDecimal.ZERO))
                .isGreaterThan(0);   

        // Number ÷ Period: Period converted to months
        assertThat(handler.executeDivision(26, Period.ofYears(2), ctx)).isInstanceOf(BigDecimal.class);
        BigDecimal result = (BigDecimal) handler.executeDivision(26, Period.ofYears(2), ctx);
        assertThat(result.compareTo(new BigDecimal("1.08"))).isGreaterThan(0);
        assertThat(result.compareTo(new BigDecimal("1.09"))).isLessThan(0);

        // Duration ÷ Duration → number (returns ratio)
        assertThat(handler.executeDivision(Duration.ofSeconds(10), Duration.ofSeconds(5), ctx))
                .isEqualTo(new BigDecimal("2"));
        assertThat(handler.executeDivision(Duration.ofSeconds(10), Duration.ZERO, ctx))
                .isEqualTo(BigDecimal.ZERO);
        
        // Period ÷ Period → number (returns ratio)
        assertThat(handler.executeDivision(Period.ofYears(2), Period.ofYears(1), ctx))
                .isEqualTo(new BigDecimal("2"));
        assertThat(handler.executeDivision(Period.ofMonths(12), Period.ofMonths(0), ctx))
                .isEqualTo(BigDecimal.ZERO);

        // division by zero
        assertThat(handler.executeDivision(10, Duration.ZERO, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executeDivision(10, Period.ofMonths(0), ctx)).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void testExponentiation() {
        // basic cases
        assertThat(handler.executePow(2, 3, ctx)).isEqualTo(new BigDecimal("8"));
        assertThat(handler.executePow(4, 2, ctx)).isEqualTo(new BigDecimal("16"));
        assertThat(handler.executePow(-4, 2, ctx)).isEqualTo(new BigDecimal("16"));
        assertThat(handler.executePow(-2, 3, ctx)).isEqualTo(new BigDecimal("-8"));

        // testExponentiation_ImplicitCoercion
        assertThat(handler.executePow("2", 3, ctx)).isEqualTo(new BigDecimal("8"));
        assertThat(handler.executePow(2, "3", ctx)).isEqualTo(new BigDecimal("8"));
        assertThat(handler.executePow(Duration.ofSeconds(2), 3, ctx)).isEqualTo(new BigDecimal("8"));

        // testExponentiation_ErrorHandling
        assertThat(handler.executePow(null, 2, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, null, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow("invalid", 2, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, "invalid", ctx)).isEqualTo(BigDecimal.ZERO);

        // testExponentiation_RangeValidation
        assertThat(handler.executePow(2, 999999999, ctx)).isInstanceOf(BigDecimal.class);
        assertThat(handler.executePow(2, -999999999, ctx)).isInstanceOf(BigDecimal.class);
        assertThat(handler.executePow(2, new BigDecimal("1000000000"), ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(2, new BigDecimal("-1000000000"), ctx)).isEqualTo(BigDecimal.ZERO);

        // testExponentiation_FractionalExponents
        BigDecimal sqrtResult = (BigDecimal) handler.executePow(4, 0.5, ctx);
        assertThat(sqrtResult.compareTo(new BigDecimal("2"))).isEqualTo(0);
        assertThat(handler.executePow(8, new BigDecimal("0.333333333333333"), ctx))
                .isInstanceOf(BigDecimal.class);

        // testExponentiation_BoundaryCases
        assertThat(handler.executePow(0, 5, ctx)).isEqualTo(BigDecimal.ZERO);
        assertThat(handler.executePow(5, 0, ctx)).isEqualTo(BigDecimal.ONE);
        assertThat(handler.executePow(1, 1000, ctx)).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void testLogicalOperations() {
        // LogicalOperations_And
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.TRUE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeAnd(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeAnd("test", Boolean.TRUE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeAnd("notBoolean", "alsoNotBoolean", ctx)).isEqualTo(Boolean.FALSE);
   
        // LogicalOperations_Or
        assertThat(handler.executeOr(Boolean.TRUE, Boolean.FALSE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeOr(Boolean.FALSE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void testComparisonOperations() {
        // Comparison_Equality
        assertThat(handler.executeEqual(null, null, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 10, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeEqual(10, 20, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeEqual("10", 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeEqual(null, 10, ctx)).isEqualTo(Boolean.FALSE);
        
        assertThat(handler.executeNotEqual(null, null, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeNotEqual(10, 20, ctx)).isEqualTo(Boolean.TRUE);
    
        // Comparison_Relational
        assertThat(handler.executeGte(Boolean.FALSE, Boolean.FALSE, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeGte("test", Boolean.TRUE, ctx)).isEqualTo(Boolean.TRUE);
        assertThat(handler.executeGte(20, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeGte("test", 10, ctx)).isEqualTo(Boolean.FALSE);
        
        assertThat(handler.executeGt(20, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeGt(new Object(), new Object(), ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeLte(5, 10, ctx)).isEqualTo(Boolean.FALSE);
        assertThat(handler.executeLt(5, 10, ctx)).isEqualTo(Boolean.FALSE);
    
        // Comparison_CompareMethod
        assertThat(handler.compare(null, null, (l, r) -> l.compareTo(r) > 0)).isEqualTo(Boolean.FALSE);
        assertThat(handler.compare(20, 10, (l, r) -> l.compareTo(r) > 0)).isTrue();
        assertThat(handler.compare("test", "test", (l, r) -> l.compareTo(r) == 0)).isTrue();
        assertThat(handler.compare("test", 10, (l, r) -> l.compareTo(r) > 0)).isEqualTo(Boolean.FALSE);
    }

}

