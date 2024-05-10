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
import java.math.RoundingMode;
import java.time.Duration;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.TemporalAmount;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.Msg;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public class DivExecutor implements InfixExecutor {

    private static final DivExecutor INSTANCE = new DivExecutor();

    private DivExecutor() {
    }

    public static DivExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return div(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    private Object div(Object left, Object right, EvaluationContext ctx) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Number) {
            if (right instanceof Number) {
                return math(left, right, ctx, (l, r) -> l.divide(r, MathContext.DECIMAL128));
            }
            if (right instanceof TemporalAmount) {
                ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            }
            return null;
        }

        if (left instanceof Duration) {
            if (right instanceof Number) {
                final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
                final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
                return Duration.ofNanos(durationNumericValue.divide(rightDecimal, 0, RoundingMode.HALF_EVEN).longValue());
            }
            if (right instanceof Duration) {
                return getBigDecimalOrNull(((Duration) left).getSeconds()).divide(getBigDecimalOrNull(((Duration) right).getSeconds()), MathContext.DECIMAL128);
            }
        }

        if (left instanceof ChronoPeriod) {
            if (right instanceof Number) {
                final BigDecimal rightDecimal = getBigDecimalOrNull(right);
                if (rightDecimal.compareTo(BigDecimal.ZERO) == 0) {
                    ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.DIVISION_BY_ZERO.getMask()));
                    return null;
                } else {
                    return ComparablePeriod.ofMonths(getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(rightDecimal, MathContext.DECIMAL128).intValue());
                }
            }
            if (right instanceof ChronoPeriod) {
                return getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
            }
        }

        return null;
    }
}
