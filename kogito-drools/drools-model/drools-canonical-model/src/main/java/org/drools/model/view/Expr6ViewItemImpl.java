package org.drools.model.view;

import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate6;

public class Expr6ViewItemImpl<A, B, C, D, E, F> extends AbstractExprViewItem<A> implements ExprNViewItem<A> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Predicate6<A, B, C, D, E, F> predicate;

    // with 3 elements we don't implement INDEXes

    public Expr6ViewItemImpl( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.predicate = predicate;
    }

    public Expr6ViewItemImpl( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.predicate = predicate;
    }

    public Predicate6<A, B, C, D, E, F> getPredicate() {
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

    public Variable<F> getSixthVariable() {
        return var6;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getSecondVariable(), getThirdVariable(), getForthVariable(), getFifthVariable(), getSixthVariable()};
    }

    @Override
    public Type getType() {
        return Type.PATTERN;
    }

}
