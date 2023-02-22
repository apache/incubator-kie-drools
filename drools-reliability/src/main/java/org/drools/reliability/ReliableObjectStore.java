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

package org.drools.reliability;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.common.FactHandleClassStore;
import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ObjectStore;
import org.infinispan.Cache;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.drools.reliability.ObjectStoreEvent.EventType.DELETE;
import static org.drools.reliability.ObjectStoreEvent.EventType.INSERT;
import static org.drools.reliability.ObjectStoreEvent.EventType.UPDATE;

public class ReliableObjectStore implements ObjectStore {

    private IdentityObjectStore delegate;

    private Cache<Long, ObjectStoreEvent> cache;

    private AtomicLong latestEventSequenceNum = new AtomicLong(-1); // We may put this into cache as metadata

    private boolean inReplay = false;

    public ReliableObjectStore(Cache<Long, ObjectStoreEvent> cache) {
        this.delegate = new IdentityObjectStore();
        this.cache = cache;
    }

    // This is a temporal implementation to "replay" events on a new ksession in case of fail-over.
    // Once we can replicate other ksession status (e.g. node memory), we would just need to populate the delegate ObjectStore
    public void replayObjectStoreEventFromCache(StatefulKnowledgeSession session) {
        int cacheSize = cache.size();
        if (cacheSize == 0) {
            return;
        }
        inReplay = true;
        try {
            latestEventSequenceNum.set(cacheSize - 1); // Currently, assume the cache keys are filled with 0, 1, 2, ...
            for (long i = 0; i <= latestEventSequenceNum.longValue(); i++) {
                ObjectStoreEvent objectStoreEvent = cache.get(i);
                if (objectStoreEvent == null) {
                    // At the moment, null is not expected. We would ignore null if we remove a cache entry
                    new ReliabilityRuntimeException("key [" + i + "] is not found in cache [" + cache.getName() + "]");
                }
                InternalFactHandle factHandle = objectStoreEvent.getFactHandle();
                System.out.println("  objectStoreEvent : " + objectStoreEvent);
                switch (objectStoreEvent.getEventType()) {
                    case INSERT:
                        session.insert(factHandle.getObject());
                        break;
                    case UPDATE:
                        session.update(factHandle, factHandle.getObject());
                        break;
                    case DELETE:
                        session.delete(factHandle);
                        break;
                    default:
                        break;
                }
            }
        } finally {
            inReplay = false;
        }
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Object getObjectForHandle(InternalFactHandle handle) {
        return delegate.getObjectForHandle(handle);
    }

    @Override
    public InternalFactHandle reconnect(InternalFactHandle factHandle) {
        return delegate.reconnect(factHandle);
    }

    @Override
    public InternalFactHandle getHandleForObject(Object object) {
        return delegate.getHandleForObject(object);
    }

    @Override
    public void updateHandle(InternalFactHandle handle, Object object) {
        delegate.updateHandle(handle, object);
        if (!inReplay) {
            cache.put(latestEventSequenceNum.incrementAndGet(), new ObjectStoreEvent(UPDATE, handle)); // handle's object is already updated
        }
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        delegate.addHandle(handle, object);
        if (!inReplay) {
            cache.put(latestEventSequenceNum.incrementAndGet(), new ObjectStoreEvent(INSERT, handle));
        }
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        delegate.removeHandle(handle);
        if (!inReplay) {
            cache.put(latestEventSequenceNum.incrementAndGet(), new ObjectStoreEvent(DELETE, handle));
        }
    }

    @Override
    public Iterator<Object> iterateObjects() {
        return delegate.iterateObjects();
    }

    @Override
    public Iterator<Object> iterateObjects(ObjectFilter filter) {
        return delegate.iterateObjects(filter);
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return delegate.iterateFactHandles();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles(ObjectFilter filter) {
        return delegate.iterateFactHandles(filter);
    }

    @Override
    public Iterator<Object> iterateNegObjects(ObjectFilter filter) {
        return delegate.iterateNegObjects(filter);
    }

    @Override
    public Iterator<InternalFactHandle> iterateNegFactHandles(ObjectFilter filter) {
        return delegate.iterateNegFactHandles(filter);
    }

    @Override
    public FactHandleClassStore getStoreForClass(Class<?> clazz) {
        return delegate.getStoreForClass(clazz);
    }

    @Override
    public boolean clearClassStore(Class<?> clazz) {
        return delegate.clearClassStore(clazz);
    }

}
