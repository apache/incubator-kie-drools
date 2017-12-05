package org.drools.model.view;

import org.drools.model.AccumulateFunction;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.UserDefinedAccumulateFunction;

public class AccumulateExprViewItem<T> extends AbstractExprViewItem<T> {

    private final ViewItem<T> expr;
    private AccumulateFunction<T, ?, ?>[] functions = new AccumulateFunction[0];
    private UserDefinedAccumulateFunction[] userDefinedAccumulateFunctions = new UserDefinedAccumulateFunction[0];

    public AccumulateExprViewItem(ViewItem<T> expr, AccumulateFunction<T, ?, ?>... functions) {
        super(expr.getFirstVariable());
        this.expr = expr;
        this.functions = functions;
    }

    public AccumulateExprViewItem(ViewItem<T> expr, UserDefinedAccumulateFunction... userDefinedAccumulateFunctions) {
        super(expr.getFirstVariable());
        this.expr = expr;
        this.userDefinedAccumulateFunctions = userDefinedAccumulateFunctions;
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

    public AccumulateFunction<T, ?, ?>[] getFunctions() {
        return functions;
    }

    public UserDefinedAccumulateFunction[] getUserDefinedAccumulateFunctions() {
        return userDefinedAccumulateFunctions;
    }
}
