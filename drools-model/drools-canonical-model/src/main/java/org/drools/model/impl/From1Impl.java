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

import org.drools.model.From1;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public class From1Impl<A> implements From1<A>, ModelComponent {

    private final Variable<A> variable;
    private final Function1<A, ?> provider;
    private final boolean reactive;

    public From1Impl( Variable<A> variable ) {
        this(variable, null, false);
    }

    public From1Impl( Variable<A> variable, Function1<A, ?> provider ) {
        this(variable, provider, false);
    }

    public From1Impl( Variable<A> variable, Function1<A, ?> provider, boolean reactive ) {
        this.variable = variable;
        this.provider = provider;
        this.reactive = reactive;
    }

    @Override
    public Variable<A> getVariable() {
        return variable;
    }

    @Override
    public Function1<A, ?> getProvider() {
        return provider;
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof From1Impl) ) return false;

        From1Impl<?> from = ( From1Impl<?> ) o;

        if ( reactive != from.reactive ) return false;
        if ( !ModelComponent.areEqualInModel( variable, from.variable ) ) return false;
        return provider != null ? provider.equals( from.provider ) : from.provider == null;
    }
}
