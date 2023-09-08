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
package org.drools.reliability.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Storage;
import org.drools.reliability.core.util.ReliabilityUtils;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class SimpleSerializationReliableObjectStore extends IdentityObjectStore implements SimpleReliableObjectStore {

    protected transient Storage<Long, StoredObject> storage;

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
        Map<Long, StoredObject> propagated = new HashMap<>();
        List<StoredObject> notPropagated = new ArrayList<>();
        for (Long factHandleId : storage.keySet()) {
            StoredObject storedObject = storage.get(factHandleId);
            if (storedObject.isPropagated()) {
                propagated.put(factHandleId, storedObject);
            } else {
                notPropagated.add(storedObject);
            }
        }

        storage.clear();

        if (session.getSessionConfiguration().getClockType() == ClockType.PSEUDO_CLOCK) {
            repropagateWithPseudoClock(session, ep, propagated);
        } else {
            repropagate(session, ep, propagated);
        }

        reInitPropagated = false;

        // fact handles without any match have never been propagated in the original session, so they should fire
        return notPropagated;
    }

    private void repropagate(InternalWorkingMemory session, InternalWorkingMemoryEntryPoint ep, Map<Long, StoredObject> propagated) {
        Map<Long, Long> factHandleIdMap = new HashMap<>();
        propagated.forEach((oldFactHandleId, storedObject) -> {
            long newFactHandleId = storedObject.repropagate(ep);
            factHandleIdMap.put(newFactHandleId, oldFactHandleId);
        });

        fireOnlyWhenActivationRemaining(session, factHandleIdMap);
    }

    private void fireOnlyWhenActivationRemaining(InternalWorkingMemory session, Map<Long, Long> factHandleIdMap) {
        if (session.getSessionConfiguration().getPersistedSessionOption().getActivationStrategy() == PersistedSessionOption.ActivationStrategy.ACTIVATION_KEY) {
            // fact handles with a match have been already propagated in the original session, so they shouldn't fire unless remained in activationsStorage
            Storage<String, Object> activationsStorage = ((ReliableKieSession)session).getActivationsStorage();
            Set<String> activationKeySet = activationsStorage.keySet();
            session.fireAllRules(match -> {
                String activationKey = ReliabilityUtils.getActivationKeyReplacingNewIdWithOldId(match, factHandleIdMap);
                if (activationKeySet.contains(activationKey)) {
                    // If there is a remaining activation, it can fire
                    activationsStorage.remove(activationKey);
                    return true;
                } else {
                    return false;
                }
            });
        } else {
            session.fireAllRules(match -> false);
        }
    }

    private void repropagateWithPseudoClock(InternalWorkingMemory session, InternalWorkingMemoryEntryPoint ep, Map<Long, StoredObject> propagated) {
        ReliablePseudoClockScheduler clock = (ReliablePseudoClockScheduler) session.getSessionClock();
        Map<Long, Long> factHandleIdMap = new HashMap<>();
        for (Map.Entry<Long, StoredObject> entry : propagated.entrySet()) {
            StoredObject storedObject = entry.getValue();
            if (storedObject.isEvent()) {
                StoredEvent storedEvent = (StoredEvent) storedObject;
                long currentTime = clock.getCurrentTime();
                long timestamp = storedEvent.getTimestamp();
                if (currentTime < timestamp) {
                    clock.advanceTime(timestamp - currentTime, TimeUnit.MILLISECONDS);
                }
            }
            long newFactHandleId = storedObject.repropagate(ep); // This may schedule an expiration
            factHandleIdMap.put(newFactHandleId, entry.getKey());
        }

        fireOnlyWhenActivationRemaining(session, factHandleIdMap);

        // Finally, meet with the persistedTime
        long currentTime = clock.getCurrentTime();
        long persistedTime = clock.getPersistedTimer().longValue();
        if (currentTime < persistedTime) {
            clock.advanceTime(persistedTime - currentTime, TimeUnit.MILLISECONDS); // This may trigger an expiration
        }
    }

    @Override
    public void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated) {
        Object object = handle.getObject();
        StoredObject storedObject = factHandleToStoredObject(handle, reInitPropagated || propagated, object);
        storage.put(getHandleForObject(object).getId(), storedObject);
    }

    protected StoredObject factHandleToStoredObject(InternalFactHandle handle, boolean propagated, Object object) {
        return handle.isEvent() ?
                createStoredEvent(propagated, object, ((DefaultEventHandle) handle).getStartTimestamp(), ((DefaultEventHandle) handle).getDuration()) :
                createStoredObject(propagated, object);
    }

    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new SerializableStoredObject(object, propagated);
    }

    protected StoredEvent createStoredEvent(boolean propagated, Object object, long timestamp, long duration) {
        return new SerializableStoredEvent(object, propagated, timestamp, duration);
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
