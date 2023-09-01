package org.drools.model.functions;

import java.io.Serializable;

public interface Function4<A, B, C, D, R> extends Serializable {

    R apply(A a, B b, C c, D d);

    class Impl<A, B, C, D, R> extends IntrospectableLambda implements Function4<A, B, C, D, R> {

        private final Function4<A, B, C, D, R> function;

        public Impl(Function4<A, B, C, D, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d) {
            return function.apply(a, b, c, d);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
