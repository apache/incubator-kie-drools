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

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.isAllowedMultiplicationBasedOnSpec;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;

public class MultExecutor implements InfixExecutor {

    private static final MultExecutor INSTANCE = new MultExecutor();
    private final Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> multFunctionsByClassesTuple;

    private MultExecutor() {
        multFunctionsByClassesTuple = getMultFunctionsByClassesTuple();
    }

    public static MultExecutor instance() {
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
        if ( params.getLeft() == null || params.getRight() == null ) {
            return null;
        } else if (!isAllowedMultiplicationBasedOnSpec(params.getLeft(), params.getRight(), ctx)) {
            return null;
        }
        ClassIdentifierTuple identifierTuple = new ClassIdentifierTuple(params.getLeft(), params.getRight());
        if (multFunctionsByClassesTuple.containsKey(identifierTuple)) {
            return multFunctionsByClassesTuple.get(identifierTuple).apply(params, ctx);
        } else {
            return math( params.getLeft(), params.getRight(), ctx, (l, r) -> l.multiply( r, MathContext.DECIMAL128 ) );
        }
    }

    private Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> getMultFunctionsByClassesTuple() {
        Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> toReturn = new HashMap<>();
        toReturn.put(new ClassIdentifierTuple(Duration.class, Number.class), (parameters, ctx) -> {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) parameters.getLeft()).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) parameters.getRight()).doubleValue());
            return Duration.ofNanos(durationNumericValue.multiply(rightDecimal).longValue());
        });
        toReturn.put(new ClassIdentifierTuple(Number.class, Duration.class), (parameters, ctx) ->
                Duration.ofSeconds(EvalHelper.getBigDecimalOrNull(parameters.getLeft()).multiply(EvalHelper.getBigDecimalOrNull(((Duration) parameters.getRight()).getSeconds()), MathContext.DECIMAL128).longValue()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, Number.class), (parameters, ctx) ->
                ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).multiply(EvalHelper.getBigDecimalOrNull(parameters.getRight()), MathContext.DECIMAL128).intValue()));
        toReturn.put(new ClassIdentifierTuple(Number.class, ChronoPeriod.class), (parameters, ctx) ->
                ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(parameters.getLeft()).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getRight())), MathContext.DECIMAL128).intValue()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, ChronoPeriod.class), (parameters, ctx) ->
                EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getLeft())).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) parameters.getRight())), MathContext.DECIMAL128));
        return toReturn;
    }
}
