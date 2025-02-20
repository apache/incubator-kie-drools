package org.drools.model.impl;

import org.drools.model.From6;
import org.drools.model.Variable;
import org.drools.model.functions.Function6;

public class From6Impl<A, B, C, D, E, F> implements From6<A, B, C, D, E, F>, ModelComponent {
    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Function6<A, B, C, D, E, F, ?> provider;
    private final boolean reactive;

    public From6Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Function6<A, B, C, D, E, F, ?> provider) {
        this(var1, var2, var3, var4, var5, var6, provider, false);
    }

    public From6Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Function6<A, B, C, D, E, F, ?> provider, boolean reactive) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
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
    public Function6<A, B, C, D, E, F, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) return true;
        if (!(o instanceof From6Impl)) return false;

        From6Impl<?, ?, ?, ?, ?, ?> from = (From6Impl<?, ?, ?, ?, ?, ?>) o;

        if (reactive != from.reactive) return false;
        if (!ModelComponent.areEqualInModel(var1, from.var1)) return false;
        if (!ModelComponent.areEqualInModel(var2, from.var2)) return false;
        if (!ModelComponent.areEqualInModel(var3, from.var3)) return false;
        if (!ModelComponent.areEqualInModel(var4, from.var4)) return false;
        if (!ModelComponent.areEqualInModel(var5, from.var5)) return false;
        if (!ModelComponent.areEqualInModel(var6, from.var6)) return false;
        return provider != null ? provider.equals(from.provider) : from.provider == null;
    }
}
