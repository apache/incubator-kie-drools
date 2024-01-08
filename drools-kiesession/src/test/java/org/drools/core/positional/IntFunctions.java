package org.drools.core.positional;

public class IntFunctions {

    interface IntFunction1<A>  {
        int apply(A a);
    }

    interface IntFunction2<A, B>  {
        int apply(A a, B b);
    }

    interface IntFunction3<A, B, C>  {
        int apply(A a, B b, C c);
    }

    interface IntFunction4<A, B, C, D>  {
        int apply(A a, B b, C c, D d);
    }

    interface IntFunction5<A, B, C, D, E>  {
        int apply(A a, B b, C c, D d, E e);
    }

    interface IntFunction6<A, B, C, D, E, F>  {
        int apply(A a, B b, C c, D d, E e, F f);
    }
}
