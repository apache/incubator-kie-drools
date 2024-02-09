package org.drools.core.positional;

public class Functions {

    interface Function1<A, R>  {
        R apply(A a);
    }

    interface Function2<A, B, R>  {
        R apply(A a, B b);
    }

    interface Function3<A, B, C, R>  {
        R apply(A a, B b, C c);
    }

    interface Function4<A, B, C, D, R>  {
        R apply(A a, B b, C c, D d);
    }

    interface Function5<A, B, C, D, E, R>  {
        R apply(A a, B b, C c, D d, E e);
    }

    interface Function6<A, B, C, D, E, F, R>  {
        R apply(A a, B b, C c, D d, E e, F f);
    }
}
