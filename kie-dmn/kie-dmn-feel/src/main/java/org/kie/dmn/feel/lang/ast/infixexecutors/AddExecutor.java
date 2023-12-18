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

import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.Msg;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.addLocalDateAndDuration;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;

public class AddExecutor implements InfixExecutor {

    private static final AddExecutor INSTANCE = new AddExecutor();

    private AddExecutor() {
    }

    public static AddExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return add(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    private Object add(Object left, Object right, EvaluationContext ctx) {
        if (left == null || right == null) {
            return null;
        } else if (left instanceof String && right instanceof String) {
            return left + ((String) right);
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return new ComparablePeriod(((ChronoPeriod) left).plus((ChronoPeriod) right));
        } else if (left instanceof Duration && right instanceof Duration) {
            return ((Duration) left).plus((Duration) right);
        } else if (left instanceof ZonedDateTime && right instanceof ChronoPeriod) {
            return ((ZonedDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof OffsetDateTime && right instanceof ChronoPeriod) {
            return ((OffsetDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof LocalDateTime && right instanceof ChronoPeriod) {
            return ((LocalDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof LocalDate && right instanceof ChronoPeriod) {
            return ((LocalDate) left).plus((ChronoPeriod) right);
        } else if (left instanceof ZonedDateTime && right instanceof Duration) {
            return ((ZonedDateTime) left).plus((Duration) right);
        } else if (left instanceof OffsetDateTime && right instanceof Duration) {
            return ((OffsetDateTime) left).plus((Duration) right);
        } else if (left instanceof LocalDateTime && right instanceof Duration) {
            return ((LocalDateTime) left).plus((Duration) right);
        } else if (left instanceof LocalDate && right instanceof Duration) {
            return addLocalDateAndDuration((LocalDate) left, (Duration) right);
        } else if (left instanceof ChronoPeriod && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof LocalDateTime) {
            return ((LocalDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof LocalDate) {
            return ((LocalDate) right).plus((ChronoPeriod) left);
        } else if (left instanceof Duration && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) right).plus((Duration) left);
        } else if (left instanceof Duration && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) right).plus((Duration) left);
        } else if (left instanceof Duration && right instanceof LocalDateTime) {
            return ((LocalDateTime) right).plus((Duration) left);
        } else if (left instanceof Duration && right instanceof LocalDate) {
            return addLocalDateAndDuration((LocalDate) right, (Duration) left);
        } else if (left instanceof LocalTime && right instanceof Duration) {
            return ((LocalTime) left).plus((Duration) right);
        } else if (left instanceof Duration && right instanceof LocalTime) {
            return ((LocalTime) right).plus((Duration) left);
        } else if (left instanceof OffsetTime && right instanceof Duration) {
            return ((OffsetTime) left).plus((Duration) right);
        } else if (left instanceof Duration && right instanceof OffsetTime) {
            return ((OffsetTime) right).plus((Duration) left);
        } else if (left instanceof Temporal && right instanceof Temporal) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        } else {
            return math(left, right, ctx, (l, r) -> l.add(r, MathContext.DECIMAL128));
        }
    }
}
