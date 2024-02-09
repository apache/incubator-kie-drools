/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

class ChangeHandledMultiMap<V extends Comparable, T, ListType extends List<T>>
        implements MultiMap<V, T, ListType> {

    private final MultiMap<V, T, ListType> map;

    private List<MultiMapChangeHandler<V, T>> changeHandlers = new ArrayList<>();

    private MultiMapChangeHandler.ChangeSet<V, T> changeSet = new MultiMapChangeHandler.ChangeSet<>();
    private int counter = 0;

    public ChangeHandledMultiMap(final MultiMap<V, T, ListType> map) {
        this.map = map;
    }

    @Override
    public boolean put(final V value,
                       final T t) {
        addToCounter();

        final boolean put = map.put(value,
                                    t);

        addToChangeSet(value,
                       t);

        fire();

        return put;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<V> keySet() {
        return map.keySet();
    }

    @Override
    public ListType get(final V key) {
        return map.get(key);
    }

    @Override
    public boolean addAllValues(final V value,
                                final Collection<T> tCollection) {
        addToCounter();

        final boolean allValuesAdded = map.addAllValues(value,
                                                        tCollection);

        for (final T t : tCollection) {
            addToChangeSet(value, t);
        }

        fire();

        return allValuesAdded;
    }

    @Override
    public Collection<T> remove(final V value) {
        addToCounter();
        for (final T t : get(value)) {
            addRemovedToChangeSet(value, t);
        }

        final Collection<T> remove = map.remove(value);

        fire();

        return remove;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void addChangeListener(final MultiMapChangeHandler<V, T> changeHandler) {
        changeHandlers.add(changeHandler);
    }

    @Override
    public Collection<T> allValues() {
        return map.allValues();
    }

    void addToCounter() {
        counter++;
    }

    protected void fire() {
        if (counter == 1) {
            for (final MultiMapChangeHandler<V, T> changeHandler : changeHandlers) {
                changeHandler.onChange(changeSet);
            }

            changeSet = new MultiMapChangeHandler.ChangeSet<>();
        }

        counter--;
    }

    private void addToChangeSet(final V value,
                                final T t) {

        if (!changeHandlers.isEmpty()) {
            changeSet.added.put(value, t);
        }
    }

    private void addRemovedToChangeSet(final V value,
                                       final T t) {
        if (!changeHandlers.isEmpty()) {
            changeSet.removed.put(value,
                                  t);
        }
    }

    @Override
    public void move(final Set<V> oldKeys,
                     final Set<V> newKeys,
                     final T t) {

        addToCounter();

        for (final V oldKey : oldKeys) {
            removeValue(oldKey,
                        t);
        }

        for (final V newKey : newKeys) {
            put(newKey,
                t);
        }

        fire();
    }

    @Override
    public boolean containsKey(final V key) {
        return map.containsKey(key);
    }

    @Override
    public V firstKey() {
        return map.firstKey();
    }

    @Override
    public V lastKey() {
        return map.lastKey();
    }

    @Override
    public MultiMap<V, T, ListType> subMap(final V fromKey,
                                           final boolean fromInclusive,
                                           final V toKey,
                                           final boolean toInclusive) {
        return map.subMap(fromKey, fromInclusive,
                          toKey, toInclusive);
    }

    @Override
    public void removeValue(final V value,
                            final T t) {
        addToCounter();

        map.removeValue(value,
                        t);

        addRemovedToChangeSet(value,
                              t);

        fire();
    }

    @Override
    public void clear() {
        addToCounter();

        for (final V value : map.keySet()) {
            for (final T t : map.get(value)) {
                addRemovedToChangeSet(value,
                                      t);
            }
        }

        map.clear();

        fire();
    }

    @Override
    public void putAllValues(final V value,
                             final Collection<T> ts) {
        addToCounter();

        for (final T t : ts) {
            addToChangeSet(value,
                           t);
        }

        map.putAllValues(value,
                         ts);
        fire();
    }
}
