package org.drools.core.positional;

public class VoidFunctions {

    interface VoidFunction1<A>  {
        void apply(A a);
    }

    interface VoidFunction2<A, B>  {
        void apply(A a, B b);
    }

    interface VoidFunction3<A, B, C>  {
        void apply(A a, B b, C c);
    }

    interface VoidFunction4<A, B, C, D>  {
        void apply(A a, B b, C c, D d);
    }

    interface VoidFunction5<A, B, C, D, E>  {
        void apply(A a, B b, C c, D d, E e);
    }

    interface VoidFunction6<A, B, C, D, E, F>  {
        void apply(A a, B b, C c, D d, E e, F f);
    }
}
