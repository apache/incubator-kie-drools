package org.drools.model.view;

import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate5;

public class Expr5ViewItemImpl<A, B, C, D, E> extends AbstractExprViewItem<A> implements ExprNViewItem<A> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Predicate5<A, B, C, D, E> predicate;

    // with 3 elements we don't implement INDEXes

    public Expr5ViewItemImpl( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public Expr5ViewItemImpl( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public Predicate5<A, B, C, D, E> getPredicate() {
        return predicate;
    }

    public Variable<B> getSecondVariable() {
        return var2;
    }

    public Variable<C> getThirdVariable() {
        return var3;
    }

    public Variable<D> getForthVariable() {
        return var4;
    }

    public Variable<E> getFifthVariable() {
        return var5;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getSecondVariable(), getThirdVariable(), getForthVariable(), getFifthVariable()};
    }

    @Override
    public Type getType() {
        return Type.PATTERN;
    }

}
