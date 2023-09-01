package org.drools.model.functions.temporal;

import org.drools.model.impl.ModelComponent;

public abstract class AbstractTemporalPredicate<T extends AbstractTemporalPredicate> implements TemporalPredicate, ModelComponent {

    protected boolean negated = false;
    protected boolean thisOnRight = false;

    public TemporalPredicate setNegated( boolean negated ) {
        this.negated = negated;
        return this;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public boolean isEqualTo( ModelComponent other ) {
        if (!(other instanceof AbstractTemporalPredicate)) {
            return false;
        }

        AbstractTemporalPredicate tempPred = (( AbstractTemporalPredicate ) other);

        if (negated != tempPred.negated || thisOnRight != tempPred.thisOnRight || getClass() != tempPred.getClass()) {
            return false;
        }

        return isTemporalPredicateEqualTo( (T) tempPred );
    }

    protected abstract boolean isTemporalPredicateEqualTo(T other);

    @Override
    public TemporalPredicate negate() {
        this.negated = !this.negated;
        return this;
    }

    @Override
    public boolean isThisOnRight() {
        return thisOnRight;
    }

    @Override
    public TemporalPredicate thisOnRight() {
        this.thisOnRight = true;
        return this;
    }
}
