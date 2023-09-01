package org.drools.verifier.core.maps;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MultiMap<Key extends Comparable, Value, ListType extends List<Value>> {

    /**
     * Merges the second MultiMap into the first.
     */
    static <Key extends Comparable, V, ListType extends List<V>> MultiMap<Key, V, ListType> merge(final MultiMap<Key, V, ListType> first,
                                                                                                  final MultiMap<Key, V, ListType> second) {

        if (first instanceof ChangeHandledMultiMap) {
            ((ChangeHandledMultiMap) first).addToCounter();
        }

        for (final Key key : second.keySet()) {
            first.putAllValues(key,
                               second.get(key));
        }

        if (first instanceof ChangeHandledMultiMap) {
            ((ChangeHandledMultiMap) first).fire();
        }

        return first;
    }

    boolean put(final Key key,
                final Value value);

    int size();

    void move(final Set<Key> oldKeys,
              final Set<Key> newKeys,
              final Value value);

    Set<Key> keySet();

    ListType get(final Key value);

    boolean addAllValues(final Key value,
                         final Collection<Value> values);

    Collection<Value> remove(final Key value);

    boolean isEmpty();

    void addChangeListener(final MultiMapChangeHandler<Key, Value> multiMapChangeHandler);

    Collection<Value> allValues();

    boolean containsKey(final Key value);

    Key firstKey();

    Key lastKey();

    MultiMap<Key, Value, ListType> subMap(final Key fromKey,
                                          final boolean fromInclusive,
                                          final Key toKey,
                                          final boolean toInclusive);

    void removeValue(final Key key,
                     final Value value);

    void clear();

    void putAllValues(final Key key,
                      final Collection<Value> collection);
}
