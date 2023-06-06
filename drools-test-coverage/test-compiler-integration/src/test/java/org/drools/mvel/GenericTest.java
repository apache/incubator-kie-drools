package org.drools.mvel;

public class GenericTest {

    public static class A {
        public void go() {

        }
    }

    public static class B extends A {
        public void stop() {

        }
    }

    public static interface FactHandle1<T> {
        public T getObject();

        <K> K as(Class<K> klass) throws ClassCastException;
    }

    public static interface FactHandle2 {
        public Object getObject();

        <K> K as(Class<K> klass) throws ClassCastException;
    }

    public void test1() {
        FactHandle1<A> fh = null;
        fh.getObject().go();
        fh.as(B.class).stop();
    }

    public void test2() {
        FactHandle2 fh = null;
        ((A)fh.getObject()).go();
        fh.as(B.class).stop();
    }
}
