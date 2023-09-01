package org.drools.verifier.core.maps;

import java.util.List;

public interface MultiMapChangeHandler<V extends Comparable, T> {

    void onChange(final ChangeSet<V, T> changeSet);

    class ChangeSet<V extends Comparable, T> {

        final MultiMap<V, T, List<T>> added = MultiMapFactory.make();
        final MultiMap<V, T, List<T>> removed = MultiMapFactory.make();

        public MultiMap<V, T, List<T>> getAdded() {
            return added;
        }

        public MultiMap<V, T, List<T>> getRemoved() {
            return removed;
        }
    }
}
