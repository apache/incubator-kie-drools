package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.PredicateInformation;
import org.drools.model.functions.PredicateN;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.view.FixedTemporalExprViewItem;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.VariableTemporalExprViewItem;

public abstract class TemporalConstraint<A> extends AbstractSingleConstraint {

    protected final Variable<A> var1;
    protected final TemporalPredicate temporalPredicate;

    public TemporalConstraint( String exprId, Variable<A> var1, TemporalPredicate temporalPredicate ) {
        super(exprId, PredicateInformation.EMPTY_PREDICATE_INFORMATION);
        this.var1 = var1;
        this.temporalPredicate = temporalPredicate;
    }

    @Override
    public PredicateN getPredicate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTemporal() {
        return true;
    }

    public TemporalPredicate getTemporalPredicate() {
        return temporalPredicate;
    }

    public static <A> TemporalConstraint<A> createTemporalConstraint( TemporalExprViewItem<A> expr ) {
        if (expr instanceof FixedTemporalExprViewItem) {
            return new FixedTemporalConstraint<>( (( FixedTemporalExprViewItem<A> ) expr) );
        }
        if (expr instanceof VariableTemporalExprViewItem ) {
            return new VariableTemporalConstraint<>( (( VariableTemporalExprViewItem<A> ) expr) );
        }
        throw new UnsupportedOperationException("Unknown expression " + expr);
    }

    public abstract Function1<?, ?> getF1();

    public abstract Function1<?, ?> getF2();
}
