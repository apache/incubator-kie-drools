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
import java.util.function.BiPredicate;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.*;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public class BFEELDialectHandler extends DefaultDialectHandler implements DialectHandler {

    @Override
    public Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> getAddOperationMap(EvaluationContext ctx) {
        Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // coerce to String if either is a String
        map.put(
                (left, right) -> left instanceof String || right instanceof String,
                (left, right) -> getString(left) + getString(right)
        );
        // null + Number → treat null as 0
        map.put(
                (left, right) -> left == null && right instanceof Number,
                (left, right) -> {
                    BigDecimal leftNum = BigDecimal.ZERO;
                    BigDecimal rightNum = getBigDecimalOrNull(right);
                    return leftNum.add(rightNum, MathContext.DECIMAL128);
                }
        );
        // Number + null → treat null as 0
        map.put(
                (left, right) -> left instanceof Number && right == null,
                (left, right) -> {
                    BigDecimal leftNum = getBigDecimalOrNull(left);
                    BigDecimal rightNum = BigDecimal.ZERO;
                    return leftNum.add(rightNum, MathContext.DECIMAL128);
                }
        );
        // (Number + String) → coerce both to string
        map.put(
                (left, right) -> (left instanceof Number && right instanceof String) || (left instanceof String && right instanceof Number),
                (left, right) -> getString(left) + getString(right)
        );
        // Temporal + Number → treat non-numeric as 0
        map.put(
                (left, right) -> left instanceof Temporal && right instanceof Number,
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
                (left, right) -> left instanceof Temporal && !(right instanceof TemporalAmount),
                (left, right) -> ((Temporal) left).plus(Duration.ZERO)
        );
        // TemporalAmount + non-Temporal → treat as no-op
        map.put(
                (left, right) -> left instanceof TemporalAmount && !(right instanceof Temporal || right instanceof ChronoPeriod),
                (left, right) -> left
        );

        map.putAll(getCommonAddOperations());

        // null + any → concatenate as strings
        map.put(
                (left, right) -> left == null || right == null,
                (left, right) -> getString(left) + getString(right)
        );
        return map;
    }
}
