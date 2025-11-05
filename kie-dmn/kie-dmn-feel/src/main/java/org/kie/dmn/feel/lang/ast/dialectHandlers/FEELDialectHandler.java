package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class FEELDialectHandler extends DefaultDialectHandler implements DialectHandler {

    private static final BiPredicate<Object, Object> DATE_PLUS_NUMBER = (left, right) -> left instanceof LocalDate && right instanceof Number;
    private static final BiPredicate<Object, Object> TEMPORAL_PLUS_NUMBER = (left, right) -> left instanceof Temporal && right instanceof Number;
    private static final BiPredicate<Object, Object> TEMPORAL_PLUS_TEMPORAL = (left, right) -> left instanceof Temporal && right instanceof Temporal;

    @Override
    public Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> getAddOperationMap(EvaluationContext ctx) {
        Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // String + null or null + String → return null
        map.put(
                (left, right) -> (left instanceof String && right == null) || (right instanceof String && left == null),
                (left, right) -> null
        );
        // null + Number or Number + null
        map.put(
                (left, right) -> (left == null && right instanceof Number) || (left instanceof Number && right == null),
                (left, right) -> null
        );
        //(Number + String) or (String + Number) → return null
        map.put(
                (left, right) -> (left instanceof Number && right instanceof String) || (left instanceof String && right instanceof Number),
                (left, right) -> null
        );

        //temporal + number
        map.put(
                TEMPORAL_PLUS_NUMBER,
                (left, right) -> null
        );

        // TemporalAmount + String → invalid
        map.put(
                (left, right) -> left instanceof TemporalAmount && right instanceof String,
                (left, right) -> null
        );
        map.put(
                TEMPORAL_PLUS_TEMPORAL,
                (left, right) -> null
        );
        // Temporal + non-TemporalAmount → invalid
        map.put(
                (left, right) -> left instanceof Temporal && !(right instanceof TemporalAmount || right instanceof Number),
                (left, right) -> null
        );

        // TemporalAmount + non-Temporal → invalid
        map.put(
                (left, right) -> left instanceof TemporalAmount && !(right instanceof Temporal || right instanceof ChronoPeriod || right instanceof Duration),
                (left, right) -> null
        );
        // LocalDate + Number → invalid
        map.put(
                DATE_PLUS_NUMBER, (left, right) -> null
        );

        // LocalDateTime + Number → invalid
        map.put(
                (left, right) -> left instanceof LocalDateTime && right instanceof Number,
                (left, right) -> null
        );
        // Temporal + Number → invalid
        map.put(
                (left, right) -> left instanceof Temporal && right instanceof Number,
                (left, right) -> null
        );

        // String + String -> concat string
        map.put(
                (left, right) -> left instanceof String && right instanceof String,
                (left, right) -> ((String) left) + ((String) right)
        );

        //Adding default rules
        map.putAll(getCommonAddOperations());

        // null + any → only concatenate if both are strings
        map.put(
                (left, right) -> left == null || right == null,
                (left, right) -> {
                    if (left instanceof String stringLeft && right instanceof String stringRight) {
                        return stringLeft + stringRight;
                    }
                    return null;
                }
        );

        return map;
    }

    @Override
    public List<BiPredicate<Object, Object>> getNotifiedPredicates() {
        return List.of(
                DATE_PLUS_NUMBER,
                TEMPORAL_PLUS_NUMBER,
                TEMPORAL_PLUS_TEMPORAL
        );
    }
}
