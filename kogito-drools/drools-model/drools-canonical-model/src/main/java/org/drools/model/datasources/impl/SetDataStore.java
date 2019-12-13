/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.datasources.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.model.datasources.PassiveDataStore;

public class SetDataStore<T> implements PassiveDataStore<T> {

    private final Set<T> store = new HashSet<T>();

    public static <T> PassiveDataStore<T> storeOf( T... items ) {
        SetDataStore<T> dataStore = new SetDataStore();
        for (T item : items) {
            dataStore.insert(item);
        }
        return dataStore;
    }

    @Override
    public void insert(T obj) {
        store.add(obj);
    }

    @Override
    public void update(T obj) {
        if (store.remove(obj)) {
            throw new RuntimeException(obj + " not present");
        }
        store.add(obj);
    }

    @Override
    public void delete(T obj) {
        store.remove(obj);
    }

    @Override
    public Collection<T> getObjects() {
        return store;
    }
}