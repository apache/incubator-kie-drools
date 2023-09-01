package org.drools.verifier.core.maps;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class RawMultiMap<Key extends Comparable, Value, ListType extends List<Value>>
        implements MultiMap<Key, Value, ListType> {

    protected final TreeMap<Key, ListType> map;
    private NewSubMapProvider<Value, ListType> newSubMapProvider;

    public RawMultiMap(final NewSubMapProvider<Value, ListType> newSubMapProvider) {
        this.map = new TreeMap();
        this.newSubMapProvider = newSubMapProvider;
    }

    protected RawMultiMap(final SortedMap<Key, ListType> map,
                          final NewSubMapProvider<Value, ListType> newSubMapProvider) {
        this.map = new TreeMap<>(map);
        this.newSubMapProvider = newSubMapProvider;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    private ListType resolveInnerList(final Key key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            final ListType list = getNewSubMap();
            map.put(key,
                    list);
            return list;
        }
    }

    public boolean containsKey(final Key key) {
        return map.containsKey(key);
    }

    public void put(final Key key,
                    final int index,
                    final Value value) {
        resolveInnerList(key).add(index,
                                  value);
    }

    public boolean put(final Key key,
                       final Value value) {
        return resolveInnerList(key).add(value);
    }

    public void move(final Set<Key> oldKeys,
                     final Set<Key> newKeys,
                     final Value value) {

        for (final Key oldKey : oldKeys) {
            removeValue(oldKey,
                        value);
        }

        for (final Key newKey : newKeys) {
            put(newKey,
                value);
        }
    }

    private ListType getNewSubMap() {
        return newSubMapProvider.getNewSubMap();
    }

    public ListType get(final Key key) {
        return map.get(key);
    }

    public boolean addAllValues(final Key key,
                                final Collection<Value> values) {
        if (map.containsKey(key)) {
            return map.get(key).addAll(values);
        } else {
            final ListType set = getNewSubMap();
            set.addAll(values);
            map.put(key, set);
            return true;
        }
    }

    @Override
    public void addChangeListener(final MultiMapChangeHandler<Key, Value> multiMapChangeHandler) {
        throw new UnsupportedOperationException("This map " + this.getClass().getName() + " can not have change handlers.");
    }

    public Collection<Value> remove(final Key key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public Set<Key> keySet() {
        return map.keySet();
    }

    public Collection<Value> allValues() {
        final ListType allValues = getNewSubMap();

        for (final Key key : keySet()) {
            final ListType collection = get(key);
            if (collection != null) {
                allValues.addAll(collection);
            }
        }

        return allValues;
    }

    public Key lastKey() {
        return map.lastKey();
    }

    @Override
    public MultiMap<Key, Value, ListType> subMap(final Key fromKey, final boolean fromInclusive, final Key toKey, final boolean toInclusive) {
        return new RawMultiMap<>(map.subMap(fromKey,
                                            fromInclusive,
                                            toKey,
                                            toInclusive),
                                 newSubMapProvider);
    }

    public void removeValue(final Key key,
                            final Value value) {
        get(key).remove(value);
    }

    public Key firstKey() {
        return map.firstKey();
    }

    public void clear() {
        map.clear();
    }

    public void putAllValues(final Key key,
                             final Collection<Value> values) {
        final ListType newSubMap = getNewSubMap();
        newSubMap.addAll(values);
        map.put(key,
                newSubMap);
    }
}
