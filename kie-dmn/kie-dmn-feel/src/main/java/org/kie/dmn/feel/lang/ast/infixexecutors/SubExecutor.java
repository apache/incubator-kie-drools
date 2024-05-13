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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.subtractTemporals;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public class SubExecutor implements InfixExecutor {

    private static final SubExecutor INSTANCE = new SubExecutor();

    private SubExecutor() {
    }

    public static SubExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return sub(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    private Object sub(Object left, Object right, EvaluationContext ctx) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Number) {
            BigDecimal leftNumber = getBigDecimalOrNull(left);
            return leftNumber != null && right instanceof Number ?
                    leftNumber.subtract(getBigDecimalOrNull(right), MathContext.DECIMAL128) :
                    null;
        }

        if (right instanceof Duration) {
            if (left instanceof LocalDate) {
                LocalDateTime leftLDT = LocalDateTime.of((LocalDate) left, LocalTime.MIDNIGHT);
                LocalDateTime evaluated = leftLDT.minus((Duration) right);
                return LocalDate.of(evaluated.getYear(), evaluated.getMonth(), evaluated.getDayOfMonth());
            }
            if (left instanceof Duration) {
                return ((Duration) left).minus((Duration) right);
            }
        }

        if (left instanceof Temporal) {
            if (right instanceof Temporal) {
                return subtractTemporals((Temporal) left, (Temporal) right, ctx);
            }
            if (right instanceof TemporalAmount) {
                return ((Temporal) left).minus((TemporalAmount) right);
            }
        }

        if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return new ComparablePeriod(((ChronoPeriod) left).minus((ChronoPeriod) right));
        }

        return null;
    }
}
