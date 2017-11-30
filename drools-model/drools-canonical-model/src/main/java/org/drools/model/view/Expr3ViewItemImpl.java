package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate3;

public class Expr3ViewItemImpl<T, U, X> extends AbstractExprViewItem<T> implements ExprViewItem<T> {

    private final Variable<U> var2;
    private final Variable<X> var3;
    private final Predicate3<T, U, X> predicate;

    // with 3 elements we don't implement INDEXes

    public Expr3ViewItemImpl(Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Expr3ViewItemImpl(String exprId, Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Predicate3<T, U, X> getPredicate() {
        return predicate;
    }

    public Variable<U> getSecondVariable() {
        return var2;
    }

    public Variable<X> getThirdVariable() {
        return var3;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getSecondVariable(), getThirdVariable()};
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

}
