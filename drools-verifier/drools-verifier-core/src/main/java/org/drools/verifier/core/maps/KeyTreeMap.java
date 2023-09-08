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

import java.util.List;
import java.util.TreeMap;

import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public class KeyTreeMap<T extends HasKeys> {

    private final TreeMap<KeyDefinition, MultiMap<Value, T, List<T>>> tree = new TreeMap<>();

    protected final UUIDKeySet keys = new UUIDKeySet(this);

    protected KeyChangeListener<T> keyChangeListener = new KeyChangeListener<T>() {
        @Override
        public void update(final Key oldKey,
                           final Key newKey,
                           final T t) {

            move(oldKey,
                 newKey,
                 t);
        }
    };

    public KeyTreeMap(final KeyDefinition... keyIDs) {
        PortablePreconditions.checkCondition("Should not be empty",
                                             keyIDs.length != 0);

        for (final KeyDefinition keyID : keyIDs) {
            resolveMapByKeyId(keyID);
        }
    }

    public void put(final T object) {
        PortablePreconditions.checkNotNull("Object can not be null",
                                           object);

        final UUIDKey uuidKey = UUIDKey.getUUIDKey(object.keys());

        if (keys.contains(uuidKey)) {
            throw new IllegalArgumentException("UUID already already in use. You are trying to add the same object twice.");
        }

        keys.add(uuidKey);

        for (final Key additionalKey : object.keys()) {
            put(additionalKey,
                object);
        }
    }

    private void move(final Key oldKey,
                      final Key newKey,
                      final T t) {

        if (newKey instanceof UpdatableKey) {
            ((UpdatableKey) newKey).addKeyChangeListener(keyChangeListener);
        }
        if (newKey.getKeyDefinition().isUpdatable()) {
            tree.get(newKey.getKeyDefinition()).move(oldKey.getValues(),
                                                     newKey.getValues(),
                                                     t);
        } else {
            throw new IllegalArgumentException("Key can not be updated");
        }
    }

    protected void put(final Key key,
                       final T object) {

        if (key instanceof UpdatableKey) {
            ((UpdatableKey) key).addKeyChangeListener(keyChangeListener);
        }

        final MultiMap<Value, T, List<T>> subMap = resolveMapByKeyId(key.getKeyDefinition());

        for (final Value value : key.getValues()) {
            subMap.put(value,
                       object);
        }
    }

    private void putAll(final KeyDefinition id,
                        final MultiMap<Value, T, List<T>> multiMap) {
        final MultiMap<Value, T, List<T>> subMap = resolveMapByKeyId(id);

        for (final Value value : multiMap.keySet()) {
            subMap.addAllValues(value,
                                multiMap.get(value));
        }
    }

    protected MultiMap<Value, T, List<T>> resolveMapByKeyId(final KeyDefinition id) {
        if (tree.containsKey(id)) {
            return tree.get(id);
        } else {
            final MultiMap<Value, T, List<T>> map = getMap(id);
            tree.put(id,
                     map);
            return map;
        }
    }

    private MultiMap<Value, T, List<T>> getMap(final KeyDefinition id) {
        return MultiMapFactory.make(id.isUpdatable());
    }

    public MultiMap<Value, T, List<T>> get(final KeyDefinition keyDefinition) {
        return tree.get(keyDefinition);
    }

    public void merge(final KeyTreeMap<T> keyTreeMap) {
        keys.addAll(keyTreeMap.keys);

        for (final KeyDefinition otherId : keyTreeMap.tree.keySet()) {
            putAll(otherId,
                   keyTreeMap.tree.get(otherId));
        }
    }

    protected T remove(final UUIDKey uuidKey) {
        final T item = getItemByUUID(uuidKey);

        if (item == null) {
            return null;
        } else {

            final Key[] removedKeys = uuidKey.getKeys();
            keys.remove(uuidKey);

            if (removedKeys == null) {
                return item;
            }

            for (final Key removedKey : removedKeys) {

                if (removeKeyForItem(removedKey,
                                     item)) {

                    if (removedKey instanceof UpdatableKey) {
                        ((UpdatableKey) removedKey).removeListener(keyChangeListener);
                    }
                }
            }

            return item;
        }
    }

    private boolean removeKeyForItem(final Key key,
                                     final T item) {
        final MultiMap<Value, T, List<T>> valueTMultiMap = tree
                .get(key.getKeyDefinition());

        for (final Value value : key.getValues()) {
            valueTMultiMap.get(value).remove(item);
        }

        // Clean up.l
        for (final Value value : key.getValues()) {
            if (valueTMultiMap.get(value).isEmpty()) {
                valueTMultiMap.remove(value);
            }
        }

        return true;
    }

    private T getItemByUUID(final UUIDKey uuidKey) {
        if (tree.isEmpty()) {
            return null;
        }

        final MultiMap<Value, T, List<T>> valueTMultiMap = get(uuidKey.getKeyDefinition());

        if (valueTMultiMap == null || valueTMultiMap.isEmpty()) {
            return null;
        }

        final List<T> list = valueTMultiMap.get(uuidKey.getSingleValue());

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.iterator().next();
    }
}
