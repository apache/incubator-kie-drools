/*
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
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandler;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandlerFactory;

public class MultExecutor implements InfixExecutor {

    private static final MultExecutor INSTANCE = new MultExecutor();

    private MultExecutor() {
    }

    public static MultExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        return handler.executeMult(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    // TODO To be removed
    /*private Object mult(Object left, Object right, EvaluationContext ctx) {
        if (!isAllowedMultiplicationBasedOnSpec(left, right, ctx)) {
            return null;
        }

        if ((left instanceof Number && right instanceof String)
                || (left instanceof String && right instanceof Number)) {
            return getMultipliedString(ctx);
        }

        if (left instanceof Number) {
            if (right instanceof Number) {
                BigDecimal leftNumber = getBigDecimalOrNull(left);
                BigDecimal rightNumber = getBigDecimal(right, ctx);
                return leftNumber != null && rightNumber != null
                        ? leftNumber.multiply(rightNumber, MathContext.DECIMAL128) : null;
            }
            if (right instanceof Duration) {
                return Duration.ofSeconds(getBigDecimalOrNull(left)
                        .multiply(getBigDecimalOrNull(((Duration) right).getSeconds()), MathContext.DECIMAL128)
                        .longValue());
            }
            if (right instanceof ChronoPeriod) {
                return ComparablePeriod.ofMonths(getBigDecimalOrNull(left)
                        .multiply(getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)),
                                MathContext.DECIMAL128)
                        .intValue());
            }
            return null;
        }

        if (left instanceof Duration) {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
            BigDecimal rightDecimal;
            if (right instanceof Number) {
                rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
            } else {
                rightDecimal = getBigDecimal(right, ctx);
            }
            return Duration.ofNanos(durationNumericValue.multiply(rightDecimal).longValue());
        }

        if (left instanceof ChronoPeriod) {
            if (right instanceof Number || right == null) {
                BigDecimal rightNumber = getBigDecimal(right, ctx);
                return ComparablePeriod
                        .ofMonths(getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left))
                                .multiply(rightNumber, MathContext.DECIMAL128).intValue());
            }
            if (right instanceof ChronoPeriod) {
                return getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(
                        getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)),
                        MathContext.DECIMAL128);
            }
        }
        if (left == null || right == null) {
            return null;
        }

        return null;
    }

    private BigDecimal getMultipliedString(EvaluationContext ctx) {
        if (ctx.getFEELDialect().equals(FEELDialect.BFEEL)) {
            return BigDecimal.ZERO;
        } else {
            return null;
        }
    }*/
}
