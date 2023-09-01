package org.drools.verifier.api;

public interface StatusUpdate {

    void update(final int currentStartIndex,
                final int endIndex,
                final int size);
}
