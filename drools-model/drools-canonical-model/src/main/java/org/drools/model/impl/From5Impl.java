package org.drools.model.impl;

import org.drools.model.From5;
import org.drools.model.Variable;
import org.drools.model.functions.Function5;

public class From5Impl<A, B, C, D, E> implements From5<A, B, C, D, E>, ModelComponent {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Function5<A, B, C, D, E, ?> provider;
    private final boolean reactive;

    public From5Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Function5<A, B, C, D, E, ?> provider) {
        this(var1, var2, var3, var4, var5, provider, false);
    }

    public From5Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Function5<A, B, C, D, E, ?> provider, boolean reactive) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
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
    public Function5<A, B, C, D, E, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) return true;
        if (!(o instanceof From5Impl)) return false;

        From5Impl<?, ?, ?, ?, ?> from = (From5Impl<?, ?, ?, ?, ?>) o;

        if (reactive != from.reactive) return false;
        if (!ModelComponent.areEqualInModel(var1, from.var1)) return false;
        if (!ModelComponent.areEqualInModel(var2, from.var2)) return false;
        if (!ModelComponent.areEqualInModel(var3, from.var3)) return false;
        if (!ModelComponent.areEqualInModel(var4, from.var4)) return false;
        if (!ModelComponent.areEqualInModel(var5, from.var5)) return false;
        return provider != null ? provider.equals(from.provider) : from.provider == null;
    }
}

// Similar implementations for From6Impl, From7Impl, From8Impl, From9Impl, and From10Impl

