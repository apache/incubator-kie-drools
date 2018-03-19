package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate1<A> extends Serializable {
    boolean test(A a) throws Exception;

    class Impl<A> extends IntrospectableLambda implements Predicate1<A> {

        private final Predicate1<A> predicate;

        public Impl(Predicate1<A> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a) throws Exception {
            return predicate.test(a);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
