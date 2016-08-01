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
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.util.EvalHelper;

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
            case ADD: return add( left, right, ctx );
            case SUB: return math( left, right, ctx, (l, r) -> l.subtract( r ) );
            case MULT: return math( left, right, ctx, (l, r) -> l.multiply( r ) );
            case DIV: return math( left, right, ctx, (l, r) -> l.divide( r ) );
            case POW: return math( left, right, ctx, (l, r) -> l.pow( r.intValue() ) );
            case AND: return and( left, right, ctx );
            case OR: return or( left, right, ctx );
            case LTE: return comparison( left, right, ctx, (l, r) -> l.compareTo( r ) <= 0 );
            case LT: return comparison( left, right, ctx, (l, r) -> l.compareTo( r ) < 0 );
            case GT: return comparison( left, right, ctx, (l, r) -> l.compareTo( r ) > 0 );
            case GTE: return comparison( left, right, ctx, (l, r) -> l.compareTo( r ) >= 0 );
            case EQ: return equality( left, right, ctx, (l, r) -> l.compareTo( r ) == 0 );
            case NE: return equality( left, right, ctx, (l, r) -> l.compareTo( r ) != 0 );
            default: return null;
        }
    }

    private Object add(Object left, Object right, EvaluationContextImpl ctx ) {
        if( left == null || right == null ) {
            return null;
        } else if( left instanceof String && right instanceof String ) {
            return ((String)left)+((String)right);
        } else {
            return math( left, right, ctx, (l, r) -> l.add( r ) );
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

    /**
     * Implements the ternary logic AND operation
     */
    private Object and(Object left, Object right, EvaluationContextImpl ctx ) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if( ( l == null && r == null ) || ( l == null && r == true ) || ( r == null && l == true ) ) {
            return null;
        } else if( l == null || r == null ) {
            return false;
        }
        return l && r;
    }

    /**
     * Implements the ternary logic OR operation
     */
    private Object or(Object left, Object right, EvaluationContextImpl ctx ) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if( ( l == null && r == null ) || ( l == null && r == false ) || ( r == null && l == false ) ) {
            return null;
        } else if( l == null || r == null ) {
            return true;
        }
        return l || r;
    }

    private Object comparison(Object left, Object right, EvaluationContextImpl ctx, BiPredicate<Comparable, Comparable> op ) {
        if( left == null && right == null ) {
            return null;
        } else if( ( left instanceof String && right instanceof String ) ||
                   ( left instanceof Number && right instanceof Number ) ||
                   ( left instanceof Boolean && right instanceof Boolean )) {
            Comparable l = (Comparable) left;
            Comparable r = (Comparable) right;
            return op.test( l, r );
        }
        return null;
    }

    private Object equality(Object left, Object right, EvaluationContextImpl ctx, BiPredicate<Comparable, Comparable> op ) {
        if( left == null && right == null ) {
            return operator == InfixOperator.EQ;
        } else  if( left == null || right == null ) {
            return operator == InfixOperator.NE;
        }
        return comparison( left, right, ctx, op );
    }

}
