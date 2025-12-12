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
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.infixexecutors.EqExecutor;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.util.BooleanEvalHelper;
import org.kie.dmn.feel.util.BuiltInTypeUtils;
import org.kie.dmn.feel.util.DateTimeEvalHelper;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.addLocalDateAndDuration;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.commonManageInvalidParameters;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getBigDecimal;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.subtractTemporals;
import static org.kie.dmn.feel.util.DateTimeEvalHelper.valuedt;
import static org.kie.dmn.feel.util.DateTimeEvalHelper.valuet;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * Base implementation of the DialectHandler interface providing common
 * functionality for all dialects.
 */
public abstract class DefaultDialectHandler implements DialectHandler {

    /**
     * Builds the common 'Addition' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Addition' operations
     */
    protected Map<CheckedPredicate, BiFunction<Object, Object, Object>> getCommonAddOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Number + Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal rightNum = getBigDecimal(right, ctx);
                    return leftNum != null && rightNum != null ? leftNum.add(rightNum, MathContext.DECIMAL128) : null;
                });

        // Duration + LocalDate
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof LocalDate, false),
                (left, right) -> addLocalDateAndDuration((LocalDate) right, (Duration) left));

        // LocalDate + Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Duration, false),
                (left, right) -> addLocalDateAndDuration((LocalDate) left, (Duration) right));

        // Duration + Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> ((Duration) left).plus((Duration) right));

        // Temporal + TemporalAmount
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof TemporalAmount, false),
                (left, right) -> ((Temporal) left).plus((TemporalAmount) right));

        // TemporalAmount + Temporal
        map.put(
                new CheckedPredicate((left, right) -> left instanceof TemporalAmount && right instanceof Temporal, false),
                (left, right) -> ((Temporal) right).plus((TemporalAmount) left));

        // TemporalAmount + ChronoPeriod
        map.put(
                new CheckedPredicate((left, right) -> left instanceof TemporalAmount && right instanceof ChronoPeriod, false),
                (left, right) -> ((ChronoPeriod) right).plus((TemporalAmount) left));

        // left or right -> null
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> null);
        return map;
    }

    /**
     * Builds the common 'And' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'And' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonAndOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        FEELDialect dialect = ctx.getFEELDialect();

        // false AND anything → false (short‑circuit)
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.FALSE.equals(left), false),
                (left, right) -> Boolean.FALSE);

        // left not Boolean → treat as false
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && dialect.equals(FEELDialect.BFEEL), false),
                (left, right) -> Boolean.FALSE);

        // true AND true → true
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.TRUE.equals(left)
                        && Boolean.TRUE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.TRUE);

        // true AND false → false
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.TRUE.equals(left)
                        && Boolean.FALSE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.FALSE);

        // true AND otherwise → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.TRUE.equals(left)
                        && evalRight(right, ctx) == null, false),
                (left, right) -> null);

        // otherwise AND true → null
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && Boolean.TRUE.equals(evalRight(right, ctx)), false),
                (left, right) -> null);

        // otherwise AND false → false
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && Boolean.FALSE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.FALSE);

        // otherwise AND otherwise → null
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && evalRight(right, ctx) == null, false),
                (left, right) -> null);

        return map;
    }

    /**
     * Builds the common 'Greater than Or EqualTo' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Greater than Or EqualTo' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonGteOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        //        FEELDialect dialect = ctx.getFEELDialect();

        // left or right is null → null
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> null);

        // both results are Boolean
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && right instanceof Boolean, false),
                (left, right) -> {
                    Boolean leftBool = (Boolean) left;
                    Boolean rightBool = (Boolean) right;
                    return leftBool || rightBool;
                });

        // numeric/comparable >= logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean greater = compare(left, right,
                            (leftNum, rightNum) -> leftNum.compareTo(rightNum) > 0,
                            () -> null, // nullFallback for Default dialect
                            () -> null // defaultFallback for unknown types
                    );
                    //Boolean equal = BooleanEvalHelper.isEqual(left, right, dialect);
                    Boolean equal = (EqExecutor.instance().evaluate(left, right, ctx) instanceof Boolean)
                            ? (Boolean) EqExecutor.instance().evaluate(left, right, ctx)
                            : null;
                    if (greater == null && equal == null)
                        return null;
                    if (Boolean.TRUE.equals(greater) || Boolean.TRUE.equals(equal))
                        return Boolean.TRUE;
                    return Boolean.FALSE;
                });
        return map;
    }

    /**
     * Builds the common 'Greater than' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Greater than' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonGtOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        //  FEELDialect dialect = ctx.getFEELDialect();
        // numeric/comparable > logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {

                    // default dialect: keep null
                    return compare(left, right,
                            (l, r) -> l.compareTo(r) > 0,
                            () -> null,
                            () -> null);
                });
        return map;
    }

    /**
     * Builds the common 'Less than Or EqualTo' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Less than Or EqualTo' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonLteOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // left or right is null → null
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> null);

        // both results are Boolean
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && right instanceof Boolean, false),
                (left, right) -> {
                    Boolean leftBool = (Boolean) left;
                    Boolean rightBool = (Boolean) right;
                    return leftBool || rightBool;
                });

        // numeric/comparable ≤ logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    Boolean less = compare(left, right,
                            (l, r) -> l.compareTo(r) < 0,
                            () -> null,
                            () -> null);
                    // Boolean equal = BooleanEvalHelper.isEqual(left, right, dialect);
                    Boolean equal = (EqExecutor.instance().evaluate(left, right, ctx) instanceof Boolean)
                            ? (Boolean) EqExecutor.instance().evaluate(left, right, ctx)
                            : null;

                    if (less == null && equal == null) {
                        return null;
                    }
                    if (Boolean.TRUE.equals(less) || Boolean.TRUE.equals(equal)) {
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                });
        return map;
    }

    /**
     * Builds the common 'Less than' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Less than Or EqualTo' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonLtOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // numeric/comparable < logic
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> {
                    return compare(left, right,
                            (l, r) -> l.compareTo(r) < 0,
                            () -> null,
                            () -> null);
                });
        return map;
    }

    /**
     * Builds the common 'Or' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Or operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonOrOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        FEELDialect dialect = ctx.getFEELDialect();
        // true OR anything → true (short‑circuit)
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.TRUE.equals(left), false),
                (left, right) -> Boolean.TRUE);

        // left not Boolean → false
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && dialect.equals(FEELDialect.BFEEL), false),
                (left, right) -> Boolean.FALSE);

        // false OR true → true
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.FALSE.equals(left)
                        && Boolean.TRUE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.TRUE);

        // false OR false → false
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.FALSE.equals(left)
                        && Boolean.FALSE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.FALSE);

        // false OR otherwise → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Boolean && Boolean.FALSE.equals(left)
                        && evalRight(right, ctx) == null, false),
                (left, right) -> null);

        // otherwise OR true → true
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && Boolean.TRUE.equals(evalRight(right, ctx)), false),
                (left, right) -> Boolean.TRUE);

        // otherwise OR false → null
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && Boolean.FALSE.equals(evalRight(right, ctx)), false),
                (left, right) -> null);

        // otherwise OR otherwise → null
        map.put(
                new CheckedPredicate((left, right) -> !(left instanceof Boolean) && !dialect.equals(FEELDialect.BFEEL)
                        && evalRight(right, ctx) == null, false),
                (left, right) -> null);

        return map;
    }

    /**
     * Builds the common 'Power of' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Power of' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonPowOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        map.put(
                new CheckedPredicate((left, right) -> true, false),
                (left, right) -> math(left, right, ctx,
                        (l, r) -> BigDecimalMath.pow(l, r, MathContext.DECIMAL128)));
        return map;
    }

    /**
     * Builds the common 'Substraction' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Substraction' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonSubOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // null - Number
        map.put(
                new CheckedPredicate((left, right) -> (left == null && right instanceof Number) || (right == null && left instanceof Number), false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimal(left, ctx);
                    BigDecimal rightNum = getBigDecimal(right, ctx);
                    return leftNum != null && rightNum != null ? leftNum.subtract(rightNum, MathContext.DECIMAL128) : null;
                });

        // Number - Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimal(left, ctx);
                    BigDecimal rightNum = getBigDecimal(right, ctx);
                    return leftNum != null && rightNum != null ? leftNum.subtract(rightNum, MathContext.DECIMAL128) : null;
                });

        // LocalDate - Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Duration, false),
                (left, right) -> {
                    LocalDateTime leftLDT = LocalDateTime.of((LocalDate) left, LocalTime.MIDNIGHT);
                    LocalDateTime evaluated = leftLDT.minus((Duration) right);
                    return LocalDate.of(evaluated.getYear(), evaluated.getMonth(), evaluated.getDayOfMonth());
                });

        // Duration - Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> ((Duration) left).minus((Duration) right));

        // Temporal - Temporal
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof Temporal, false),
                (left, right) -> subtractTemporals((Temporal) left, (Temporal) right, ctx));

        // Temporal - TemporalAmount
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof TemporalAmount, false),
                (left, right) -> ((Temporal) left).minus((TemporalAmount) right));

        // ChronoPeriod - ChronoPeriod
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof ChronoPeriod, false),
                (left, right) -> new ComparablePeriod(((ChronoPeriod) left).minus((ChronoPeriod) right)));

        // left == null || right == null
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> null);

        return map;
    }

    /**
     * Builds the common 'Multiplication' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Multiplication operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonMultOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        FEELDialect dialect = ctx.getFEELDialect();
        // Number * Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal rightNum = getBigDecimal(right, ctx);
                    return leftNum != null && rightNum != null ? leftNum.multiply(rightNum, MathContext.DECIMAL128) : null;
                });

        // Number * Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Duration, false),
                (left, right) -> Duration.ofSeconds(
                        getBigDecimalOrNull(left)
                                .multiply(BigDecimal.valueOf(((Duration) right).getSeconds()), MathContext.DECIMAL128)
                                .longValue()));

        // Number * ChronoPeriod
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof ChronoPeriod, false),
                (left, right) -> ComparablePeriod.ofMonths(
                        getBigDecimalOrNull(left)
                                .multiply(getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128)
                                .intValue()));

        // Duration * Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
                    BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
                    return Duration.ofNanos(durationNumericValue.multiply(rightDecimal).longValue());
                });
        // ChronoPeriod * Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal rightNumber = getBigDecimal(right, ctx);
                    return ComparablePeriod.ofMonths(
                            getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left))
                                    .multiply(rightNumber, MathContext.DECIMAL128)
                                    .intValue());
                });

        // left or right == null
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> null);

        return map;
    }

    /**
     * Builds the common 'Division' operation map used by the dialect handlers.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the common 'Division' operations
     */
    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getCommonDivisionOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Number ÷ Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number
                        && getBigDecimalOrNull(right) != null
                        && getBigDecimalOrNull(right).compareTo(BigDecimal.ZERO) != 0,
                        false),
                (left, right) -> {
                    BigDecimal leftBD = getBigDecimalOrNull(left);
                    BigDecimal rightBD = getBigDecimalOrNull(right);
                    return leftBD.divide(rightBD, MathContext.DECIMAL128);
                });

        // Number ÷ Number , Division by zero case → notify
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number
                        && getBigDecimalOrNull(right) != null
                        && getBigDecimalOrNull(right).compareTo(BigDecimal.ZERO) == 0,
                        true),
                (left, right) -> null);

        // duration ÷ number
        map.put(new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Number, false),
                (left, right) -> {
                    Duration dur = (Duration) left;
                    BigDecimal nanos = BigDecimal.valueOf(dur.toNanos());
                    BigDecimal divisor = getBigDecimalOrNull(right);
                    if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0)
                        return null;
                    BigDecimal scaled = nanos.divide(divisor, 0, RoundingMode.HALF_EVEN);
                    return Duration.ofNanos(scaled.longValue());
                });

        // duration ÷ duration → number
        map.put(new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> {
                    BigDecimal leftSecs = getBigDecimalOrNull(((Duration) left).getSeconds());
                    BigDecimal rightSecs = getBigDecimalOrNull(((Duration) right).getSeconds());
                    if (leftSecs == null || rightSecs == null || rightSecs.compareTo(BigDecimal.ZERO) == 0)
                        return null;
                    return leftSecs.divide(rightSecs, MathContext.DECIMAL128);
                });

        // period ÷ number
        map.put(new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof Number, true),
                (left, right) -> {
                    BigDecimal months = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left));
                    BigDecimal divisor = getBigDecimalOrNull(right);
                    if (months == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0)
                        return null;
                    BigDecimal scaled = months.divide(divisor, MathContext.DECIMAL128);
                    return ComparablePeriod.ofMonths(scaled.intValue());
                });

        // period ÷ period → number
        map.put(new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof ChronoPeriod, false),
                (left, right) -> {
                    BigDecimal leftMonths = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left));
                    BigDecimal rightMonths = getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right));
                    if (leftMonths == null || rightMonths == null || rightMonths.compareTo(BigDecimal.ZERO) == 0)
                        return null;
                    return leftMonths.divide(rightMonths, MathContext.DECIMAL128);
                });

        // left or right == null --> null
        map.put(new CheckedPredicate((left, right) -> left == null || right == null, false), (left, right) -> null);

        return map;
    }

    /**
     * A wrapper around a BiPredicate used to determine whether a given pair of operands
     * Matches a particular operation rule, with an additional flag indicating whether an error
     * notification should be raised when the operation result is null.
     */
    public static class CheckedPredicate {
        final BiPredicate<Object, Object> predicate;
        final boolean toNotify;

        CheckedPredicate(BiPredicate<Object, Object> predicate, boolean toNotify) {
            this.predicate = predicate;
            this.toNotify = toNotify;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof CheckedPredicate))
                return false;
            CheckedPredicate other = (CheckedPredicate) obj;
            return Objects.equals(this.predicate, other.predicate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(predicate);
        }
    }

    @Override
    public Object executeAdd(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getAddOperations(ctx));
    }

    @Override
    public Object executeAnd(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getAndOperations(ctx));
    }

    @Override
    public Object executeEqual(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getEqualOperations(ctx));
    }

    @Override
    public Object executeGte(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getGteOperations(ctx));
    }

    @Override
    public Object executeGt(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getGtOperations(ctx));
    }

    @Override
    public Object executeLte(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getLteOperations(ctx));
    }

    @Override
    public Object executeLt(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getLtOperations(ctx));
    }

    @Override
    public Object executeNotEqual(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getNotEqualOperations(ctx));
    }

    @Override
    public Object executeOr(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getOrOperations(ctx));
    }

    @Override
    public Object executePow(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getPowOperations(ctx));
    }

    @Override
    public Object executeSub(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getSubOperations(ctx));
    }

    @Override
    public Object executeMult(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getMultOperations(ctx));
    }

    @Override
    public Object executeDivision(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getDivisionOperations(ctx));
    }

    /**
     * Executes a binary operation defined in the given operation map against the provided operands.
     * 
     * @param left : the left operand of the operation;
     * @param right : the right operand of the operation;
     * @param ctx : the current EvaluationContext
     * @param operationMap : the map of CheckedPredicate to BiFunction defining available operations
     * @return : the result of applying the matched operation, or null if the operation is undefined or produces no result
     */
    private Object executeOperation(
            Object left,
            Object right,
            EvaluationContext ctx,
            Map<CheckedPredicate, BiFunction<Object, Object, Object>> operationMap) {
        Optional<Map.Entry<CheckedPredicate, BiFunction<Object, Object, Object>>> match =
                operationMap.entrySet().stream()
                        .filter(entry -> entry.getKey().predicate.test(left, right))
                        .findFirst();

        if (match.isPresent()) {
            Object result = match.get().getValue().apply(left, right);
            if (result == null && match.get().getKey().toNotify) {
                commonManageInvalidParameters(ctx);
            }
            return result;
        }
        commonManageInvalidParameters(ctx);
        return null;
    }

    // Evaluate right operand if it’s a node
    static Object evalRight(Object right, EvaluationContext ctx) {
        if (right instanceof InfixOpNode) {
            return ((InfixOpNode) right).evaluate(ctx);
        } else if (right instanceof BaseNode) {
            return ((BaseNode) right).evaluate(ctx);
        } else {
            return right;
        }
    }

    /**
     * Compares left and right operands using the given predicate and returns TRUE/FALSE accordingly
     *
     * @param left
     * @param right
     * @param op
     * @return
     */
    public static Boolean compare(Object left, Object right, BiPredicate<Comparable, Comparable> op, Supplier<Boolean> nullFallback,
            Supplier<Boolean> defaultFallback) {
        if (nullFallback == null || defaultFallback == null) {
            throw new IllegalArgumentException("Fallback suppliers must not be null");
        }
        if (left == null || right == null) {
            return nullFallback.get();
        }
        if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            // periods have special compare semantics in FEEL as it ignores "days". Only months and years are compared
            Long l = ComparablePeriod.toTotalMonths((ChronoPeriod) left);
            Long r = ComparablePeriod.toTotalMonths((ChronoPeriod) right);
            return op.test(l, r);
        }
        if (left instanceof TemporalAccessor && right instanceof TemporalAccessor) {
            // Handle specific cases when both time / datetime
            TemporalAccessor l = (TemporalAccessor) left;
            TemporalAccessor r = (TemporalAccessor) right;
            if (BuiltInTypeUtils.determineTypeFromInstance(left) == BuiltInType.TIME && BuiltInTypeUtils.determineTypeFromInstance(right) == BuiltInType.TIME) {
                return op.test(valuet(l), valuet(r));
            } else if (BuiltInTypeUtils.determineTypeFromInstance(left) == BuiltInType.DATE_TIME && BuiltInTypeUtils.determineTypeFromInstance(right) == BuiltInType.DATE_TIME) {
                return op.test(valuedt(l, r.query(TemporalQueries.zone())), valuedt(r, l.query(TemporalQueries.zone())));
            }
        }
        if (left instanceof Number && right instanceof Number) {
            // Handle specific cases when both are Number, converting both to BigDecimal
            BigDecimal l = getBigDecimalOrNull(left);
            BigDecimal r = getBigDecimalOrNull(right);
            return op.test(l, r);
        }
        // last fallback:
        if ((left instanceof String && right instanceof String) ||
                (left instanceof Boolean && right instanceof Boolean) ||
                (left instanceof Comparable && left.getClass().isAssignableFrom(right.getClass()))) {
            Comparable<?> l = (Comparable<?>) left;
            Comparable<?> r = (Comparable<?>) right;
            return op.test(l, r);
        }
        return defaultFallback.get();
    }

    /**
     * Compares left and right for equality applying FEEL semantics to specific data types
     *
     * @param left : the first object to compare
     * @param right : the second object to compare
     * @param nullFallback : supplier invoked when either argument is null; must not be null
     * @param defaultFallback supplier invoked when no comparison rule applies or comparison fails; must not be null
     * @return : result of the provided fallback suppliers depending on the case
     */
    public static Boolean isEqual(Object left, Object right, Supplier<Boolean> nullFallback, Supplier<Boolean> defaultFallback) {
        if (nullFallback == null || defaultFallback == null) {
            throw new IllegalArgumentException("Fallback suppliers must not be null");
        }
        if (left == null || right == null) {
            return nullFallback.get();
        }

        // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
        // and vice-versa
        if (left instanceof Collection && !(right instanceof Collection) && ((Collection) left).size() == 1) {
            left = ((Collection) left).toArray()[0];
        } else if (right instanceof Collection && !(left instanceof Collection) && ((Collection) right).size() == 1) {
            right = ((Collection) right).toArray()[0];
        }

        if (left instanceof Range && right instanceof Range) {
            return BooleanEvalHelper.isEqual((Range) left, (Range) right);
        } else if (left instanceof Iterable && right instanceof Iterable) {
            return BooleanEvalHelper.isEqual((Iterable) left, (Iterable) right);
        } else if (left instanceof Map && right instanceof Map) {
            return BooleanEvalHelper.isEqual((Map) left, (Map) right);
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            // periods have special compare semantics in FEEL as it ignores "days". Only months and years are compared
            Long l = ComparablePeriod.toTotalMonths((ChronoPeriod) left);
            Long r = ComparablePeriod.toTotalMonths((ChronoPeriod) right);
            return isEqual(l, r, nullFallback, defaultFallback);
        } else if (left instanceof TemporalAccessor && right instanceof TemporalAccessor) {
            // Handle specific cases when both time / datetime
            TemporalAccessor l = (TemporalAccessor) left;
            TemporalAccessor r = (TemporalAccessor) right;
            if (BuiltInTypeUtils.determineTypeFromInstance(left) == BuiltInType.TIME && BuiltInTypeUtils.determineTypeFromInstance(right) == BuiltInType.TIME) {
                return isEqual(DateTimeEvalHelper.valuet(l), DateTimeEvalHelper.valuet(r), nullFallback, defaultFallback);
            } else if (BuiltInTypeUtils.determineTypeFromInstance(left) == BuiltInType.DATE_TIME && BuiltInTypeUtils.determineTypeFromInstance(right) == BuiltInType.DATE_TIME) {
                return isEqual(DateTimeEvalHelper.valuedt(l, r.query(TemporalQueries.zone())), DateTimeEvalHelper.valuedt(r, l.query(TemporalQueries.zone())), nullFallback, defaultFallback);
            } // fallback; continue:
        }
        //return compare(left, right, feelDialect, (l, r) -> l.compareTo(r) == 0);
        // Fallback: Comparable equality
        return compare(left, right,
                (l, r) -> l.compareTo(r) == 0,
                nullFallback,
                defaultFallback);
    }

}
