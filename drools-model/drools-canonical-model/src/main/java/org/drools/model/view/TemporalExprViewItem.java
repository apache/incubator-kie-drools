package org.drools.model.view;

import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.temporal.TemporalPredicate;

public abstract class TemporalExprViewItem<T> extends AbstractExprViewItem<T> {

    private final TemporalPredicate temporalPredicate;

    public TemporalExprViewItem( Variable<T> var1, Object arg, TemporalPredicate temporalPredicate ) {
        super(getConstraintId(temporalPredicate, var1, arg), var1);
        this.temporalPredicate = temporalPredicate;
    }

    public TemporalExprViewItem( String exprId, Variable<T> var1, TemporalPredicate temporalPredicate ) {
        super(exprId, var1);
        this.temporalPredicate = temporalPredicate;
    }

    public TemporalPredicate getTemporalPredicate() {
        return temporalPredicate;
    }

    @Override
    public Type getType() {
        return Type.TEMPORAL;
    }

    private static String getConstraintId(TemporalPredicate temporalPredicate, Variable<?> var1, Object arg) {
        return temporalPredicate + "_" + var1 + "_" + arg;
    }
}
