package org.drools.model;

public interface AccumulatePattern<T> extends Pattern<T> {

    AccumulateFunction<T, ?, ?>[] getFunctions();
}
