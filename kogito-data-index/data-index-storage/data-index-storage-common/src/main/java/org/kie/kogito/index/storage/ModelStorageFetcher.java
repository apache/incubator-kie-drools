package org.kie.kogito.index.storage;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageFetcher;
import org.kie.kogito.persistence.api.query.Query;

import io.smallrye.mutiny.Multi;

public abstract class ModelStorageFetcher<V> implements StorageFetcher<String, V> {

    protected final Storage<String, V> storage;

    public ModelStorageFetcher(Storage<String, V> storage) {
        this.storage = storage;
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
    public Multi<String> objectRemovedListener() {
        return storage.objectRemovedListener();
    }

    @Override
    public Query<V> query() {
        return storage.query();
    }

    @Override
    public V get(String key) {
        return storage.get(key);
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
