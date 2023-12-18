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
import java.time.chrono.ChronoPeriod;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.isAllowedMultiplicationBasedOnSpec;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;

public class MultExecutor implements InfixExecutor {

    private static final MultExecutor INSTANCE = new MultExecutor();

    private MultExecutor() {
    }

    public static MultExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return mult(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    private Object mult(Object left, Object right, EvaluationContext ctx) {
        if (left == null || right == null) {
            return null;
        } else if (!isAllowedMultiplicationBasedOnSpec(left, right, ctx)) {
            return null;
        } else if (left instanceof Duration && right instanceof Number) {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
            return Duration.ofNanos(durationNumericValue.multiply(rightDecimal).longValue());
        } else if (left instanceof Number && right instanceof Duration) {
            return Duration.ofSeconds(EvalHelper.getBigDecimalOrNull(left).multiply(EvalHelper.getBigDecimalOrNull(((Duration) right).getSeconds()), MathContext.DECIMAL128).longValue());
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(EvalHelper.getBigDecimalOrNull(right), MathContext.DECIMAL128).intValue());
        } else if (left instanceof Number && right instanceof ChronoPeriod) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(left).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128).intValue());
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
        } else {
            return math(left, right, ctx, (l, r) -> l.multiply(r, MathContext.DECIMAL128));
        }
    }
}
