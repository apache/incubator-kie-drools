package org.drools.model.functions;

public interface Function2<A, B, R> {
    R apply(A a, B b);
}
