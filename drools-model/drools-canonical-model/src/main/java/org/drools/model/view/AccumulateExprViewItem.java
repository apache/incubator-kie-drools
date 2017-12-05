package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.AccumulateFunction;

public class AccumulateExprViewItem<T> extends AbstractExprViewItem<T> {

    private final ViewItem<T> expr;
    private final AccumulateFunction[] accumulateFunctions;

    public AccumulateExprViewItem(ViewItem<T> expr, AccumulateFunction... accumulateFunctions) {
        super(expr.getFirstVariable());
        this.expr = expr;
        this.accumulateFunctions = accumulateFunctions;
    }

    @Override
    public Condition.Type getType() {
        return Type.ACCUMULATE;
    }

    @Override
    public Variable<?>[] getVariables() {
        return expr.getVariables();
    }

    public ViewItem<T> getExpr() {
        return expr;
    }

    public AccumulateFunction[] getAccumulateFunctions() {
        return accumulateFunctions;
    }
}
