package org.drools.verifier.core.index.keys;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.core.maps.KeyChangeListener;
import org.drools.verifier.core.maps.KeyDefinition;

public class UpdatableKey<T>
        extends Key {

    private List<KeyChangeListener<T>> keyChangeListeners = new ArrayList<>();

    public UpdatableKey(final KeyDefinition keyDefinition,
                        final Comparable value) {
        super(keyDefinition,
              value);
    }

    public UpdatableKey(final KeyDefinition keyDefinition,
                        final Values values) {
        super(keyDefinition,
              values);
    }

    public void update(final UpdatableKey newKey,
                       final T t) {
        for (final KeyChangeListener<T> keyChangeListener : keyChangeListeners) {
            keyChangeListener.update(this,
                                     newKey,
                                     t);
        }
    }

    public void addKeyChangeListener(final KeyChangeListener<T> keyChangeListener) {
        keyChangeListeners.add(keyChangeListener);
    }

    public void removeListener(final KeyChangeListener<T> keyChangeListener) {
        keyChangeListeners.remove(keyChangeListener);
    }
}
