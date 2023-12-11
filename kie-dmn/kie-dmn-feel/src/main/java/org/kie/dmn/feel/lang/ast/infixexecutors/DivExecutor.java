/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast.infixexecutors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;

public class DivExecutor implements InfixExecutor {

    private static final DivExecutor INSTANCE = new DivExecutor();
//    private final Map<ClassIdentifierTupla, BiFunction<EvaluatedParameters, EvaluationContext, Object>> functionMap;
//
//    private DivExecutor() {
//        functionMap = getFunctionMap();
//    }

    public static DivExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return div(left, right, ctx);
//        return evaluate(new EvaluatedParameters(left, right), ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
//        return evaluate(new EvaluatedParameters(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx)), ctx);
    }

    private Object div(Object left, Object right, EvaluationContext ctx) {
        if (left == null || right == null) {
            return null;
        } else if (left instanceof Duration && right instanceof Number) {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
            return Duration.ofNanos(durationNumericValue.divide(rightDecimal, 0, RoundingMode.HALF_EVEN).longValue());
        } else if (left instanceof Number && right instanceof TemporalAmount) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        } else if (left instanceof Duration && right instanceof Duration) {
            return EvalHelper.getBigDecimalOrNull(((Duration) left).getSeconds()).divide(EvalHelper.getBigDecimalOrNull(((Duration) right).getSeconds()), MathContext.DECIMAL128);
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            final BigDecimal rightDecimal = EvalHelper.getBigDecimalOrNull(right);
            if (rightDecimal.compareTo(BigDecimal.ZERO) == 0) {
                ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.DIVISION_BY_ZERO.getMask()));
                return null;
            } else {
                return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(rightDecimal, MathContext.DECIMAL128).intValue());
            }
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
        } else {
            return math(left, right, ctx, (l, r) -> l.divide(r, MathContext.DECIMAL128));
        }
    }

//    private Object evaluate(EvaluatedParameters params, EvaluationContext ctx) {
//        if (params.getLeft() == null || params.getRight() == null) {
//            return null;
//        }
//        ClassIdentifierTupla identifierTupla = new ClassIdentifierTupla(params.getLeft(), params.getRight());
//        if (functionMap.containsKey(identifierTupla)) {
//            return functionMap.get(identifierTupla).apply(params, ctx);
//        } else {
//            return math(params.getLeft(), params.getRight(), ctx, (l, r) -> l.add(r, MathContext.DECIMAL128));
//        }
//    }
//
//    private Map<ClassIdentifierTupla, BiFunction<EvaluatedParameters, EvaluationContext, Object>> getFunctionMap() {
//        Map<ClassIdentifierTupla, BiFunction<EvaluatedParameters, EvaluationContext, Object>> toReturn = new HashMap<>();
//        toReturn.put(new ClassIdentifierTupla(Duration.class, Number.class), (parameters, ctx) -> {
//            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) parameters.getLeft()).toNanos());
//            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) parameters.getRight()).doubleValue());
//            return Duration.ofNanos(durationNumericValue.divide(rightDecimal, 0, RoundingMode.HALF_EVEN).longValue());
//        });
//        toReturn.put(new ClassIdentifierTupla(Number.class, TemporalAmount.class), (parameters, ctx) -> {
//            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
//            return null;
//        });
//        toReturn.put(new ClassIdentifierTupla(Duration.class, Duration.class), (parameters, ctx) ->
//                EvalHelper.getBigDecimalOrNull(((Duration) parameters.getLeft()).getSeconds()).divide(EvalHelper.getBigDecimalOrNull(((Duration) parameters.getRight()).getSeconds()), MathContext.DECIMAL128));
//        toReturn.put(new ClassIdentifierTupla(ChronoPeriod.class, Number.class), (parameters, ctx) -> {
//            final BigDecimal rightDecimal = EvalHelper.getBigDecimalOrNull(parameters.getRight());
//            if (rightDecimal.compareTo(BigDecimal.ZERO) == 0) {
//                ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.DIVISION_BY_ZERO.getMask()));
//                return null;
//            } else {
//                return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).divide(rightDecimal, MathContext.DECIMAL128).intValue());
//            }
//        });
//        toReturn.put(new ClassIdentifierTupla(ChronoPeriod.class, ChronoPeriod.class), (parameters, ctx) ->
//                EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getRight())), MathContext.DECIMAL128));
//        return toReturn;
//    }
}
