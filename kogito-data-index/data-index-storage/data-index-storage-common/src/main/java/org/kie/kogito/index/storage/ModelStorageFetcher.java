/*
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
package org.kie.kogito.index.storage;

import java.util.function.Function;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageFetcher;
import org.kie.kogito.persistence.api.query.Query;

import io.smallrye.mutiny.Multi;

public abstract class ModelStorageFetcher<K, V> implements StorageFetcher<K, V> {

    protected final Storage<String, V> storage;

    private final Function<K, String> toString;
    private final Function<String, K> fromString;

    public ModelStorageFetcher(Storage<String, V> storage) {
        this(storage, Object::toString, s -> (K) s);
    }

    public ModelStorageFetcher(Storage<String, V> storage, Function<K, String> toString, Function<String, K> fromString) {
        this.storage = storage;
        this.toString = toString;
        this.fromString = fromString;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        return storage.objectCreatedListener();
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        return storage.objectUpdatedListener();
    }

    @Override
    public Multi<K> objectRemovedListener() {
        return storage.objectRemovedListener().map(fromString);
    }

    @Override
    public Query<V> query() {
        return storage.query();
    }

    @Override
    public V get(K key) {
        return storage.get(toString.apply(key));
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
