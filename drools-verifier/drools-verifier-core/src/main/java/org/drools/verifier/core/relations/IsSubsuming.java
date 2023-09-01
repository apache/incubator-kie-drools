package org.drools.verifier.core.relations;

public interface IsSubsuming<T> {

    boolean subsumes(final T other);
}
