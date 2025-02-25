package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate17;

// Continue defining Expr17ViewItemImpl
public class Expr17ViewItemImpl<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> extends AbstractExprViewItem<A> implements ExprNViewItem<A> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Variable<H> var8;
    private final Variable<I> var9;
    private final Variable<J> var10;
    private final Variable<K> var11;
    private final Variable<L> var12;
    private final Variable<M> var13;
    private final Variable<N> var14;
    private final Variable<O> var15;
    private final Variable<P> var16;
    private final Variable<Q> var17;
    private final Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> predicate;

    public Expr17ViewItemImpl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                              Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12, Variable<M> var13, Variable<N> var14, Variable<O> var15, Variable<P> var16, Variable<Q> var17,
                              Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.var11 = var11;
        this.var12 = var12;
        this.var13 = var13;
        this.var14 = var14;
        this.var15 = var15;
        this.var16 = var16;
        this.var17 = var17;
        this.predicate = predicate;
    }

    public Expr17ViewItemImpl(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6,
                              Variable<G> var7, Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12, Variable<M> var13, Variable<N> var14, Variable<O> var15, Variable<P> var16, Variable<Q> var17,
                              Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.var11 = var11;
        this.var12 = var12;
        this.var13 = var13;
        this.var14 = var14;
        this.var15 = var15;
        this.var16 = var16;
        this.var17 = var17;
        this.predicate = predicate;
    }

    public Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> getPredicate() {
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

    public Variable<H> getVar8() {
        return var8;
    }

    public Variable<I> getVar9() {
        return var9;
    }

    public Variable<J> getVar10() {
        return var10;
    }

    public Variable<K> getVar11() {
        return var11;
    }

    public Variable<L> getVar12() {
        return var12;
    }

    public Variable<M> getVar13() {
        return var13;
    }

    public Variable<N> getVar14() {
        return var14;
    }

    public Variable<O> getVar15() {
        return var15;
    }

    public Variable<P> getVar16() {
        return var16;
    }

    public Variable<Q> getVar17() {
        return var17;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3(), getVar4(), getVar5(), getVar6(), getVar7(), getVar8(), getVar9(), getVar10(), getVar11(), getVar12(), getVar13(), getVar14(), getVar15(), getVar16(), getVar17()};
    }

    @Override
    public Condition.Type getType() {
        return Condition.Type.PATTERN;
    }

}
