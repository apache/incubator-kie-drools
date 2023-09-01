package org.drools.verifier.core.maps;

import org.drools.verifier.core.index.keys.Key;

public interface KeyChangeListener<T> {

    void update(final Key oldKey,
                final Key newKey,
                final T t);
}
