/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.model.impl;

import org.drools.model.From7;
import org.drools.model.Variable;
import org.drools.model.functions.Function7;

public class From7Impl<A, B, C, D, E, F, G> implements From7<A, B, C, D, E, F, G>, ModelComponent {
    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Function7<A, B, C, D, E, F, G, ?> provider;
    private final boolean reactive;

    public From7Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Function7<A, B, C, D, E, F, G, ?> provider) {
        this(var1, var2, var3, var4, var5, var6, var7, provider, false);
    }

    public From7Impl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Function7<A, B, C, D, E, F, G, ?> provider, boolean reactive) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
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
    public Function7<A, B, C, D, E, F, G, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) return true;
        if (!(o instanceof From7Impl)) return false;

        From7Impl<?, ?, ?, ?, ?, ?, ?> from = (From7Impl<?, ?, ?, ?, ?, ?, ?>) o;

        if (reactive != from.reactive) return false;
        if (!ModelComponent.areEqualInModel(var1, from.var1)) return false;
        if (!ModelComponent.areEqualInModel(var2, from.var2)) return false;
        if (!ModelComponent.areEqualInModel(var3, from.var3)) return false;
        if (!ModelComponent.areEqualInModel(var4, from.var4)) return false;
        if (!ModelComponent.areEqualInModel(var5, from.var5)) return false;
        if (!ModelComponent.areEqualInModel(var6, from.var6)) return false;
        if (!ModelComponent.areEqualInModel(var7, from.var7)) return false;
        return provider != null ? provider.equals(from.provider) : from.provider == null;
    }
}
