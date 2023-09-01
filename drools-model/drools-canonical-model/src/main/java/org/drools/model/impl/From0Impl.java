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
