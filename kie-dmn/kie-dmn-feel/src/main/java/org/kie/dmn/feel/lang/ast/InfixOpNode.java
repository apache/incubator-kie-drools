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

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.BinaryOperator;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.EvalHelper;

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
        // see FEEL spec Table 45.
        if ( operator.isBoolean() ) { return BuiltInType.BOOLEAN; }
        switch ( operator ) {
            case ADD:
            case SUB: {
                if ( left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER ) {
                    return BuiltInType.NUMBER;
                } else if ( left.getResultType() == BuiltInType.DATE_TIME && right.getResultType() == BuiltInType.DATE_TIME ) {
                    return BuiltInType.DATE_TIME;
                } else if ( left.getResultType() == BuiltInType.TIME && right.getResultType() == BuiltInType.TIME ) {
                    return BuiltInType.TIME;
                } else if ( left.getResultType() == BuiltInType.DURATION || right.getResultType() == BuiltInType.DURATION ) {
                    if ( left.getResultType() == BuiltInType.DATE_TIME || right.getResultType() == BuiltInType.DATE_TIME ) {
                        return BuiltInType.DATE_TIME;
                    } else if ( left.getResultType() == BuiltInType.TIME || right.getResultType() == BuiltInType.TIME ) {
                        return BuiltInType.TIME;
                    } else if ( left.getResultType() == BuiltInType.DURATION && right.getResultType() == BuiltInType.DURATION ) {
                        return BuiltInType.DURATION;
                    }
                } else if ( left.getResultType() == BuiltInType.STRING && right.getResultType() == BuiltInType.STRING ) {
                    return BuiltInType.STRING;
                }
            }
            case MULT:
            case DIV: {
                if ( left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER ) {
                    return BuiltInType.NUMBER;
                } else if ( left.getResultType() == BuiltInType.DURATION || right.getResultType() == BuiltInType.DURATION ) {
                    if ( left.getResultType() == BuiltInType.NUMBER || right.getResultType() == BuiltInType.NUMBER ) {
                        return BuiltInType.NUMBER;
                    }
                }
            }
            case POW: {
                if ( left.getResultType() == BuiltInType.NUMBER && right.getResultType() == BuiltInType.NUMBER ) {
                    return BuiltInType.NUMBER;
                }
            }
            default:
                return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if (this.left == null) return null;
        switch ( operator ) {
            case ADD:
                return add(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
            case SUB:
                return sub(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
            case MULT:
                return mult(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
            case DIV:
                return div(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
            case POW:
                return math(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx, (l, r) -> BigDecimalMath.pow(l, r, MathContext.DECIMAL128));
            case AND:
                Boolean leftAND = EvalHelper.getBooleanOrNull(this.left.evaluate(ctx));
                if (leftAND != null) {
                    if (leftAND.booleanValue()) {
                        return EvalHelper.getBooleanOrNull(this.right.evaluate(ctx));
                    } else {
                        return Boolean.FALSE; //left hand operand is false, we do not need to evaluate right side
                    }
                } else {
                    Boolean rightAND = EvalHelper.getBooleanOrNull(this.right.evaluate(ctx));
                    return Boolean.FALSE.equals(rightAND) ? Boolean.FALSE : null;
                }
            case OR:
                Boolean leftOR = EvalHelper.getBooleanOrNull(this.left.evaluate(ctx));
                if (leftOR != null) {
                    if (!leftOR.booleanValue()) {
                        return EvalHelper.getBooleanOrNull(this.right.evaluate(ctx));
                    } else {
                        return Boolean.TRUE; //left hand operand is true, we do not need to evaluate right side
                    }
                } else {
                    Boolean rightOR = EvalHelper.getBooleanOrNull(this.right.evaluate(ctx));
                    return Boolean.TRUE.equals(rightOR) ? Boolean.TRUE : null;
                }
            case LTE:
                Object leftLTE = this.left.evaluate(ctx);
                Object rightLTE = this.right.evaluate(ctx);
                return or(EvalHelper.compare(leftLTE, rightLTE, ctx, (l, r) -> l.compareTo(r) < 0),
                          EvalHelper.isEqual(leftLTE, rightLTE, ctx),
                          ctx); // do not use Java || to avoid potential NPE due to FEEL 3vl.
            case LT:
                return EvalHelper.compare(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx, (l, r) -> l.compareTo(r) < 0);
            case GT:
                return EvalHelper.compare(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx, (l, r) -> l.compareTo(r) > 0);
            case GTE:
                Object leftGTE = this.left.evaluate(ctx);
                Object rightGTE = this.right.evaluate(ctx);
                return or(EvalHelper.compare(leftGTE, rightGTE, ctx, (l, r) -> l.compareTo(r) > 0),
                          EvalHelper.isEqual(leftGTE, rightGTE, ctx),
                          ctx); // do not use Java || to avoid potential NPE due to FEEL 3vl.
            case EQ:
                return EvalHelper.isEqual(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
            case NE:
                Boolean result = EvalHelper.isEqual(this.left.evaluate(ctx), this.right.evaluate(ctx), ctx);
                return result != null ? !result : null;
            default:
                return null;
        }
    }

    public static Object add(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof String && right instanceof String ) {
            return ((String) left) + ((String) right);
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return new ComparablePeriod(((ChronoPeriod) left).plus((ChronoPeriod) right));
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return ((Duration) left).plus( (Duration) right);
        } else if (left instanceof ZonedDateTime && right instanceof ChronoPeriod) {
            return ((ZonedDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof OffsetDateTime && right instanceof ChronoPeriod) {
            return ((OffsetDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof LocalDateTime && right instanceof ChronoPeriod) {
            return ((LocalDateTime) left).plus((ChronoPeriod) right);
        } else if (left instanceof LocalDate && right instanceof ChronoPeriod) {
            return ((LocalDate) left).plus((ChronoPeriod) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Duration ) {
            return ((ZonedDateTime) left).plus( (Duration) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Duration ) {
            return ((OffsetDateTime) left).plus( (Duration) right);
        } else if ( left instanceof LocalDateTime && right instanceof Duration ) {
            return ((LocalDateTime) left).plus( (Duration) right);
        } else if ( left instanceof LocalDate && right instanceof Duration ) {
            return ((LocalDate) left).plusDays( ((Duration) right).toDays() );
        } else if (left instanceof ChronoPeriod && right instanceof ZonedDateTime) {
            return ((ZonedDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof OffsetDateTime) {
            return ((OffsetDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof LocalDateTime) {
            return ((LocalDateTime) right).plus((ChronoPeriod) left);
        } else if (left instanceof ChronoPeriod && right instanceof LocalDate) {
            return ((LocalDate) right).plus((ChronoPeriod) left);
        } else if ( left instanceof Duration && right instanceof ZonedDateTime ) {
            return ((ZonedDateTime) right).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof OffsetDateTime ) {
            return ((OffsetDateTime) right).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof LocalDateTime ) {
            return ((LocalDateTime) right).plus( (Duration) left);
        } else if ( left instanceof Duration && right instanceof LocalDate ) {
            return ((LocalDate) right).plusDays( ((Duration) left).toDays() );
        } else if ( left instanceof LocalTime && right instanceof Duration ) {
            return ((LocalTime) left).plus( (Duration) right);
        } else if ( left instanceof Duration && right instanceof LocalTime ) {
            return ((LocalTime) right).plus( (Duration) left);
        } else if ( left instanceof OffsetTime && right instanceof Duration ) {
            return ((OffsetTime) left).plus( (Duration) right);
        } else if ( left instanceof Duration && right instanceof OffsetTime ) {
            return ((OffsetTime) right).plus( (Duration) left);
        } else {
            return math( left, right, ctx, (l, r) -> l.add( r, MathContext.DECIMAL128 ) );
        }
    }

    public static Object sub(Object left, Object right, EvaluationContext ctx) {
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
            } else if( right instanceof LocalDate && left instanceof LocalDate ) {
                return Duration.ofDays( ChronoUnit.DAYS.between( (LocalDate) right, (LocalDate) left ) );
            }
            return Duration.between( (Temporal) right, (Temporal) left);
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return new ComparablePeriod(((ChronoPeriod) left).minus((ChronoPeriod) right));
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return ((Duration) left).minus( (Duration) right);
        } else if (left instanceof ZonedDateTime && right instanceof ChronoPeriod) {
            return ((ZonedDateTime) left).minus((ChronoPeriod) right);
        } else if (left instanceof OffsetDateTime && right instanceof ChronoPeriod) {
            return ((OffsetDateTime) left).minus((ChronoPeriod) right);
        } else if (left instanceof LocalDateTime && right instanceof ChronoPeriod) {
            return ((LocalDateTime) left).minus((ChronoPeriod) right);
        } else if (left instanceof LocalDate && right instanceof ChronoPeriod) {
            return ((LocalDate) left).minus((ChronoPeriod) right);
        } else if ( left instanceof ZonedDateTime && right instanceof Duration ) {
            return ((ZonedDateTime) left).minus( (Duration) right);
        } else if ( left instanceof OffsetDateTime && right instanceof Duration ) {
            return ((OffsetDateTime) left).minus( (Duration) right);
        } else if ( left instanceof LocalDateTime && right instanceof Duration ) {
            return ((LocalDateTime) left).minus( (Duration) right);
        } else if ( left instanceof LocalDate && right instanceof Duration ) {
            return ((LocalDate) left).minusDays( ((Duration) right).toDays() );
        } else if ( left instanceof LocalTime && right instanceof Duration ) {
            return ((LocalTime) left).minus( (Duration) right);
        } else if ( left instanceof OffsetTime && right instanceof Duration ) {
            return ((OffsetTime) left).minus( (Duration) right);
        } else {
            return math( left, right, ctx, (l, r) -> l.subtract( r, MathContext.DECIMAL128 )  );
        }
    }

    public static Object mult(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Duration && right instanceof Number ) {
            return ((Duration)left).multipliedBy( ((Number) right).longValue() );
        } else if ( left instanceof Number && right instanceof Duration ) {
            return Duration.ofSeconds( EvalHelper.getBigDecimalOrNull( left ).multiply( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 ).longValue() );
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return EvalHelper.getBigDecimalOrNull( ((Duration) left).getSeconds() ).multiply( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 );
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(EvalHelper.getBigDecimalOrNull(((Number) right).longValue()), MathContext.DECIMAL128).intValue());
        } else if (left instanceof Number && right instanceof ChronoPeriod) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(left).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128).intValue());
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
        } else {
            return math( left, right, ctx, (l, r) -> l.multiply( r, MathContext.DECIMAL128 ) );
        }
    }

    public static Object div(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Duration && right instanceof Number ) {
            return ((Duration)left).dividedBy( ((Number) right).longValue() );
        } else if ( left instanceof Number && right instanceof Duration ) {
            return Duration.ofSeconds( EvalHelper.getBigDecimalOrNull( left ).divide( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 ).longValue() );
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return EvalHelper.getBigDecimalOrNull( ((Duration) left).getSeconds() ).divide( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 );
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(EvalHelper.getBigDecimalOrNull(((Number) right).longValue()), MathContext.DECIMAL128).intValue());
        } else if (left instanceof Number && right instanceof ChronoPeriod) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(left).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128).intValue());
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
        } else {
            return math( left, right, ctx, (l, r) -> l.divide( r, MathContext.DECIMAL128 ) );
        }
    }

    public static Object math(Object left, Object right, EvaluationContext ctx, BinaryOperator<BigDecimal> op) {
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
    public static Object and(Object left, Object right, EvaluationContext ctx) {
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
    public static Object or(Object left, Object right, EvaluationContext ctx) {
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

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { left, right };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
