package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate3;

public class Expr3ViewItemImpl<A, B, C> extends AbstractExprViewItem<A> implements ExprNViewItem<A> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Predicate3<A, B, C> predicate;

    // with 3 elements we don't implement INDEXes

    public Expr3ViewItemImpl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Expr3ViewItemImpl(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Predicate3<A, B, C> getPredicate() {
        return predicate;
    }

    public Variable<B> getVar2() {
        return var2;
    }

    public Variable<C> getVar3() {
        return var3;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3()};
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

}
