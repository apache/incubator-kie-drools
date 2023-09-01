package org.drools.model.impl;

import org.drools.model.From2;
import org.drools.model.Variable;
import org.drools.model.functions.Function2;

public class From2Impl<A, B> implements From2<A, B>, ModelComponent {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Function2<A, B, ?> provider;
    private final boolean reactive;

    public From2Impl( Variable<A> var1, Variable<B> var2, Function2<A, B, ?> provider ) {
        this(var1, var2, provider, false);
    }

    public From2Impl( Variable<A> var1, Variable<B> var2, Function2<A, B, ?> provider, boolean reactive ) {
        this.var1 = var1;
        this.var2 = var2;
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
    public Function2<A, B, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof From2Impl) ) return false;

        From2Impl<?,?> from = ( From2Impl<?,?> ) o;

        if ( reactive != from.reactive ) return false;
        if ( !ModelComponent.areEqualInModel( var1, from.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, from.var2 ) ) return false;
        return provider != null ? provider.equals( from.provider ) : from.provider == null;
    }
}
