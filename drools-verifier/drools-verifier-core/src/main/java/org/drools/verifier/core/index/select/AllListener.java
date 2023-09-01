package org.drools.verifier.core.index.select;

import java.util.Collection;

public interface AllListener<T> {

    void onAllChanged(final Collection<T> all);
}
