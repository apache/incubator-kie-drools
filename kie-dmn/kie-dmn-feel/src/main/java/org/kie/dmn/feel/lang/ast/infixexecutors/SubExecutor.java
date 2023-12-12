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

import java.math.MathContext;
import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.subtractTemporals;

public class SubExecutor implements InfixExecutor {

    private static final SubExecutor INSTANCE = new SubExecutor();
    private final Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> subFunctionsByClassesTuple;

    private SubExecutor() {
        subFunctionsByClassesTuple = getSubFunctionsByClassesTuple();
    }

    public static SubExecutor instance() {
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
        if (subFunctionsByClassesTuple.containsKey(identifierTuple)) {
            return subFunctionsByClassesTuple.get(identifierTuple).apply(params, ctx);
        } else {
            return math(params.getLeft(), params.getRight(), ctx, (l, r) -> l.subtract(r, MathContext.DECIMAL128));
        }
    }

    private Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> getSubFunctionsByClassesTuple() {
        Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> toReturn = new HashMap<>();
        toReturn.put(new ClassIdentifierTuple(Temporal.class, Temporal.class), (parameters, ctx) ->
                subtractTemporals((Temporal) parameters.getLeft(), (Temporal) parameters.getRight(), ctx));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, ChronoPeriod.class), (parameters, ctx) ->
                new ComparablePeriod(((ChronoPeriod) parameters.getLeft()).minus((ChronoPeriod) parameters.getRight())));
        toReturn.put(new ClassIdentifierTuple(Duration.class, Duration.class), (parameters, ctx) ->
                ((Duration) parameters.getLeft()).minus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(ZonedDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getLeft()).minus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(OffsetDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getLeft()).minus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getLeft()).minus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDate.class, ChronoPeriod.class), (parameters, ctx) ->
                ((LocalDate) parameters.getLeft()).minus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(ZonedDateTime.class, Duration.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getLeft()).minus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(OffsetDateTime.class, Duration.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getLeft()).minus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDateTime.class, Duration.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getLeft()).minus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDate.class, Duration.class), (parameters, ctx) -> {
            LocalDateTime leftLDT = LocalDateTime.of((LocalDate) parameters.getLeft(), LocalTime.MIDNIGHT);
            LocalDateTime evaluated = leftLDT.minus((Duration) parameters.getRight());
            return LocalDate.of(evaluated.getYear(), evaluated.getMonth(), evaluated.getDayOfMonth());
        });
        toReturn.put(new ClassIdentifierTuple(LocalTime.class, Duration.class), (parameters, ctx) ->
                ((LocalTime) parameters.getLeft()).minus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(OffsetTime.class, Duration.class), (parameters, ctx) ->
                ((OffsetTime) parameters.getLeft()).minus((Duration) parameters.getRight()));
        return toReturn;
    }
}
