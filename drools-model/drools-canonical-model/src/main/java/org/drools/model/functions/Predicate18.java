package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> extends Serializable {

    boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p, Q q, R r) throws Exception;

    default Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) -> !test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r);
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> extends IntrospectableLambda implements Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {

        private final Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> predicate;

        public Impl(Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p, Q q, R r) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r);
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
