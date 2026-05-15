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
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.infixexecutors.EqExecutor;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import ch.obermuhlner.math.big.BigDecimalMath;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getString;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

/**
 * Handler implementation of the DialectHandler interface providing BFEEL specific
 * functionalities
 */
public class BFEELDialectHandler extends DefaultDialectHandler implements DialectHandler {

    /**
     * Builds the BFeel specific 'Addition' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Addition' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // B-FEEL Precedence Table for Addition (DMN 1.6 Section 11.9):
        // Row 1: STRING - The non-string value is converted to a string using the string() B-FEEL function
        //                 and Table 58 applies. Subtraction returns an empty string.
        // Row 2: NUMBER - The non-number value is converted to a number using the number() B-FEEL function
        //                 and Table 58 applies.
        // Row 3: DATE AND TIME - The non-date and time value is converted to a duration using the duration()
        //                        B-FEEL function and Table 58 applies.
        // Row 4: DATE - The non-date value is converted to a duration using the duration() B-FEEL function
        //               and Table 58 applies.
        // Row 5: TIME - The non-time value is converted to a duration using the duration() B-FEEL function
        //               and Table 58 applies.
        // Row 6: YEARS AND MONTHS DURATION - The non-years and months duration value is converted to a
        //                                     duration using the duration() B-FEEL function and Table 58 applies.
        // Row 7: DAYS AND TIME DURATION - The non-days and time duration value is converted to a duration
        //                                  using the duration() B-FEEL function and Table 58 applies.
        
        // Row 1: STRING - any String operand → concatenate both as strings (subtraction returns "")
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> {
                    String leftNum = getString(left);
                    String rightNum = getString(right);
                    return leftNum + rightNum;
                });

        // Row 2: NUMBER - date + number → convert date to 0, then add: 0 + number = number
        // Since 0 + number = number, we return the number operand directly
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Number, false),
                (left, right) -> right);
        
        // Row 2: NUMBER - number + date → convert date to 0, then add: number + 0 = number
        // Since number + 0 = number, we return the number operand directly
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof LocalDate, false),
                (left, right) -> left);

        // Row 2: NUMBER - duration + number → convert duration to seconds, then add
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal seconds = getBigDecimalOrNull(((Duration) left).getSeconds());
                    BigDecimal rightNum = getBigDecimalOrNull(right);
                    if (seconds == null || rightNum == null) return rightNum != null ? rightNum : BigDecimal.ZERO;
                    return seconds.add(rightNum, MathContext.DECIMAL128);
                });

        // Row 2: NUMBER - number + duration → convert duration to seconds, then add
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Duration, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal seconds = getBigDecimalOrNull(((Duration) right).getSeconds());
                    if (leftNum == null || seconds == null) return leftNum != null ? leftNum : BigDecimal.ZERO;
                    return leftNum.add(seconds, MathContext.DECIMAL128);
                });

        // Row 2: NUMBER - period + number → convert period to months, then add
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal months = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left));
                    BigDecimal rightNum = getBigDecimalOrNull(right);
                    if (months == null || rightNum == null) return rightNum != null ? rightNum : BigDecimal.ZERO;
                    return months.add(rightNum, MathContext.DECIMAL128);
                });

        // Row 2: NUMBER - number + period → convert period to months, then add
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof ChronoPeriod, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal months = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right));
                    if (leftNum == null || months == null) return leftNum != null ? leftNum : BigDecimal.ZERO;
                    return leftNum.add(months, MathContext.DECIMAL128);
                });

        // Number + null → number (null converts to 0 in B-FEEL numeric context)
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right == null, false),
                (left, right) -> left);

        // null + Number → number (null converts to 0 in B-FEEL numeric context)
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof Number, false),
                (left, right) -> right);

        map.putAll(getCommonAddOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAndOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        FEELDialect dialect = ctx.getFEELDialect();

        // Special case: true AND otherwise → false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.TRUE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // Special case: false AND true → false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.TRUE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // Special case: false AND false → false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        map.putAll(getCommonAndOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getEqualOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Shortcut: null = null → false
        map.put(
                new CheckedPredicate((left, right) -> left == null && right == null, false),
                (left, right) -> Boolean.TRUE);
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> isEqual(left, right,
                        () -> Boolean.FALSE, () -> Boolean.FALSE));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGteOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // Any non-Boolean coerces to false, so (false,false) --> false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // non-Boolean coercion to false
        map.put(
                new CheckedPredicate((left, right) -> (!(left instanceof Boolean) || !(right instanceof Boolean)), false),
                (left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Boolean rightBool = (right instanceof Boolean) ? (Boolean) right : Boolean.FALSE;
                    return leftBool || rightBool;
                });
        // numeric/comparable >= logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean greater = compare(left, right, (l, r) -> l.compareTo(r) > 0);
                    //Boolean equal = BooleanEvalHelper.isEqual(left, right, ctx.getFEELDialect());
                    Boolean equal = (EqExecutor.instance().evaluate(left, right, ctx) instanceof Boolean)
                            ? (Boolean) EqExecutor.instance().evaluate(left, right, ctx)
                            : null;

                    if (greater == null && equal == null) {
                        return Boolean.FALSE; // BFEEL default
                    }
                    if (Boolean.TRUE.equals(greater) || Boolean.TRUE.equals(equal)) {
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                });

        // Fall back to common >= operations for all other cases
        map.putAll(getCommonGteOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGtOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Any non-Boolean coerces to false, so (false,false) --> false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // non-Boolean coercion to false
        map.put(
                new CheckedPredicate((left, right) -> (!(left instanceof Boolean) || !(right instanceof Boolean)), false),
                (left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Boolean rightBool = (right instanceof Boolean) ? (Boolean) right : Boolean.FALSE;
                    return leftBool || rightBool;
                });

        // numeric/comparable > logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean greater = compare(left, right,
                            (l, r) -> l.compareTo(r) > 0);
                    return Objects.requireNonNullElse(greater, Boolean.FALSE);
                });

        map.putAll(getCommonGtOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getLteOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Any non-Boolean coerces to false, so (false,false) --> false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // General non-Boolean coercion to false
        map.put(
                new CheckedPredicate((left, right) -> (!(left instanceof Boolean) || !(right instanceof Boolean)), false),
                (left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Boolean rightBool = (right instanceof Boolean) ? (Boolean) right : Boolean.FALSE;
                    return leftBool || rightBool;
                });

        // Numeric/comparable ≤ logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean less = compare(left, right,
                            (l, r) -> l.compareTo(r) < 0);
                    Boolean equal = (EqExecutor.instance().evaluate(left, right, ctx) instanceof Boolean)
                            ? (Boolean) EqExecutor.instance().evaluate(left, right, ctx)
                            : null;

                    if (less == null && equal == null) {
                        return Boolean.FALSE; // BFEEL default
                    }
                    if (Boolean.TRUE.equals(less) || Boolean.TRUE.equals(equal)) {
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                });

        map.putAll(getCommonLteOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getLtOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Non-Boolean coerces to false, so (false,false) --> false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        // General non-Boolean coercion to false
        map.put(
                new CheckedPredicate((left, right) -> (!(left instanceof Boolean) || !(right instanceof Boolean)), false),
                (left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Boolean rightBool = (right instanceof Boolean) ? (Boolean) right : Boolean.FALSE;
                    return leftBool || rightBool;
                });

        // Numeric/comparable < logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean less = compare(left, right,
                            (l, r) -> l.compareTo(r) < 0);
                    return Objects.requireNonNullElse(less, Boolean.FALSE);
                });
        map.putAll(getCommonLtOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getNotEqualOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // Shortcut: null != null → true
        map.put(
                new CheckedPredicate((left, right) -> left == null && right == null, false),
                (left, right) -> Boolean.FALSE);
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean result = isEqual(left, right,
                            () -> Boolean.FALSE, // nullFallback
                            () -> Boolean.FALSE // defaultFallback
                    );
                    // If result is null, treat as false
                    return result != null ? !result : Boolean.FALSE;
                });

        return map;
    }

    /**
     * Builds the BFeel specific 'OR' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'OR' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getOrOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // false OR otherwise → false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean leftBool = (left instanceof Boolean) ? (Boolean) left : Boolean.FALSE;
                    Object rightValue = evalRight(right, ctx);
                    Boolean rightBool = (rightValue instanceof Boolean) ? (Boolean) rightValue : Boolean.FALSE;
                    return Boolean.FALSE.equals(leftBool) && Boolean.FALSE.equals(rightBool);
                }, false),
                (left, right) -> Boolean.FALSE);

        map.putAll(getCommonOrOperations(ctx));
        return map;
    }

    /**
     * Builds the BFeel specific 'Power' operations.
     * <p>
     * B-FEEL exponentiation rules:
     * <ul>
     *   <li>Implicit Coercion: Both operands are converted to numbers using B-FEEL number() function</li>
     *   <li>Error Handling: Returns 0 instead of null for semantic errors or out-of-range scale</li>
     *   <li>Precision: Result limited to 34 digits of precision</li>
     *   <li>Range: Exponent must be in range [-999,999,999..999,999,999]</li>
     *   <li>Precedence: -4 ** 2 is interpreted as (-4) ** 2 = 16 (handled by parser)</li>
     * </ul>
     *
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Power' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getPowOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        
        // B-FEEL: Implicit coercion to numbers, return 0 on error instead of null
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    // Implicit coercion: convert both operands to numbers using B-FEEL number() semantics
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal rightNum = getBigDecimalOrNull(right);
                    
                    // B-FEEL implicit coercion: Duration → seconds
                    if (leftNum == null && left instanceof Duration) {
                        leftNum = BigDecimal.valueOf(((Duration) left).getSeconds());
                    }
                    if (rightNum == null && right instanceof Duration) {
                        rightNum = BigDecimal.valueOf(((Duration) right).getSeconds());
                    }
                    
                    // B-FEEL implicit coercion: ChronoPeriod → total months
                    if (leftNum == null && left instanceof ChronoPeriod) {
                        leftNum = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left));
                    }
                    if (rightNum == null && right instanceof ChronoPeriod) {
                        rightNum = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right));
                    }
                    
                    // B-FEEL returns 0 instead of null for invalid operations
                    if (leftNum == null || rightNum == null) {
                        return BigDecimal.ZERO;
                    }
                    
                    // Check exponent range: [-999,999,999..999,999,999]
                    BigDecimal minExponent = new BigDecimal("-999999999");
                    BigDecimal maxExponent = new BigDecimal("999999999");
                    if (rightNum.compareTo(minExponent) < 0 || rightNum.compareTo(maxExponent) > 0) {
                        return BigDecimal.ZERO; // Out of range, return 0
                    }
                    
                    try {
                        // Apply standard FEEL semantics with 34 digits precision (DECIMAL128)
                        BigDecimal result = BigDecimalMath.pow(leftNum, rightNum, MathContext.DECIMAL128);
                        return result;
                    } catch (ArithmeticException e) {
                        // B-FEEL returns 0 for arithmetic errors instead of null
                        return BigDecimal.ZERO;
                    }
                });

        return map;
    }

    /**
     * Builds the BFeel specific 'Substraction' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Substraction' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getSubOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // empty string
        map.put(
                new CheckedPredicate((left, right) -> (left instanceof String || right instanceof String), false),
                (left, right) -> "");
        map.putAll(getCommonSubOperations(ctx));
        return map;
    }

    /**
     * Builds the BFeel specific 'Multiplication' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Multiplication' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getMultOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // String * Number or Number * String → BigDecimal.ZERO
        map.put(
                new CheckedPredicate((left, right) -> (left instanceof Number && right instanceof String) ||
                        (left instanceof String && right instanceof Number), false),
                (left, right) -> BigDecimal.ZERO);

        // String * String → BigDecimal.ZERO
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String && right instanceof String, false),
                (left, right) -> BigDecimal.ZERO);
        // Duration * null → zero duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right == null, false),
                (left, right) -> Duration.ZERO);
        // null * Duration → zero duration
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof Duration, false),
                (left, right) -> Duration.ZERO);

        // ChronoPeriod * null → zero period
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right == null, false),
                (left, right) -> ComparablePeriod.ofMonths(0));

        // null * ChronoPeriod → zero period
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof ChronoPeriod, false),
                (left, right) -> ComparablePeriod.ofMonths(0));

        // Period × Duration (mixed types) → B-FEEL Section 11.10: YMD has higher precedence than DTD
        // Convert Duration to seconds (as number), result is Period
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof Duration, false),
                (left, right) -> {
                    BigDecimal months = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left));
                    BigDecimal seconds = getBigDecimalOrNull(((Duration) right).getSeconds());
                    if (months == null || seconds == null)
                        return ComparablePeriod.ofMonths(0);
                    return ComparablePeriod.ofMonths(months.multiply(seconds, MathContext.DECIMAL128).intValue());
                });

        // Duration × Period (mixed types) → B-FEEL Section 11.10: YMD has higher precedence than DTD
        // Convert Duration to seconds (as number), result is Period
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof ChronoPeriod, false),
                (left, right) -> {
                    BigDecimal seconds = getBigDecimalOrNull(((Duration) left).getSeconds());
                    BigDecimal months = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right));
                    if (seconds == null || months == null)
                        return ComparablePeriod.ofMonths(0);
                    return ComparablePeriod.ofMonths(seconds.multiply(months, MathContext.DECIMAL128).intValue());
                });

        // Duration × Duration (same type) → returns zero duration (B-FEEL default for disallowed operation)
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> Duration.ZERO);

        // Period × Period (same type) → returns zero period (B-FEEL default for disallowed operation)
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof ChronoPeriod, false),
                (left, right) -> ComparablePeriod.ofMonths(0));
        map.putAll(getCommonMultOperations(ctx));
        return map;
    }

    /**
     * Builds the BFeel specific 'Division' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Division' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getDivisionOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // string ÷ number or number ÷ string → BigDecimal.ZERO
        map.put(new CheckedPredicate((l, r) -> (l instanceof Number && r instanceof String) ||
                (l instanceof String && r instanceof Number), false),
                (l, r) -> BigDecimal.ZERO);

        // string ÷ string → BigDecimal.ZERO
        map.put(new CheckedPredicate((l, r) -> l instanceof String && r instanceof String, false),
                (l, r) -> BigDecimal.ZERO);

        // number ÷ duration → B-FEEL implicit coercion: convert duration to seconds, then divide
        map.put(new CheckedPredicate((l, r) -> l instanceof Number && r instanceof Duration, false),
                (l, r) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(l);
                    BigDecimal rightSecs = getBigDecimalOrNull(((Duration) r).getSeconds());
                    if (leftNum == null || rightSecs == null || rightSecs.compareTo(BigDecimal.ZERO) == 0)
                        return BigDecimal.ZERO; // B-FEEL returns 0 instead of null
                    return leftNum.divide(rightSecs, MathContext.DECIMAL128);
                });

        // number ÷ period → B-FEEL implicit coercion: convert period to total months, then divide
        map.put(new CheckedPredicate((l, r) -> l instanceof Number && r instanceof ChronoPeriod, false),
                (l, r) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(l);
                    BigDecimal rightMonths = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) r));
                    if (leftNum == null || rightMonths == null || rightMonths.compareTo(BigDecimal.ZERO) == 0)
                        return BigDecimal.ZERO; // B-FEEL returns 0 instead of null
                    return leftNum.divide(rightMonths, MathContext.DECIMAL128);
                });

        // duration ÷ null → Duration.ZERO
        map.put(new CheckedPredicate((l, r) -> l instanceof Duration && r == null, false),
                (l, r) -> Duration.ZERO);

        // null ÷ duration → Duration.ZERO
        map.put(new CheckedPredicate((l, r) -> l == null && r instanceof Duration, false),
                (l, r) -> Duration.ZERO);

        // period ÷ null → P0M
        map.put(new CheckedPredicate((l, r) -> l instanceof ChronoPeriod && r == null, false),
                (l, r) -> ComparablePeriod.ofMonths(0));

        // null ÷ period → P0M
        map.put(new CheckedPredicate((l, r) -> l == null && r instanceof ChronoPeriod, false),
                (l, r) -> ComparablePeriod.ofMonths(0));

        // null ÷ number
        map.put(new CheckedPredicate((l, r) -> l == null && r instanceof Number, false),
                (l, r) -> BigDecimal.ZERO);

        // number ÷ number (including division by zero) → B-FEEL returns BigDecimal.ZERO for division by zero
        map.put(new CheckedPredicate((l, r) -> l instanceof Number && r instanceof Number, false),
                (l, r) -> {
                    BigDecimal leftBD = getBigDecimalOrNull(l);
                    BigDecimal rightBD = getBigDecimalOrNull(r);
                    if (leftBD == null || rightBD == null || rightBD.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO; // B-FEEL returns 0 for division by zero
                    }
                    return leftBD.divide(rightBD, MathContext.DECIMAL128);
                });

        // duration ÷ duration → number (B-FEEL: returns 0 instead of null when divisor is zero)
        map.put(new CheckedPredicate((l, r) -> l instanceof Duration && r instanceof Duration, false),
                (l, r) -> {
                    BigDecimal leftSecs = getBigDecimalOrNull(((Duration) l).getSeconds());
                    BigDecimal rightSecs = getBigDecimalOrNull(((Duration) r).getSeconds());
                    if (leftSecs == null || rightSecs == null || rightSecs.compareTo(BigDecimal.ZERO) == 0)
                        return BigDecimal.ZERO; // B-FEEL returns 0 instead of null
                    return leftSecs.divide(rightSecs, MathContext.DECIMAL128);
                });

        // period ÷ period → number (B-FEEL: returns 0 instead of null when divisor is zero)
        map.put(new CheckedPredicate((l, r) -> l instanceof ChronoPeriod && r instanceof ChronoPeriod, false),
                (l, r) -> {
                    BigDecimal leftMonths = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) l));
                    BigDecimal rightMonths = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) r));
                    if (leftMonths == null || rightMonths == null || rightMonths.compareTo(BigDecimal.ZERO) == 0)
                        return BigDecimal.ZERO; // B-FEEL returns 0 instead of null
                    return leftMonths.divide(rightMonths, MathContext.DECIMAL128);
                });

        map.putAll(getCommonDivisionOperations(ctx));
        return map;
    }

    @Override
    public Boolean compare(Object left, Object right, BiPredicate<Comparable, Comparable> op) {
        return compare(left, right, op, () -> Boolean.FALSE, () -> Boolean.FALSE);
    }
}
