package org.drools.model;

public interface BetaIndexN<A, V> extends Index<A, V> {

    Class<?> getRightReturnType();

    int getArity();
}