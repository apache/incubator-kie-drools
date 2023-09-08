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
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.function.BinaryOperator;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

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
            return left + ((String) right);
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
            return addLocalDateAndDuration((LocalDate) left, (Duration) right);
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
            return addLocalDateAndDuration((LocalDate) right, (Duration) left);
        } else if ( left instanceof LocalTime && right instanceof Duration ) {
            return ((LocalTime) left).plus( (Duration) right);
        } else if ( left instanceof Duration && right instanceof LocalTime ) {
            return ((LocalTime) right).plus( (Duration) left);
        } else if ( left instanceof OffsetTime && right instanceof Duration ) {
            return ((OffsetTime) left).plus( (Duration) right);
        } else if ( left instanceof Duration && right instanceof OffsetTime ) {
            return ((OffsetTime) right).plus( (Duration) left);
        } else if ( left instanceof Temporal && right instanceof Temporal ) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        } else {
            return math( left, right, ctx, (l, r) -> l.add( r, MathContext.DECIMAL128 ) );
        }
    }

    public static Object sub(Object left, Object right, EvaluationContext ctx) {
        if ( left == null || right == null ) {
            return null;
        } else if ( left instanceof Temporal && right instanceof Temporal ) {
            return subtractTemporals((Temporal) left, (Temporal) right, ctx);
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
            LocalDateTime leftLDT = LocalDateTime.of((LocalDate) left, LocalTime.MIDNIGHT);
            LocalDateTime evaluated = leftLDT.minus((Duration) right);
            return LocalDate.of(evaluated.getYear(), evaluated.getMonth(), evaluated.getDayOfMonth());
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
        } else if (!isAllowedMultiplicationBasedOnSpec(left, right, ctx)) {
            return null;
        } else if ( left instanceof Duration && right instanceof Number ) {
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
            return Duration.ofNanos(durationNumericValue.multiply(rightDecimal).longValue());
        } else if ( left instanceof Number && right instanceof Duration ) {
            return Duration.ofSeconds( EvalHelper.getBigDecimalOrNull( left ).multiply( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 ).longValue() );
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).multiply(EvalHelper.getBigDecimalOrNull(right), MathContext.DECIMAL128).intValue());
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
            final BigDecimal durationNumericValue = BigDecimal.valueOf(((Duration) left).toNanos());
            final BigDecimal rightDecimal = BigDecimal.valueOf(((Number) right).doubleValue());
            return Duration.ofNanos(durationNumericValue.divide(rightDecimal, 0, RoundingMode.HALF_EVEN).longValue());
        } else if ( left instanceof Number && right instanceof TemporalAmount) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return null;
        } else if ( left instanceof Duration && right instanceof Duration ) {
            return EvalHelper.getBigDecimalOrNull( ((Duration) left).getSeconds() ).divide( EvalHelper.getBigDecimalOrNull( ((Duration)right).getSeconds() ), MathContext.DECIMAL128 );
        } else if (left instanceof ChronoPeriod && right instanceof Number) {
            final BigDecimal rightDecimal = EvalHelper.getBigDecimalOrNull(right);
            if (rightDecimal.compareTo(BigDecimal.ZERO) == 0) {
                ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.DIVISION_BY_ZERO.getMask()));
                return null;
            } else {
                return ComparablePeriod.ofMonths(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(rightDecimal, MathContext.DECIMAL128).intValue());
            }
        } else if (left instanceof ChronoPeriod && right instanceof ChronoPeriod) {
            return EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) left)).divide(EvalHelper.getBigDecimalOrNull(ComparablePeriod.toTotalMonths((ChronoPeriod) right)), MathContext.DECIMAL128);
        } else {
            return math( left, right, ctx, (l, r) -> l.divide( r, MathContext.DECIMAL128 ) );
        }
    }

    public static Object math(Object left, Object right, EvaluationContext ctx, BinaryOperator<BigDecimal> op) {
        BigDecimal l = left instanceof String ? null : EvalHelper.getBigDecimalOrNull( left );
        BigDecimal r = right instanceof String ? null : EvalHelper.getBigDecimalOrNull( right );
        if ( l == null || r == null ) {
            return null;
        }
        try {
            return op.apply( l, r );
        } catch ( ArithmeticException e ) {
            // happens in cases like division by 0
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.GENERAL_ARITHMETIC_EXCEPTION, e.getMessage())));
            return null;
        }
    }

    /**
     * Implements the ternary logic AND operation
     * @deprecated this variant do not allow short-circuit of the operator
     */
    @Deprecated
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
     * @deprecated this variant do not allow short-circuit of the operator
     */
    @Deprecated
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

    private static LocalDate addLocalDateAndDuration(LocalDate left, Duration right) {
        LocalDateTime leftLDT = LocalDateTime.of( left, LocalTime.MIDNIGHT);
        LocalDateTime evaluated = leftLDT.plus(right);
        return LocalDate.of(evaluated.getYear(), evaluated.getMonth(), evaluated.getDayOfMonth());
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { left, right };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    private static Object subtractTemporals(final Temporal left, final Temporal right, final EvaluationContext ctx) {
        // Based on the Table 57 in the spec, if it is only date, convert to date and time.
        final Temporal leftTemporal = getTemporalForSubtraction(left);
        final Temporal rightTemporal = getTemporalForSubtraction(right);

        if (isAllowedTemporalSubtractionBasedOnSpec(leftTemporal, rightTemporal, ctx)) {
            return Duration.between(rightTemporal, leftTemporal);
        } else {
            return null;
        }
    }

    private static Temporal getTemporalForSubtraction(final Temporal temporal) {
        if (temporal instanceof LocalDate) {
            return ZonedDateTime.of((LocalDate) temporal, LocalTime.MIDNIGHT, ZoneOffset.UTC);
        } else {
            return temporal;
        }
    }

    /**
     * Checks if the multiplication is supported by the DMN specification based on the objects specified as parameters.
     *
     * @param left Left parameter of the subtraction expression.
     * @param right Right parameter of the subtraction expression.
     * @param ctx Context that is used to notify about not allowed set of parameters.
     * @return True, if the parameters are valid for multiplication based on the DMN specification.
     *         False, when multiplication is not defined for the specified set of parameters in the DMN spec, or is forbidden: <br>
     *         - Multiplication of two durations e is not allowed in the specification.
     */
    static boolean isAllowedMultiplicationBasedOnSpec(final Object left, final Object right, final EvaluationContext ctx) {
        if (left instanceof TemporalAmount && right instanceof TemporalAmount) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.INVALID_PARAMETERS_FOR_OPERATION, "multiplication",
                                                                                                       left.getClass().getName(),
                                                                                                       right.getClass().getName())));
            return false;
        }
        return true;
    }

    /**
     * Checks if the subtraction is supported by the DMN specification based on the temporals specified as parameters.
     *
     * @param leftTemporal Left temporal parameter of the subtraction expression.
     * @param rightTemporal Right temporal parameter of the subtraction expression.
     * @param ctx Context that is used to notify about not allowed set of parameters.
     * @return True, if the temporal parameters are valid for subtraction based on the DMN specification.
     *         False, when subtraction is not defined for the specified set of parameters in the DMN spec, or is forbidden: <br>
     *         - Subtraction of a datetime with timezone and a datetime without a timezone is not defined in the specification.
     *         - Subtraction of a time and a datetime is not defined in the specification.
     */
    private static boolean isAllowedTemporalSubtractionBasedOnSpec(final Temporal leftTemporal, final Temporal rightTemporal, final EvaluationContext ctx) {
        // Both datetimes have a timezone or both timezones don't have it. Cannot combine timezoned datetime and datetime without a timezone.
        if ((leftTemporal instanceof ZonedDateTime || leftTemporal instanceof OffsetDateTime)
                && (rightTemporal instanceof LocalDateTime))  {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.DATE_AND_TIME_TIMEZONE_NEEDED, "first", leftTemporal, "second", rightTemporal)));
            return false;
        } else if ((rightTemporal instanceof ZonedDateTime || rightTemporal instanceof OffsetDateTime)
                && (leftTemporal instanceof LocalDateTime)) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.DATE_AND_TIME_TIMEZONE_NEEDED, "second", rightTemporal, "first", leftTemporal)));
            return false;
        }

        // Cannot combine time and date (or datetime) based on the DMN specification.
        if ((!leftTemporal.isSupported(ChronoUnit.DAYS) && rightTemporal.isSupported(ChronoUnit.DAYS))
                || (!rightTemporal.isSupported(ChronoUnit.DAYS) && leftTemporal.isSupported(ChronoUnit.DAYS))) {
            ctx.notifyEvt(() -> new InvalidParametersEvent(FEELEvent.Severity.ERROR, Msg.OPERATION_IS_UNDEFINED_FOR_PARAMETERS.getMask()));
            return false;
        }

        return true;
    }
}
