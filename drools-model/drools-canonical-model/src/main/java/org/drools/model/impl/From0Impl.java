package org.drools.model.impl;

import org.drools.model.From0;
import org.drools.model.Variable;
import org.drools.model.functions.Function0;

public class From0Impl<T> implements From0<T>, ModelComponent {

    private final Function0<T> provider;
    private final boolean reactive;

    public From0Impl( Function0<T> provider) {
        this(provider, false);
    }

    public From0Impl( Function0<T> provider, boolean reactive) {
        this.provider = provider;
        this.reactive = reactive;
    }

    @Override
    public Variable<T> getVariable() {
        return null;
    }

    @Override
    public Function0<T> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof From0Impl)) {
            return false;
        }

        From0Impl<?> from = (From0Impl<?> ) o;

        if (reactive != from.reactive) {
            return false;
        }
        return provider != null ? provider.equals( from.provider ) : from.provider == null;
    }
}
