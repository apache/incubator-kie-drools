package org.drools.model.functions;

import java.io.Serializable;

public interface Function2<A, B, R> extends Serializable {
    R apply(A a, B b);

    class Impl<A,B,R> extends IntrospectableLambda implements Function2<A,B,R> {

        private final Function2<A,B,R> function;

        public Impl(Function2<A,B,R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b) {
            return function.apply(a, b);
        }

        @Override
        protected Object getLambda() {
            return function;
        }
    }
}
