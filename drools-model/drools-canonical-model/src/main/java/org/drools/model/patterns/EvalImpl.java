package org.drools.model.patterns;

import org.drools.model.Condition;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

import static org.drools.model.Condition.Type.EVAL;

public class EvalImpl implements Condition, ModelComponent {

    private final SingleConstraint expr;

    public EvalImpl( boolean value ) {
        this( value ? SingleConstraint.TRUE : SingleConstraint.FALSE );
    }

    public EvalImpl( SingleConstraint expr ) {
        this.expr = expr;
    }

    public SingleConstraint getExpr() {
        return expr;
    }

    @Override
    public Type getType() {
        return EVAL;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof EvalImpl) ) return false;

        EvalImpl that = ( EvalImpl ) o;

        return ModelComponent.areEqualInModel( expr, that.expr );
    }

    @Override
    public String toString() {
        return "EvalImpl (expr: " + expr + ")";
    }
}
