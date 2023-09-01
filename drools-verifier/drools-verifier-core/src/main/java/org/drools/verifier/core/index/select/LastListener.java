package org.drools.verifier.core.index.select;

public interface LastListener<T> {

    void onLastChanged(final T last);
}
