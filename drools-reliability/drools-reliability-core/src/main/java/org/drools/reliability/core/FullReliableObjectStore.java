package org.drools.reliability.core;

import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Storage;

public class FullReliableObjectStore extends IdentityObjectStore {

    private final transient Storage<Long, StoredObject> storage;

    public FullReliableObjectStore(){
        super();
        this.storage = null;
    }

    public FullReliableObjectStore(Storage<Long, StoredObject> storage) {
        super();
        this.storage = storage;
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        super.addHandle(handle, object);
        putIntoPersistedCache(handle, handle.hasMatches());
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        removeFromPersistedCache(handle.getObject());
        super.removeHandle(handle);
    }

    void putIntoPersistedCache(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = new SerializableStoredObject(object, propagated);
        storage.put(getHandleForObject(object).getId(), storedObject);
    }

    void removeFromPersistedCache(Object object) {
        InternalFactHandle fh = getHandleForObject(object);
        if (fh != null) {
            storage.remove(fh.getId());
        }
    }

    public void reInit() {
        for (StoredObject entry : storage.values()) {
            super.addHandle(getHandleForObject(entry.getObject()),entry.getObject());
        }
    }
}
