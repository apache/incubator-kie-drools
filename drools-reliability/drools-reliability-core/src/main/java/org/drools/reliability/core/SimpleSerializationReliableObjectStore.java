/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.core;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Storage;

import java.util.ArrayList;
import java.util.List;

public class SimpleSerializationReliableObjectStore extends IdentityObjectStore implements SimpleReliableObjectStore {

    protected final transient Storage<Long, StoredObject> storage;

    protected boolean reInitPropagated = false;

    public SimpleSerializationReliableObjectStore() {
        throw new UnsupportedOperationException("This constructor should never be called");
    }

    public SimpleSerializationReliableObjectStore(Storage<Long, StoredObject> storage) {
        super();
        this.storage = storage;
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        super.addHandle(handle, object);
        putIntoPersistedStorage(handle, handle.hasMatches());
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        removeFromPersistedStorage(handle.getObject());
        super.removeHandle(handle);
    }

    @Override
    public List<StoredObject> reInit(InternalWorkingMemory session, InternalWorkingMemoryEntryPoint ep) {
        reInitPropagated = true;
        List<StoredObject> propagated = new ArrayList<>();
        List<StoredObject> notPropagated = new ArrayList<>();
        for (StoredObject entry : storage.values()) {
            if (entry.isPropagated()) {
                propagated.add(entry);
            } else {
                notPropagated.add(entry);
            }
        }
        storage.clear();

        // fact handles with a match have been already propagated in the original session, so they shouldn't fire
        propagated.forEach(obj -> obj.repropagate(ep));
        session.fireAllRules(match -> false);
        reInitPropagated = false;

        // fact handles without any match have never been propagated in the original session, so they should fire
        return notPropagated;
    }

    @Override
    public void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = factHandleToStoredObject(handle, reInitPropagated || propagated, object);
        storage.put(getHandleForObject(object).getId(), storedObject);
    }

    private StoredObject factHandleToStoredObject(InternalFactHandle handle, boolean propagated, Object object) {
        return handle.isEvent() ?
                createStoredObject(propagated, object, ((EventFactHandle) handle).getStartTimestamp(), ((EventFactHandle) handle).getDuration()) :
                createStoredObject(propagated, object);
    }

    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new SerializableStoredObject(object, propagated);
    }

    protected StoredObject createStoredObject(boolean propagated, Object object, long timestamp, long duration) {
        return new SerializableStoredObject(object, propagated, timestamp, duration);
    }

    @Override
    public void removeFromPersistedStorage(Object object) {
        InternalFactHandle fh = getHandleForObject(object);
        if (fh != null) {
            storage.remove(fh.getId());
        }
    }

    @Override
    public void safepoint() {
        if (storage.requiresFlush()) {
            storage.flush();
        }
    }
}
