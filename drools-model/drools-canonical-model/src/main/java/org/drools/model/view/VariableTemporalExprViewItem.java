package org.drools.model.view;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;

public class VariableTemporalExprViewItem<T> extends TemporalExprViewItem<T> {

    private final Function1<?,?> f1;
    private final Variable<?> var2;
    private final Function1<?,?> f2;

    public VariableTemporalExprViewItem( Variable<T> var1, Function1<?,?> f1, Variable<?> var2, Function1<?,?> f2, TemporalPredicate temporalPredicate ) {
        super(var1, var2, temporalPredicate);
        this.f1 = f1;
        this.var2 = var2;
        this.f2 = f2;
    }

    public VariableTemporalExprViewItem( String exprId, Variable<T> var1, Function1<?,?> f1, Variable<?> var2, Function1<?,?> f2, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.f1 = f1;
        this.var2 = var2;
        this.f2 = f2;
    }

    public Variable<?> getSecondVariable() {
        return var2;
    }

    public Function1<?, ?> getF1() {
        return f1;
    }

    public Function1<?, ?> getF2() {
        return f2;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable(), getSecondVariable() };
    }
}
