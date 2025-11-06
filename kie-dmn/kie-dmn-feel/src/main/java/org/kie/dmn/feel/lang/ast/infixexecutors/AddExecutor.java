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

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandler;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandlerFactory;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.addLocalDateAndDuration;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.commonManageInvalidParameters;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getBigDecimal;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getString;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.getTemporalAmount;
import static org.kie.dmn.feel.util.NumberEvalHelper.getBigDecimalOrNull;

public class AddExecutor implements InfixExecutor {

    private static final AddExecutor INSTANCE = new AddExecutor();

    private AddExecutor() {
        // Singleton pattern
    }

    public static AddExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        //return add(left, right, ctx);
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        return handler.executeAdd(left, right, ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx), ctx);
    }

    /*private Object add(Object left, Object right, EvaluationContext ctx) {
        //return InfixExecutorUtils.executeAdd(left, right, ctx);
        if ((left instanceof String || right instanceof String)) {
            return getSummedString(left, right, ctx);
        }

        if (left instanceof Number) {
            BigDecimal leftNumber = getBigDecimalOrNull(left);
            BigDecimal rightNumber = getBigDecimal(right, ctx);
            return leftNumber != null && rightNumber != null
                    ? leftNumber.add(rightNumber, MathContext.DECIMAL128) : null;
        }
        if (left == null && right instanceof Number) {
            BigDecimal rightNumber = getBigDecimalOrNull(right);
            left = getBigDecimal(left, ctx);
            return rightNumber != null && left instanceof Number
                    ? rightNumber.add(getBigDecimalOrNull(left), MathContext.DECIMAL128) : null;
        }

        if (left instanceof Duration leftDuration) {
            if (right instanceof LocalDate localDate) {
                return addLocalDateAndDuration(localDate, leftDuration);
            }
            if (right instanceof Duration rightDuration) {
                return leftDuration.plus(rightDuration);
            }
        }
        if (right instanceof Duration duration && left instanceof LocalDate localDate) {
            return addLocalDateAndDuration(localDate, duration);
        }

        if (left instanceof Temporal temporal) {
            if (right instanceof Number) {
                BigDecimal leftNumber = getBigDecimal(left, ctx);
                BigDecimal rightNumber = getBigDecimal(right, ctx);
                if (leftNumber != null && rightNumber != null) {
                    return leftNumber.add(rightNumber, MathContext.DECIMAL128);
                }
            }
            right = getTemporalAmount(right, ctx);
            if (right instanceof TemporalAmount temporalAmount) {
                return temporal.plus(temporalAmount);
            }
        } else if (left instanceof TemporalAmount temporalAmount) {
            if (right instanceof Temporal temporal) {
                return temporal.plus(temporalAmount);
            }
            if (right instanceof ChronoPeriod chronoPeriod) {
                return chronoPeriod.plus(temporalAmount);
            }
        }
        if (left == null || right == null) {
            return getSummedString(left, right, ctx);
        }

        commonManageInvalidParameters(ctx);
        return null;
    }*/

    private String getSummedString(Object left, Object right, EvaluationContext ctx) {
        if (ctx.getFEELDialect().equals(FEELDialect.BFEEL)) {
            String leftString = getString(left);
            String rightString = getString(right);
            return leftString + rightString;
        } else {
            if (left instanceof String stringLeft && right instanceof String stringRight) {
                return stringLeft + stringRight;
            }
            return null;
        }
    }
}
