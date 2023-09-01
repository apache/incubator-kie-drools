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
