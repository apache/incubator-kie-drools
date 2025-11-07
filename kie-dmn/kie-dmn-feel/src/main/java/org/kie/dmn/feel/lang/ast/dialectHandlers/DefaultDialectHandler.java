package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.BooleanEvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.*;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public abstract class DefaultDialectHandler implements DialectHandler {

    protected Map<CheckedPredicate, BiFunction<Object, Object, Object>> getCommonAddOperations() {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // String + String → concatenate
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String && right instanceof String, false),
                (left, right) -> getString(left) + getString(right)
        );
        // Number + Number
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Number, false),
                (left, right) -> getBigDecimalOrNull(left).add(getBigDecimalOrNull(right), MathContext.DECIMAL128)
        );
        // Duration + Duration → Duration
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, false),
                (left, right) -> ((Duration) left).plus((Duration) right)
        );
        // Duration + LocalDate → LocalDate
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof LocalDate, false),
                (left, right) -> {
                    Duration leftDuration = (Duration) left;
                    LocalDate localDate = (LocalDate) right;
                    return addLocalDateAndDuration(localDate, leftDuration);
                }
        );
        // LocalDate + Duration → LocalDate
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Duration, false),
                (left, right) -> {
                    LocalDate localDate = (LocalDate) left;
                    Duration duration = (Duration) right;
                    return addLocalDateAndDuration(localDate, duration);
                }
        );

        // Temporal + TemporalAmount → normal addition
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof TemporalAmount, false),
                (left, right) -> ((Temporal) left).plus((TemporalAmount) right)
        );
        //TemporalAmount +Temporal
        map.put(
                new CheckedPredicate((left, right) -> left instanceof TemporalAmount && right instanceof Temporal, false),
                (left, right) -> ((Temporal) right).plus((TemporalAmount) left)
        );

        // TemporalAmount + ChronoPeriod → combine periods
        map.put(
                new CheckedPredicate((left, right) -> left instanceof TemporalAmount && right instanceof ChronoPeriod, false),
                (left, right) -> ((ChronoPeriod) right).plus((TemporalAmount) left)
        );
        return map;
    }


    protected Map<CheckedPredicate, BiFunction<Object, Object, Object>> getCommonAndOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        FEELDialect dialect = ctx.getFEELDialect();
        // Even though AndExecutor short-circuits right-side evaluation,
        // this rule ensures correct logical interpretation when both operands are passed
        // left is Boolean false → false
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean l = BooleanEvalHelper.getBooleanOrDialectDefault(left, dialect);
                    return Boolean.FALSE.equals(l);
                }, false),
                (left, right) -> Boolean.FALSE
        );
        // left is Boolean true → evaluate right
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean l = BooleanEvalHelper.getBooleanOrDialectDefault(left, dialect);
                    return Boolean.TRUE.equals(l);
                }, false),
                (left, right) -> {
                    Boolean r = BooleanEvalHelper.getBooleanOrDialectDefault(right, dialect);
                    return r != null ? r : BooleanEvalHelper.getFalseOrDialectDefault(right, dialect);
                }
        );

        // left is null → fallback to getFalseOrDialectDefault(right)
        map.put(
                new CheckedPredicate((left, right) -> {
                    Boolean l = BooleanEvalHelper.getBooleanOrDialectDefault(left, dialect);
                    return l == null;
                }, false),
                (left, right) -> BooleanEvalHelper.getFalseOrDialectDefault(right, dialect)
        );

        return map;
    }

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
        return executeOperation(left, right, ctx, getAddOperationMap(ctx));
    }

    @Override
    public Object executeAnd(Object left, Object right, EvaluationContext ctx) {
        return executeOperation(left, right, ctx, getAndOperationMap(ctx));
    }

    private Object executeOperation(
            Object left,
            Object right,
            EvaluationContext ctx,
            Map<CheckedPredicate, BiFunction<Object, Object, Object>> operationMap
    ) {
        Optional<Map.Entry<CheckedPredicate, BiFunction<Object, Object, Object>>> match =
                operationMap.entrySet().stream()
                        .filter(entry -> entry.getKey().predicate.test(left, right))
                        .findFirst();

        if (match.isPresent()) {
            Object result = match.get().getValue().apply(left, right);
            if (result == null && match.get().getKey().toNotify) {
                ctx.notifyEvt(() -> new InvalidParametersEvent(
                        FEELEvent.Severity.ERROR,
                        Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()
                ));
            }
            return result;
        }

        ctx.notifyEvt(() -> new InvalidParametersEvent(
                FEELEvent.Severity.ERROR,
                Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()
        ));
        return null;
    }
}
