package org.drools.model.consequences;

import org.drools.model.Condition;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;

public class ConditionalNamedConsequenceImpl implements Condition {
    private final SingleConstraint expr;
    private final NamedConsequenceImpl thenConsequence;
    private final ConditionalNamedConsequenceImpl elseBranch;

    public ConditionalNamedConsequenceImpl( SingleConstraint expr, NamedConsequenceImpl thenConsequence, ConditionalNamedConsequenceImpl elseBranch ) {
        this.expr = expr;
        this.thenConsequence = thenConsequence;
        this.elseBranch = elseBranch;
    }

    public SingleConstraint getExpr() {
        return expr;
    }

    public NamedConsequenceImpl getThenConsequence() {
        return thenConsequence;
    }

    public ConditionalNamedConsequenceImpl getElseBranch() {
        return elseBranch;
    }

    @Override
    public Type getType() {
        return Type.CONSEQUENCE;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ConditionalNamedConsequenceImpl) ) return false;

        ConditionalNamedConsequenceImpl that = ( ConditionalNamedConsequenceImpl ) o;

        if ( expr != null ? !expr.equals( that.expr ) : that.expr != null ) return false;
        if ( thenConsequence != null ? !thenConsequence.equals( that.thenConsequence ) : that.thenConsequence != null )
            return false;
        return elseBranch != null ? elseBranch.equals( that.elseBranch ) : that.elseBranch == null;
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (thenConsequence != null ? thenConsequence.hashCode() : 0);
        result = 31 * result + (elseBranch != null ? elseBranch.hashCode() : 0);
        return result;
    }
}
