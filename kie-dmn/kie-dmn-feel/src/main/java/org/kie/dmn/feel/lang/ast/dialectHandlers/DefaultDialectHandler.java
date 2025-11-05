package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.Msg;

import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.*;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public abstract class DefaultDialectHandler implements DialectHandler {

    protected Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> getCommonAddOperations() {
        Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // String + String → concatenate
        map.put(
                (left, right) -> left instanceof String && right instanceof String,
                (left, right) -> getString(left) + getString(right)
        );
        // Number + Number
        map.put(
                (left, right) -> left instanceof Number && right instanceof Number,
                (left, right) -> getBigDecimalOrNull(left).add(getBigDecimalOrNull(right), MathContext.DECIMAL128)
        );
        // Duration + Duration → Duration
        map.put(
                (left, right) -> left instanceof Duration && right instanceof Duration,
                (left, right) -> ((Duration) left).plus((Duration) right)
        );
        // Duration + LocalDate → LocalDate
        map.put(
                (left, right) -> left instanceof Duration && right instanceof LocalDate,
                (left, right) -> {
                    Duration leftDuration = (Duration) left;
                    LocalDate localDate = (LocalDate) right;
                    return addLocalDateAndDuration(localDate, leftDuration);
                }
        );
        // LocalDate + Duration → LocalDate
        map.put(
                (left, right) -> left instanceof LocalDate && right instanceof Duration,
                (left, right) -> {
                    LocalDate localDate = (LocalDate) left;
                    Duration duration = (Duration) right;
                    return addLocalDateAndDuration(localDate, duration);
                }
        );

        // Temporal + TemporalAmount → normal addition
        map.put(
                (left, right) -> left instanceof Temporal && right instanceof TemporalAmount,
                (left, right) -> ((Temporal) left).plus((TemporalAmount) right)
        );
        //TemporalAmount +Temporal
        map.put(
                (left, right) -> left instanceof TemporalAmount && right instanceof Temporal,
                (left, right) -> ((Temporal) right).plus((TemporalAmount) left)
        );

        // TemporalAmount + ChronoPeriod → combine periods
        map.put(
                (left, right) -> left instanceof TemporalAmount && right instanceof ChronoPeriod,
                (left, right) -> ((ChronoPeriod) right).plus((TemporalAmount) left)
        );
        return map;
    }

    @Override
    public Object executeAdd(Object left, Object right, EvaluationContext ctx) {
        List<BiPredicate<Object, Object> > notifiedPredicates = getNotifiedPredicates();
        /*for (Map.Entry<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> entry : getAddOperationMap(ctx).entrySet()) {
            if (entry.getKey().test(left, right)) {
                Object result = entry.getValue().apply(left, right);
                if (result == null && notifiedPredicates.contains(entry.getKey())) {
                    ctx.notifyEvt(() -> new InvalidParametersEvent(
                            FEELEvent.Severity.ERROR,
                            Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()
                    ));
                }
                return result;
            }
        }
        FEELEvent.Severity severity = FEELEvent.Severity.ERROR;
        ctx.notifyEvt(() -> new InvalidParametersEvent(severity, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
        return null;*/

        Optional<Map.Entry<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>>> match =
                getAddOperationMap(ctx).entrySet().stream()
                        .filter(entry -> entry.getKey().test(left, right))
                        .findFirst();
        if (match.isPresent()) {
            Map.Entry<BiPredicate<Object, Object>, BiFunction<Object, Object, Object>> entry = match.get();
            Object result = entry.getValue().apply(left, right);
            if (result == null && notifiedPredicates.contains(entry.getKey())) {
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
