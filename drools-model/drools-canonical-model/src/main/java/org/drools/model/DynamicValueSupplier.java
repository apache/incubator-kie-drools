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
package org.drools.model;

import org.drools.model.functions.Function0;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;

public interface DynamicValueSupplier<T> {

    Variable[] getVariables();

    T supply(Object[] args);

    class _0<R> implements DynamicValueSupplier<R> {
        private final Function0<R> f;

        public _0( Function0<R> f ) {
            this.f = f;
        }

        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public R supply( Object[] args ) {
            return f.apply();
        }
    }

    class _1<A, R> implements DynamicValueSupplier<R> {
        private final Variable<A> var1;
        private final Function1<A, R> f;

        public _1( Variable<A> var1, Function1<A, R> f ) {
            this.var1 = var1;
            this.f = f;
        }

        @Override
        public Variable[] getVariables() {
            return new Variable[] { var1 };
        }

        @Override
        public R supply( Object[] args ) {
            return f.apply( (A) args[0] );
        }
    }

    class _2<A, B, R> implements DynamicValueSupplier<R> {
        private final Variable<A> var1;
        private final Variable<B> var2;
        private final Function2<A, B, R> f;

        public _2( Variable<A> var1, Variable<B> var2, Function2<A, B, R> f ) {
            this.var1 = var1;
            this.var2 = var2;
            this.f = f;
        }

        @Override
        public Variable[] getVariables() {
            return new Variable[] { var1, var2 };
        }

        @Override
        public R supply( Object[] args ) {
            return f.apply( (A) args[0], (B) args[1] );
        }
    }
}
