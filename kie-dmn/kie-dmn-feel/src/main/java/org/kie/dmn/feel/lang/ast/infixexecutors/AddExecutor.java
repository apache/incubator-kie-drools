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
import org.kie.dmn.feel.util.Msg;

import java.math.MathContext;
import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.addLocalDateAndDuration;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;

public class AddExecutor implements InfixExecutor {

    private static final AddExecutor INSTANCE = new AddExecutor();
    private final Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> addFunctionsByClassesTuple;

    private AddExecutor() {
        addFunctionsByClassesTuple = getAddFunctionsByClassesTuple();
    }

    public static AddExecutor instance() {
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
        if (addFunctionsByClassesTuple.containsKey(identifierTuple)) {
            return addFunctionsByClassesTuple.get(identifierTuple).apply(params, ctx);
        } else {
            return math(params.getLeft(), params.getRight(), ctx, (l, r) -> l.add(r, MathContext.DECIMAL128));
        }
    }

    private Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> getAddFunctionsByClassesTuple() {
        Map<ClassIdentifierTuple, BiFunction<EvaluatedParameters, EvaluationContext, Object>> toReturn = new HashMap<>();
        toReturn.put(new ClassIdentifierTuple(String.class, String.class), (parameters, ctx) -> parameters.getLeft() + (String) parameters.getRight());
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, ChronoPeriod.class), (parameters, ctx) ->
                new ComparablePeriod(((ChronoPeriod) parameters.getLeft()).plus((ChronoPeriod) parameters.getRight())));
        toReturn.put(new ClassIdentifierTuple(Duration.class, Duration.class), (parameters, ctx) ->
                ((Duration) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(ZonedDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getLeft()).plus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(OffsetDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getLeft()).plus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDateTime.class, ChronoPeriod.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getLeft()).plus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDate.class, ChronoPeriod.class), (parameters, ctx) ->
                ((LocalDate) parameters.getLeft()).plus((ChronoPeriod) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(ZonedDateTime.class, Duration.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(OffsetDateTime.class, Duration.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDateTime.class, Duration.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(LocalDate.class, Duration.class), (parameters, ctx) ->
                addLocalDateAndDuration((LocalDate) parameters.getLeft(), (Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, ZonedDateTime.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getRight()).plus((ChronoPeriod) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, OffsetDateTime.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getRight()).plus((ChronoPeriod) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, LocalDateTime.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getRight()).plus((ChronoPeriod) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(ChronoPeriod.class, LocalDate.class), (parameters, ctx) ->
                ((LocalDate) parameters.getRight()).plus((ChronoPeriod) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, ZonedDateTime.class), (parameters, ctx) ->
                ((ZonedDateTime) parameters.getRight()).plus((Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, OffsetDateTime.class), (parameters, ctx) ->
                ((OffsetDateTime) parameters.getRight()).plus((Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, LocalDateTime.class), (parameters, ctx) ->
                ((LocalDateTime) parameters.getRight()).plus((Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, LocalDate.class), (parameters, ctx) ->
                addLocalDateAndDuration((LocalDate) parameters.getRight(), (Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(LocalTime.class, Duration.class), (parameters, ctx) ->
                ((LocalTime) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, LocalTime.class), (parameters, ctx) ->
                ((LocalTime) parameters.getRight()).plus((Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(OffsetTime.class, Duration.class), (parameters, ctx) ->
                ((OffsetTime) parameters.getLeft()).plus((Duration) parameters.getRight()));
        toReturn.put(new ClassIdentifierTuple(Duration.class, OffsetTime.class), (parameters, ctx) ->
                ((OffsetTime) parameters.getRight()).plus((Duration) parameters.getLeft()));
        toReturn.put(new ClassIdentifierTuple(Temporal.class, Temporal.class), (parameters, ctx) -> {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        });
        return toReturn;
    }
}
