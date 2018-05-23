/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.impl;

import org.drools.model.From;
import org.drools.model.Variable;
import org.drools.model.functions.Function0;
import org.drools.model.functions.Function1;

public class FromSupplierImpl<T> implements From<T>,
                                            ModelComponent {

    private final Function0<T> supplier;
    private final boolean reactive;

    public FromSupplierImpl(Function0<T> provider) {
        this(provider, false);
    }

    public FromSupplierImpl(Function0<T> supplier, boolean reactive) {
        this.supplier = supplier;
        this.reactive = reactive;
    }

    @Override
    public Variable<T> getVariable() {
        return null;
    }

    @Override
    public Function1<T, ?> getProvider() {
        return null;
    }

    @Override
    public Function0<T> getSupplier() {
        return supplier;
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
        if (!(o instanceof FromSupplierImpl)) {
            return false;
        }

        FromSupplierImpl<?> from = (FromSupplierImpl<?>) o;

        if (reactive != from.reactive) {
            return false;
        }
        return supplier != null ? supplier.equals(from.supplier) : from.supplier == null;
    }
}
