package org.drools.model.view;

import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate7;

public class Expr7ViewItemImpl<A, B, C, D, E, F, G> extends AbstractExprViewItem<A> implements ExprNViewItem<A> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Predicate7<A, B, C, D, E, F, G> predicate;

    // with 3 elements we don't implement INDEXes

    public Expr7ViewItemImpl( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Predicate7<A, B, C, D, E, F, G> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.predicate = predicate;
    }

    public Expr7ViewItemImpl( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Predicate7<A, B, C, D, E, F, G> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.predicate = predicate;
    }

    public Predicate7<A, B, C, D, E, F, G> getPredicate() {
        return predicate;
    }

    public Variable<B> getVar2() {
        return var2;
    }

    public Variable<C> getVar3() {
        return var3;
    }

    public Variable<D> getVar4() {
        return var4;
    }

    public Variable<E> getVar5() {
        return var5;
    }

    public Variable<F> getVar6() {
        return var6;
    }

    public Variable<G> getVar7() {
        return var7;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3(), getVar4(), getVar5(), getVar6(), getVar7()};
    }

    @Override
    public Type getType() {
        return Type.PATTERN;
    }

}
