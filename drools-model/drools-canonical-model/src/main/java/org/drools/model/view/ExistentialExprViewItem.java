package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Variable;

public class ExistentialExprViewItem<T> extends AbstractExprViewItem<T> {

    private final Condition.Type type;
    private final ViewItem expression;

    public ExistentialExprViewItem(Condition.Type type, ViewItem expression) {
        super(expression.getFirstVariable());
        this.type = type;
        this.expression = expression;
    }

    public ViewItem getExpression() {
        return expression;
    }

    @Override
    public Variable<?>[] getVariables() {
        return expression.getVariables();
    }

    @Override
    public Condition.Type getType() {
        return type;
    }
}
