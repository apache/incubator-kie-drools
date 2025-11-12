package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.*;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public class BFEELDialectHandler extends DefaultDialectHandler implements DialectHandler {

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperationMap(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // coerce to String if either is a String
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> getString(left) + getString(right)
        );
        // null + Number → treat null as 0
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal leftNum = BigDecimal.ZERO;
                    BigDecimal rightNum = getBigDecimalOrNull(right);
                    return leftNum.add(rightNum, MathContext.DECIMAL128);
                }
        );
        // Number + null → treat null as 0
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right == null, false),
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal rightNum = BigDecimal.ZERO;
                    return leftNum.add(rightNum, MathContext.DECIMAL128);
                }
        );
        // (Number + String) → coerce both to string
        map.put(
                new CheckedPredicate((left, right) -> (left instanceof Number && right instanceof String) || (left instanceof String && right instanceof Number), false),
                (left, right) -> getString(left) + getString(right)
        );
        // Temporal + Number → treat non-numeric as 0
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof Number, false),
                (left, right) -> {
                    BigDecimal leftNumber = left instanceof Number
                            ? getBigDecimalOrNull(left)
                            : BigDecimal.ZERO;
                    BigDecimal rightNumber = getBigDecimalOrNull(right);
                    return (leftNumber != null && rightNumber != null)
                            ? leftNumber.add(rightNumber, MathContext.DECIMAL128)
                            : null;
                }
        );
        // Temporal + non-TemporalAmount → treat as Duration.ZERO
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && !(right instanceof TemporalAmount), false),
                (left, right) -> ((Temporal) left).plus(Duration.ZERO)
        );
        // TemporalAmount + non-Temporal → treat as no-op
        map.put(
                new CheckedPredicate((left, right) -> left instanceof TemporalAmount && !(right instanceof Temporal || right instanceof ChronoPeriod), false),
                (left, right) -> left
        );

        map.putAll(getCommonAddOperations());

        // null + any → concatenate as strings
        map.put(
                new CheckedPredicate((left, right) -> left == null || right == null, false),
                (left, right) -> getString(left) + getString(right)
        );
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAndOperationMap(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonAndOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getEqualOperationMap(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonEqualOperationMap(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGteOperationMap(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonGteOperationMap(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGtOperationMap(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonGtOperationMap(ctx));
    }
}
