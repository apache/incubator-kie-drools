package org.drools.model.view;

import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Variable;

public class CombinedExprViewItem<T> extends AbstractExprViewItem<T> {

    private final Condition.Type type;
    private final ViewItem[] expressions;

    public CombinedExprViewItem(Condition.Type type, ViewItem[] expressions) {
        super(getCombinedVariable(expressions));
        this.type = type;
        this.expressions = expressions;
    }

    public ViewItem[] getExpressions() {
        return expressions;
    }

    @Override
    public Variable<?>[] getVariables() {
        return Stream.of(expressions)
                     .flatMap( expr -> Stream.of(expr.getVariables()) )
                     .distinct()
                     .toArray(Variable[]::new);
    }

    @Override
    public Condition.Type getType() {
        return type;
    }

    private static Variable getCombinedVariable(ViewItem... expressions) {
        Variable var = null;
        for (ViewItem expression : expressions) {
            if (var == null) {
                var = expression.getFirstVariable();
            } else if (var != expression.getFirstVariable()) {
                return null;
            }
        }
        return var;
    }

    @Override
    public void setQueryExpression( boolean queryExpression ) {
        super.setQueryExpression( queryExpression );
        for (ViewItem expr : expressions) {
            if (expr instanceof AbstractExprViewItem) {
                (( AbstractExprViewItem ) expr).setQueryExpression( queryExpression );
            }
        }
    }
}
