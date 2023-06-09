package org.drools.core.positional;

public class Predicates {

    interface Predicate1<A>  {
        boolean test(A a);
    }

    interface Predicate2<A, B>  {
        boolean test(A a, B b);
    }

    interface Predicate3<A, B, C>  {
        boolean test(A a, B b, C c);
    }

    interface Predicate4<A, B, C, D>  {
        boolean test(A a, B b, C c, D d);
    }

    interface Predicate5<A, B, C, D, E>  {
        boolean test(A a, B b, C c, D d, E e);
    }

    interface Predicate6<A, B, C, D, E, F>  {
        boolean test(A a, B b, C c, D d, E e, F f);
    }
}
