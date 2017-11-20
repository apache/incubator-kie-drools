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

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.UnaryTestImpl;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

public class UnaryTestNode
        extends BaseNode {

    private UnaryOperator operator;
    private BaseNode      value;

    public enum UnaryOperator {
        LTE( "<=" ),
        LT( "<" ),
        GT( ">" ),
        GTE( ">=" ),
        NE( "!=" ),
        EQ( "=" ),
        NOT( "not" ),
        IN( "in" );

        public final String symbol;

        UnaryOperator(String symbol) {
            this.symbol = symbol;
        }

        public static UnaryOperator determineOperator(String symbol) {
            for ( UnaryOperator op : UnaryOperator.values() ) {
                if ( op.symbol.equals( symbol ) ) {
                    return op;
                }
            }
            throw new IllegalArgumentException( "No operator found for symbol '" + symbol + "'" );
        }
    }

    public UnaryTestNode( String op, BaseNode value ) {
        super();
        setText( op+" "+value.getText() );
        this.operator = UnaryOperator.determineOperator( op );
        this.value = value;
    }

    public UnaryTestNode(ParserRuleContext ctx, String op, BaseNode value) {
        super( ctx );
        this.operator = UnaryOperator.determineOperator( op );
        this.value = value;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(UnaryOperator operator) {
        this.operator = operator;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    @Override
    public UnaryTest evaluate(EvaluationContext ctx) {
        switch ( operator ) {
            case LTE:
                return new UnaryTestImpl( createCompareUnaryTest( (l, r) -> l.compareTo( r ) <= 0 ) , value.getText() );
            case LT:
                return new UnaryTestImpl( createCompareUnaryTest( (l, r) -> l.compareTo( r ) < 0 ) , value.getText() );
            case GT:
                return new UnaryTestImpl( createCompareUnaryTest( (l, r) -> l.compareTo( r ) > 0 ) , value.getText() );
            case GTE:
                return new UnaryTestImpl( createCompareUnaryTest( (l, r) -> l.compareTo( r ) >= 0 ) , value.getText() );
            case EQ:
                return new UnaryTestImpl( createIsEqualUnaryTest( ) , value.getText() );
            case NE:
                return new UnaryTestImpl( createIsNotEqualUnaryTest( ) , value.getText() );
            case IN:
                return new UnaryTestImpl( createInUnaryTest() , value.getText() );
            case NOT:
                return new UnaryTestImpl( createNotUnaryTest() , value.getText() );
        }
        ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.NULL_OR_UNKNOWN_OPERATOR)));
        return null;
    }

    private UnaryTest createCompareUnaryTest( BiPredicate<Comparable, Comparable> op ) {
        return (context, left) -> {
            Object right = value.evaluate( context );
            return EvalHelper.compare( left, right, context, op );
        };
    }

    /**
     * For a Unary Test an = (equal) semantic depends on the RIGHT value.
     * If the RIGHT is NOT a list, then standard equals semantic applies
     * If the RIGHT is a LIST, then the semantic is "right contains left"
     */
    private Boolean utEqualSemantic(Object left, Object right, EvaluationContext context) {
        if (right instanceof Collection) {
            return ((Collection) right).contains(left);
        } else {
            // evaluate single entity
            return EvalHelper.isEqual(left, right, context);
        }
    }

    private UnaryTest createIsEqualUnaryTest( ) {
        return (context, left) -> {
            Object right = value.evaluate( context );
            return utEqualSemantic(left, right, context);
        };
    }

    private UnaryTest createIsNotEqualUnaryTest( ) {
        return (context, left) -> {
            Object right = value.evaluate( context );
            Boolean result = utEqualSemantic(left, right, context);
            return result != null ? ! result : null;
        };
    }

    private UnaryTest createInUnaryTest() {
        return (c, o) -> {
            if (o == null) {
                return false;
            }
            Object val = value.evaluate( c );
            if (val instanceof Range) {
                try {
                    return ((Range) val).includes(o);
                } catch (Exception e) {
                    c.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.EXPRESSION_IS_RANGE_BUT_VALUE_IS_NOT_COMPARABLE, o.toString(), val.toString())));
                    throw e;
                }
            } else if (val instanceof Collection) {
                return ((Collection) val).contains(o);
            } else {
                return false; // make consistent with #createNotUnaryTest()
            }
        };
    }

    private UnaryTest createNotUnaryTest() {
        return (c, o) -> {
            Object val = value.evaluate( c );
            if( val == null ) {
                return null;
            }
            List<Object> tests = (List<Object>) val;
            for( Object test : tests ) {
                if( test == null ) {
                    if( o == null ) {
                        return false;
                    }
                } else if( test instanceof UnaryTest ) {
                    if( ((UnaryTest)test).apply( c, o ) ) {
                        return false;
                    }
                } else if( o == null ) {
                    if( test == null ) {
                        return false;
                    }
                } else if( test instanceof Range ) {
                    try {
                        if( ((Range)test).includes( o ) ) {
                            return false;
                        }
                    } catch ( Exception e ) {
                        c.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.EXPRESSION_IS_RANGE_BUT_VALUE_IS_NOT_COMPARABLE, o.toString(), test.toString() ) ) );
                        throw e;
                    }
                } else if (test instanceof Collection) {
                    return !((Collection) val).contains(o);
                } else {
                    // test is a constant, so return false if it is equal to "o"
                    if( test.equals( o ) ) {
                        return false;
                    }
                }
            }
            return true;
        };
    }
}
