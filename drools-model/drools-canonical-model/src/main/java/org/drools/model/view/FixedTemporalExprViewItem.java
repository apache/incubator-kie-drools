package org.drools.model.view;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;

public class FixedTemporalExprViewItem<T> extends TemporalExprViewItem<T> {

    private final Function1<?,?> f1;
    private final long value;

    public FixedTemporalExprViewItem( Variable<T> var1, Function1<?,?> f1, long value, TemporalPredicate temporalPredicate ) {
        super(var1, value, temporalPredicate);
        this.f1 = f1;
        this.value = value;
    }

    public FixedTemporalExprViewItem( String exprId, Variable<T> var1, Function1<?,?> f1, long value, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.f1 = f1;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public Function1<?, ?> getF1() {
        return f1;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable() };
    }
}
