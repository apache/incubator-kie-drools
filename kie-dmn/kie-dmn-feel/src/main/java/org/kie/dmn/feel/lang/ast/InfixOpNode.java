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
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public class InfixOpNode
        extends BaseNode {

    public static enum InfixOperator {
        ADD( "+" ),
        SUB( "-" ),
        MULT( "*" ),
        DIV( "/" ),
        POW( "**" ),
        LTE( "<=" ),
        LT( "<" ),
        GT( ">" ),
        GTE( ">=" ),
        EQ( "=" ),
        NE( "!=" ),
        AND( "and" ),
        OR( "or" );

        public final String symbol;

        InfixOperator(String symbol) {
            this.symbol = symbol;
        }

        public static InfixOperator determineOperator(String symbol) {
            for ( InfixOperator op : InfixOperator.values() ) {
                if ( op.symbol.equals( symbol ) ) {
                    return op;
                }
            }
            throw new IllegalArgumentException( "No operator found for symbol '" + symbol + "'" );
        }

        public boolean isBoolean() {
            return this == LTE || this == LT || this == GT || this == GTE || this == EQ || this == NE || this == AND || this == OR;
        }
    }

    private InfixOperator operator;
    private BaseNode      left;
    private BaseNode      right;

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

    public boolean isBoolean() {
        return this.operator.isBoolean();
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
    public Type getResultType() {
        // bellow is not exactly correct, as the result type depends on both
        // left and right
        return operator.isBoolean() ? BuiltInType.BOOLEAN : this.left.getResultType();
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        Object left = this.left.evaluate( ctx );
        Object right = this.right.evaluate( ctx );
        switch ( operator ) {
            case ADD:
                return add( left, right, ctx );
            case SUB:
                return sub( left, right, ctx );
            case MULT:
                return mult( left, right, ctx );
            case DIV:
                return div( left, right, ctx );
            case POW:
                return math( left, right, ctx, (l, r) -> l.pow( r.intValue(), MathContext.DECIMAL128 ) );
            case AND:
                return and( left, right, ctx );
            case OR:
                return or( left, right, ctx );
            case LTE:
                return EvalHelper.compare( left, right, ctx, (l, r) -> l.compareTo( r ) <= 0 );
            case LT:
                return EvalHelper.compare( left, right, ctx, (l, r) -> l.compareTo( r ) < 0 );
            case GT:
                return EvalHelper.compare( left, right, ctx, (l, r) -> l.compareTo( r ) > 0 );
            case GTE:
                return EvalHelper.compare( left, right, ctx, (l, r) -> l.compareTo( r ) >= 0 );
            case EQ:
                return EvalHelper.isEqual( left, right, ctx );
            case NE:
                Boolean result = EvalHelper.isEqual( left, right, ctx );
                return result != null ? ! result : null;
            default:
                return null;
        }
    }

    private Object add(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof String && right instanceof String ) {
            return ((String) left) + ((String) right);
        } else if ( left instanceof Period && right instanceof Period ) {
            return ((Period) left).plus( (Period) right);
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return ((Duration) left).plus( (Duration) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Period ) {
            return ((ZonedDateTime) left).plus( (Period) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Period ) {
            return ((OffsetDateTime) left).plus( (Period) right);
        } else if ( left instanceof LocalDateTime && right instanceof Period ) {
            return ((LocalDateTime) left).plus( (Period) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Duration ) {
            return ((ZonedDateTime) left).plus( (Duration) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Duration ) {
            return ((OffsetDateTime) left).plus( (Duration) right);
        } else if ( left instanceof LocalDateTime && right instanceof Duration ) {
            return ((LocalDateTime) left).plus( (Duration) right);
        } else if ( left instanceof Period && right instanceof ZonedDateTime ) {
            return ((ZonedDateTime) right).plus( (Period) left);
        } else if ( left instanceof Period && right instanceof OffsetDateTime ) {
            return ((OffsetDateTime) right).plus( (Period) left);
        } else if ( left instanceof Period && right instanceof LocalDateTime ) {
            return ((LocalDateTime) right).plus( (Period) left);
        } else if ( left instanceof Duration && right instanceof ZonedDateTime ) {
            return ((ZonedDateTime) right).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof OffsetDateTime ) {
            return ((OffsetDateTime) right).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof LocalDateTime ) {
            return ((LocalDateTime) right).plus( (Duration) left);
        } else if ( left instanceof LocalTime && right instanceof Duration ) {
            return ((LocalDateTime) left).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof LocalTime ) {
            return ((LocalDateTime) right).plus( (Duration) left);
        } else if ( left instanceof OffsetTime && right instanceof Duration ) {
            return ((OffsetTime) left).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof OffsetTime ) {
            return ((OffsetTime) right).plus( (Duration) left);
        } else {
            return math( left, right, ctx, (l, r) -> l.add( r, MathContext.DECIMAL128 ) );
        }
    }

    private Object sub(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Temporal && right instanceof Temporal ) {
            if( left instanceof ZonedDateTime || left instanceof OffsetDateTime ) {
                if( right instanceof LocalDateTime ) {
                    right = ZonedDateTime.of( (LocalDateTime) right, ZoneId.systemDefault() );
                }
            } else if( right instanceof ZonedDateTime || right instanceof OffsetDateTime ) {
                if( left instanceof LocalDateTime ) {
                    left = ZonedDateTime.of( (LocalDateTime) left, ZoneId.systemDefault() );
                }
            }
            return Duration.between( (Temporal) right, (Temporal) left);
        } else if ( left instanceof Period && right instanceof Period ) {
            return ((Period) left).minus( (Period) right);
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return ((Duration) left).minus( (Duration) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Period ) {
            return ((ZonedDateTime) left).minus( (Period) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Period ) {
            return ((OffsetDateTime) left).minus( (Period) right);
        } else if ( left instanceof LocalDateTime && right instanceof Period ) {
            return ((LocalDateTime) left).minus( (Period) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Duration ) {
            return ((ZonedDateTime) left).minus( (Duration) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Duration ) {
            return ((OffsetDateTime) left).minus( (Duration) right);
        } else if ( left instanceof LocalDateTime && right instanceof Duration ) {
            return ((LocalDateTime) left).minus( (Duration) right);
        } else if ( left instanceof LocalTime && right instanceof Duration ) {
            return ((LocalDateTime) left).minus( (Duration) left);
        } else if ( left instanceof OffsetTime && right instanceof Duration ) {
            return ((OffsetTime) left).minus( (Duration) left);
        } else {
            return math( left, right, ctx, (l, r) -> l.subtract( r, MathContext.DECIMAL128 )  );
        }
    }

    private Object mult(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Duration && right instanceof Number ) {
            return ((Duration)left).multipliedBy( ((Number) right).longValue() );
        } else if ( left instanceof Number && right instanceof Duration ) {
            return Duration.ofSeconds( EvalHelper.getBigDecimalOrNull( left ).multiply( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 ).longValue() );
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return EvalHelper.getBigDecimalOrNull( ((Duration) left).getSeconds() ).multiply( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 );
        } else if ( left instanceof Period && right instanceof Number ) {
            return Period.ofMonths( EvalHelper.getBigDecimalOrNull( ((Period)left).getMonths() ).multiply( EvalHelper.getBigDecimalOrNull( ((Number) right).longValue() ), MathContext.DECIMAL128 ).intValue() );
        } else if ( left instanceof Number && right instanceof Period ) {
            return Period.ofMonths( EvalHelper.getBigDecimalOrNull( left ).multiply( EvalHelper.getBigDecimalOrNull( ((Period)right).getMonths() ), MathContext.DECIMAL128 ).intValue() );
        } else if ( left instanceof Period && right instanceof Period ) {
            return EvalHelper.getBigDecimalOrNull( ((Period) left).getMonths() ).multiply( EvalHelper.getBigDecimalOrNull( ((Period)right).getMonths() ), MathContext.DECIMAL128 );
        } else {
            return math( left, right, ctx, (l, r) -> l.multiply( r, MathContext.DECIMAL128 ) );
        }
    }

    private Object div(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Duration && right instanceof Number ) {
            return ((Duration)left).dividedBy( ((Number) right).longValue() );
        } else if ( left instanceof Number && right instanceof Duration ) {
            return Duration.ofSeconds( EvalHelper.getBigDecimalOrNull( left ).divide( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 ).longValue() );
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return EvalHelper.getBigDecimalOrNull( ((Duration) left).getSeconds() ).divide( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 );
        } else if ( left instanceof Period && right instanceof Number ) {
            return Period.ofMonths( EvalHelper.getBigDecimalOrNull( ((Period)left).getMonths() ).divide( EvalHelper.getBigDecimalOrNull( ((Number) right).longValue() ), MathContext.DECIMAL128 ).intValue() );
        } else if ( left instanceof Number && right instanceof Period ) {
            return Period.ofMonths( EvalHelper.getBigDecimalOrNull( left ).divide( EvalHelper.getBigDecimalOrNull( ((Period)right).getMonths() ), MathContext.DECIMAL128 ).intValue() );
        } else if ( left instanceof Period && right instanceof Period ) {
            return EvalHelper.getBigDecimalOrNull( ((Period) left).getMonths() ).divide( EvalHelper.getBigDecimalOrNull( ((Period)right).getMonths() ), MathContext.DECIMAL128 );
        } else {
            return math( left, right, ctx, (l, r) -> l.divide( r, MathContext.DECIMAL128 ) );
        }
    }

    private Object math(Object left, Object right, EvaluationContext ctx, BinaryOperator<BigDecimal> op) {
        BigDecimal l = EvalHelper.getBigDecimalOrNull( left );
        BigDecimal r = EvalHelper.getBigDecimalOrNull( right );
        if ( l == null || r == null ) {
            return null;
        }
        try {
            return op.apply( l, r );
        } catch ( ArithmeticException e ) {
            // happens in cases like division by 0
            return null;
        }
    }

    /**
     * Implements the ternary logic AND operation
     */
    private Object and(Object left, Object right, EvaluationContext ctx) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if ( (l == null && r == null) || (l == null && r == true) || (r == null && l == true) ) {
            return null;
        } else if ( l == null || r == null ) {
            return false;
        }
        return l && r;
    }

    /**
     * Implements the ternary logic OR operation
     */
    private Object or(Object left, Object right, EvaluationContext ctx) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if ( (l == null && r == null) || (l == null && r == false) || (r == null && l == false) ) {
            return null;
        } else if ( l == null || r == null ) {
            return true;
        }
        return l || r;
    }

}
