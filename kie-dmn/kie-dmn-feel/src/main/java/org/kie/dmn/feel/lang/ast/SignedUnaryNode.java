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
package org.kie.dmn.feel.lang.ast;

import java.math.BigDecimal;
import java.time.Duration;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.Msg;
import org.kie.dmn.feel.util.NumberEvalHelper;

public class SignedUnaryNode
        extends BaseNode {

    public enum Sign {
        POSITIVE, NEGATIVE;

        public static Sign determineSign(String str) {
            if ( "-".equals( str ) ) {
                return NEGATIVE;
            } else if ( "+".equals( str ) ) {
                return POSITIVE;
            }
            throw new IllegalArgumentException( "Unknown sign: '" + str + "'. Expecting either '+' or '-'." );
        }
    }

    private Sign     sign;
    private BaseNode expression;

    public SignedUnaryNode(ParserRuleContext ctx, BaseNode expr) {
        super( ctx );
        sign = Sign.determineSign( ctx.start.getText() );
        expression = expr;
    }

    public SignedUnaryNode(Sign sign, BaseNode expression, String text) {
        this.sign = sign;
        this.expression = expression;
        this.setText(text);
    }

    public Sign getSign() {
        return sign;
    }

    public BaseNode getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if (expression == null) return null;
        Object expressionResult = expression.evaluate( ctx );
        if (expressionResult instanceof String) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.CANNOT_BE_SIGNED)));
            return null;
        }
        if (expressionResult instanceof ComparablePeriod comparablePeriod) {
            return  Sign.NEGATIVE == sign ? comparablePeriod.negated() : expressionResult;
        }
        if (expressionResult instanceof Duration duration) {
            return  Sign.NEGATIVE == sign ? duration.negated() : expressionResult;
        }
        BigDecimal result = NumberEvalHelper.getBigDecimalOrNull(expressionResult );
        if ( result == null ) {
            ctx.notifyEvt( astEvent(Severity.WARN, Msg.createMessage(Msg.NEGATING_A_NULL)));
            return null;
        } else if ( Sign.NEGATIVE == sign ) {
            return BigDecimal.valueOf( -1 ).multiply( result );
        } else {
            return result;
        }
    }

    @Override
    public Type getResultType() {
        return expression.getResultType();
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { expression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
