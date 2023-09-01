package org.drools.verifier.core.relations;

public interface IsRedundant<T> {

    boolean isRedundant(final T other);
}
