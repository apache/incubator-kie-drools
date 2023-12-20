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

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.Msg;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.addLocalDateAndDuration;
import static org.kie.dmn.feel.util.EvalHelper.getBigDecimalOrNull;

public class AddExecutor implements InfixExecutor {

    private static final AddExecutor INSTANCE = new AddExecutor();

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
        }

        if (left instanceof Number) {
            BigDecimal leftNumber = getBigDecimalOrNull(left);
            return leftNumber != null && right instanceof Number ?
                    leftNumber.add(getBigDecimalOrNull(right), MathContext.DECIMAL128) :
                    null;
        }

        if (left instanceof String) {
            return right instanceof String ? left + (String) right : null;
        }

        if (left instanceof Duration) {
            if (right instanceof LocalDate) {
                return addLocalDateAndDuration((LocalDate) right, (Duration) left);
            }
            if (right instanceof Duration) {
                return ((Duration) left).plus((Duration) right);
            }
        }
        if (right instanceof Duration && left instanceof LocalDate) {
            return addLocalDateAndDuration((LocalDate) left, (Duration) right);
        }

        if (left instanceof Temporal) {
            if (right instanceof TemporalAmount) {
                return ((Temporal) left).plus((TemporalAmount) right);
            }
        } else if (left instanceof TemporalAmount) {
            if (right instanceof Temporal) {
                return ((Temporal) right).plus((TemporalAmount) left);
            }
            if (right instanceof ChronoPeriod) {
                return ((ChronoPeriod) right).plus((TemporalAmount) left);
            }
        }

        ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
        return null;
    }


}
