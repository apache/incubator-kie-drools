/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.math.BigDecimal;

public class SignedUnaryNode
        extends BaseNode {

    public static enum Sign {
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

    public Sign getSign() {
        return sign;
    }

    public BaseNode getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        BigDecimal result = EvalHelper.getBigDecimalOrNull( expression.evaluate( ctx ) );
        if ( result == null ) {
            ctx.notifyEvt( astEvent(Severity.WARN, Msg.createMessage(Msg.NEGATING_A_NULL)));
            return null;
        } else if ( Sign.NEGATIVE == sign ) {
            return BigDecimal.valueOf( -1 ).multiply( result );
        } else {
            return result;
        }
    }

}
