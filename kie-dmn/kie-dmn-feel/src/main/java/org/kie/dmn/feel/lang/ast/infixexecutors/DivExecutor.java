/**
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
    private final Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> sumFunctionsByClassesTuple;

    private DivExecutor() {
        sumFunctionsByClassesTuple = getSumFunctionsByClassesTuple();
    }

    public static DivExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return evaluate(new EvaluatedParameters(left, right), ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    private Object evaluate(EvaluatedParameters params, EvaluationContext ctx) {
        if (params.getLeft() == null || params.getRight() == null) {
            return null;
        }
        ClassIdentifierTuple identifierTuple = new ClassIdentifierTuple(params.getLeft(), params.getRight());
        if (sumFunctionsByClassesTuple.containsKey(identifierTuple)) {
            return sumFunctionsByClassesTuple.get(identifierTuple).apply(params, ctx);
        } else {
            return math(params.getLeft(), params.getRight(), ctx, (l, r) -> l.divide(r, MathContext.DECIMAL128));
        }
    }

    private Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> getSumFunctionsByClassesTuple() {
        Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> toReturn = new HashMap<>();
        toReturn.put(new ClassIdentifierTuple(Duration.class, Number.class), (parameters, ctx) -> {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) parameters.getLeft()).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) parameters.getRight()).doubleValue());
            return Duration.ofNanos(durationNumericValue.divide(rightDecimal, 0, RoundingMode.HALF_EVEN).longValue());
        });
        toReturn.put(new ClassIdentifierTuple(Number.class, TemporalAmount.class), (parameters, ctx) -> {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        });
        toReturn.put(new ClassIdentifierTuple(Duration.class, Duration.class), (parameters, ctx) ->
                EvalHelper.getBigDecimalOrNull(((Duration) parameters.getLeft()).getSeconds()).divide(EvalHelper.getBigDecimalOrNull(((Duration) parameters.getRight()).getSeconds()), MathContext.DECIMAL128));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, Number.class), (parameters, ctx) -> {
            final BigDecimal rightDecimal = EvalHelper.getBigDecimalOrNull(parameters.getRight());
            if (rightDecimal.compareTo(BigDecimal.ZERO) == 0) {
                ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.DIVISION_BY_ZERO.getMask()));
                return null;
            } else {
                return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).divide(rightDecimal, MathContext.DECIMAL128).intValue());
            }
        });
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, ChronoPeriod.class), (parameters, ctx) ->
                EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getRight())), MathContext.DECIMAL128));
        return toReturn;
    }
}
