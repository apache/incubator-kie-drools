package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> extends Serializable {

    boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p) throws Exception;

    default Predicate16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) -> !test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> extends IntrospectableLambda implements Predicate16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {

        private final Predicate16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> predicate;

        public Impl(Predicate16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }

        @Override
        public PredicateInformation predicateInformation() {
            return predicate.predicateInformation();
        }
    }
}
