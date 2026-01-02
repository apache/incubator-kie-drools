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

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getString;

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

        // any String operand → concatenate both as strings
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> {
                    String leftNum = getString(left);
                    String rightNum = getString(right);
                    return leftNum + rightNum;
                });

        // date + number → return the number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Number, false),
                (left, right) -> right);
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof LocalDate, false),
                (left, right) -> left);

        // Number + null → number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right == null, false),
                (left, right) -> left);

        // null + Number → number
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
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the BFeel specific 'Power' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getPowOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        //TODO Change pow behaviour for BFeel
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> Boolean.FALSE);

        map.putAll(getCommonPowOperations(ctx));
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

        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> null);

        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof ChronoPeriod, false),
                (left, right) -> null);
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

        // number ÷ duration → invalid → null
        map.put(new CheckedPredicate((l, r) -> l instanceof Number && r instanceof Duration, false),
                (l, r) -> null);

        // number ÷ period → invalid → null
        map.put(new CheckedPredicate((l, r) -> l instanceof Number && r instanceof ChronoPeriod, false),
                (l, r) -> null);

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

        map.putAll(getCommonDivisionOperations(ctx));
        return map;
    }

    @Override
    public Boolean compare(Object left, Object right, BiPredicate<Comparable, Comparable> op) {
        return compare(left, right, op, () -> Boolean.FALSE, () -> Boolean.FALSE);
    }
}
