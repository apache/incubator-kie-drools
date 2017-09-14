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