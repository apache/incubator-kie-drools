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

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.lang.impl.EvaluationContextImpl;
import org.kie.dmn.util.EvalHelper;

import java.math.BigDecimal;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public class InfixOpNode
        extends BaseNode {

    public static enum InfixOperator {
        ADD("+"),
        SUB("-"),
        MULT("*"),
        DIV("/"),
        POW("**"),
        LTE("<="),
        LT("<"),
        GT(">"),
        GTE(">="),
        EQ("="),
        NE("!="),
        AND("and"),
        OR("or");

        public final String symbol;

        InfixOperator(String symbol) {
            this.symbol = symbol;
        }

        public static InfixOperator determineOperator( String symbol ) {
            for( InfixOperator op : InfixOperator.values() ) {
                if( op.symbol.equals( symbol ) ) {
                    return op;
                }
            }
            throw new IllegalArgumentException( "No operator found for symbol '"+symbol+"'" );
        }
    }

    private InfixOperator operator;
    private BaseNode left;
    private BaseNode right;

    public InfixOpNode(ParserRuleContext ctx, BaseNode left, String op, BaseNode right) {
        super( ctx );
        this.left = left;
        this.operator = InfixOperator.determineOperator( op );
        this.right = right;
    }

    public InfixOperator getOperator() {
        return operator;
    }

    public void setOperator(InfixOperator operator) {
        this.operator = operator;
    }

    public BaseNode getLeft() {
        return left;
    }

    public void setLeft(BaseNode left) {
        this.left = left;
    }

    public BaseNode getRight() {
        return right;
    }

    public void setRight(BaseNode right) {
        this.right = right;
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx) {
        Object left = this.left.evaluate( ctx );
        Object right = this.right.evaluate( ctx );
        switch( operator ) {
            case ADD: return math( left, right, ctx, (l, r) -> l.add(r) );
            case SUB: return math( left, right, ctx, (l, r) -> l.subtract( r ) );
            case MULT: return math( left, right, ctx, (l, r) -> l.multiply( r ) );
            case DIV: return math( left, right, ctx, (l, r) -> l.divide( r ) );
            case POW: return math( left, right, ctx, (l, r) -> l.pow( r.intValue() ) );
            default: return null;
        }
    }

    private Object math(Object left, Object right, EvaluationContextImpl ctx, BinaryOperator<BigDecimal> op ) {
        BigDecimal l = EvalHelper.getBigDecimalOrNull( left );
        BigDecimal r = EvalHelper.getBigDecimalOrNull( right );
        if( l == null || r == null ) {
            return null;
        }
        try {
            return op.apply( l, r );
        } catch( ArithmeticException e ) {
            // happens in cases like division by 0
            return null;
        }
    }

    private Object comparison(Object left, Object right, EvaluationContextImpl ctx, BiPredicate<Object, Object> op ) {
        BigDecimal l = EvalHelper.getBigDecimalOrNull( left );
        BigDecimal r = EvalHelper.getBigDecimalOrNull( right );
        if( l == null || r == null ) {
            return null;
        }
        try {
            return op.test( l, r );
        } catch( ArithmeticException e ) {
            // happens in cases like division by 0
            return null;
        }
    }
}
