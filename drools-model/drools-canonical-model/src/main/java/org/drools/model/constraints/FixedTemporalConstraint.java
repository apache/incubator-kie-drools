package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.FixedTemporalExprViewItem;

public class FixedTemporalConstraint<A> extends TemporalConstraint {

    private final Function1<?,?> func;
    private final long value;

    public FixedTemporalConstraint( String exprId, Variable<A> var1, Function1<?,?> func, long value, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.func = func;
        this.value = value;
    }

    public FixedTemporalConstraint( FixedTemporalExprViewItem<A> expr ) {
        this( expr.getExprId(), expr.getFirstVariable(), expr.getF1(), expr.getValue(), expr.getTemporalPredicate() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { var1 };
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        FixedTemporalConstraint<?> that = ( FixedTemporalConstraint<?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( func, that.func ) ) return false;
        if ( value != that.value ) return false;
        return temporalPredicate.equals( that.temporalPredicate );
    }

    @Override
    public Function1<?, ?> getF1() {
        return func;
    }

    @Override
    public Function1<?, ?> getF2() {
        return null;
    }

    @Override
    public FixedTemporalConstraint<A> negate() {
        return new FixedTemporalConstraint<>( "!" + getExprId(), var1, func, value, temporalPredicate.negate() );
    }

    @Override
    public FixedTemporalConstraint replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new FixedTemporalConstraint( getExprId(), newVar, func, value, temporalPredicate );
        }
        return this;
    }

    @Override
    public String toString() {
        return "FixedTemporalConstraint for '" + getExprId() + "' (" +
                "function: lambda " + System.identityHashCode(func) + ", " +
                "value: " + value + ")";
    }

}
