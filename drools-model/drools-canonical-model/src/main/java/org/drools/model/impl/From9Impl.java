package org.drools.model.impl;

import org.drools.model.From9;
import org.drools.model.Variable;
import org.drools.model.functions.Function9;

public class From9Impl<A, B, C, D, E, F, G, H, I> implements From9<A, B, C, D, E, F, G, H, I>, ModelComponent {
    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Variable<H> var8;
    private final Variable<I> var9;
    private final Function9<A, B, C, D, E, F, G, H, I, ?> provider;
    private final boolean reactive;

    public From9Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Variable<H> var8, Variable<I> var9, Function9<A, B, C, D, E, F, G, H, I, ?> provider) {
        this(var1, var2, var3, var4, var5, var6, var7, var8, var9, provider, false);
    }

    public From9Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Variable<H> var8, Variable<I> var9, Function9<A, B, C, D, E, F, G, H, I, ?> provider, boolean reactive) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.provider = provider;
        this.reactive = reactive;
    }

    @Override
    public Variable<A> getVariable() {
        return var1;
    }

    @Override
    public Variable<B> getVariable2() {
        return var2;
    }

    @Override
    public Variable<C> getVariable3() {
        return var3;
    }

    @Override
    public Variable<D> getVariable4() {
        return var4;
    }

    @Override
    public Variable<E> getVariable5() {
        return var5;
    }

    @Override
    public Variable<F> getVariable6() {
        return var6;
    }

    @Override
    public Variable<G> getVariable7() {
        return var7;
    }

    @Override
    public Variable<H> getVariable8() {
        return var8;
    }

    @Override
    public Variable<I> getVariable9() {
        return var9;
    }

    @Override
    public Function9<A, B, C, D, E, F, G, H, I, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) return true;
        if (!(o instanceof From9Impl)) return false;

        From9Impl<?, ?, ?, ?, ?, ?, ?, ?, ?> from = (From9Impl<?, ?, ?, ?, ?, ?, ?, ?, ?>) o;

        if (reactive != from.reactive) return false;
        if (!ModelComponent.areEqualInModel(var1, from.var1)) return false;
        if (!ModelComponent.areEqualInModel(var2, from.var2)) return false;
        if (!ModelComponent.areEqualInModel(var3, from.var3)) return false;
        if (!ModelComponent.areEqualInModel(var4, from.var4)) return false;
        if (!ModelComponent.areEqualInModel(var5, from.var5)) return false;
        if (!ModelComponent.areEqualInModel(var6, from.var6)) return false;
        if (!ModelComponent.areEqualInModel(var7, from.var7)) return false;
        if (!ModelComponent.areEqualInModel(var8, from.var8)) return false;
        if (!ModelComponent.areEqualInModel(var9, from.var9)) return false;
        return provider != null ? provider.equals(from.provider) : from.provider == null;
    }
}
