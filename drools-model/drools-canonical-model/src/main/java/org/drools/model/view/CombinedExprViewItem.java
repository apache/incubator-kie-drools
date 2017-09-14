package org.drools.model.view;

import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Variable;

public class CombinedExprViewItem<T> extends AbstractExprViewItem<T> {

    private final Condition.Type type;
    private final ExprViewItem[] expressions;

    public CombinedExprViewItem(Condition.Type type, ExprViewItem[] expressions) {
        super(getCombinedVariable(expressions));
        this.type = type;
        this.expressions = expressions;
    }

    public ExprViewItem[] getExpressions() {
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

    private static Variable getCombinedVariable(ExprViewItem... expressions) {
        Variable var = null;
        for (ExprViewItem expression : expressions) {
            if (var == null) {
                var = expression.getFirstVariable();
            } else if (var != expression.getFirstVariable()) {
                return null;
            }
        }
        return var;
    }
}
