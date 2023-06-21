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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Storage;
import org.drools.core.phreak.PropagationEntry;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.EntryPoint;

import static org.drools.reliability.core.ReliablePropagationList.PROPAGATION_LIST;

public class ReliableSessionInitializer {

    private static final Map<PersistedSessionOption.PersistenceStrategy, SessionInitializer> initializersMap = Map.of(
            PersistedSessionOption.PersistenceStrategy.STORES_ONLY, new StoresOnlySessionInitializer(),
            PersistedSessionOption.PersistenceStrategy.FULL, new FullReliableSessionInitializer());

    public static InternalWorkingMemory initReliableSession(SessionConfiguration sessionConfig, InternalWorkingMemory session) {
        PersistedSessionOption persistedSessionOption = sessionConfig.getPersistedSessionOption();
        return initializersMap.get(persistedSessionOption.getPersistenceStrategy()).init(session, persistedSessionOption);
    }

    interface SessionInitializer {
        InternalWorkingMemory init(InternalWorkingMemory session, PersistedSessionOption persistedSessionOption);
    }

    static class StoresOnlySessionInitializer implements SessionInitializer {

        @Override
        public InternalWorkingMemory init(InternalWorkingMemory session, PersistedSessionOption persistedSessionOption) {
            if (!persistedSessionOption.isNewSession()) {
                // re-propagate objects from the storage to the new session
                populateSessionFromStorage(session);
            }

            session.setWorkingMemoryActionListener(entry -> onWorkingMemoryAction(session, entry));
            session.getRuleRuntimeEventSupport().addEventListener(new SimpleStoreRuntimeEventListener(session));

            return session;
        }

        private void onWorkingMemoryAction(InternalWorkingMemory session, PropagationEntry entry) {
            if (entry instanceof PropagationEntry.Insert) {
                InternalFactHandle fh = ((PropagationEntry.Insert) entry).getHandle();
                WorkingMemoryEntryPoint ep = fh.getEntryPoint(session);
                ((SimpleReliableObjectStore) ep.getObjectStore()).putIntoPersistedStorage(fh, true);
            }
        }

        private void populateSessionFromStorage(InternalWorkingMemory session) {
            Map<InternalWorkingMemoryEntryPoint, List<StoredObject>> notPropagatedByEntryPoint = new HashMap<>();

            for (EntryPoint ep : session.getEntryPoints()) {
                SimpleReliableObjectStore store = (SimpleReliableObjectStore) ((WorkingMemoryEntryPoint) ep).getObjectStore();
                notPropagatedByEntryPoint.put((InternalWorkingMemoryEntryPoint) ep, store.reInit(session, (InternalWorkingMemoryEntryPoint) ep));
            }

            notPropagatedByEntryPoint.forEach((ep, objects) -> objects.forEach(obj -> obj.repropagate(ep)));
        }

        static class SimpleStoreRuntimeEventListener implements RuleRuntimeEventListener {

            private final InternalWorkingMemory session;

            SimpleStoreRuntimeEventListener(InternalWorkingMemory session) {
                this.session = session;
            }

            public void objectInserted(ObjectInsertedEvent ev) {
                // no-op. The object is already added in the storage by addHandle
            }

            public void objectDeleted(ObjectDeletedEvent ev) {
                // no-op. The object is already removed in the storage by removeHandle
            }

            public void objectUpdated(ObjectUpdatedEvent ev) {
                InternalFactHandle fh = (InternalFactHandle) ev.getFactHandle();
                SimpleReliableObjectStore store = (SimpleReliableObjectStore) fh.getEntryPoint(session).getObjectStore();
                store.putIntoPersistedStorage(fh, false);
            }
        }
    }

    static class FullReliableSessionInitializer implements SessionInitializer {

        @Override
        public InternalWorkingMemory init(InternalWorkingMemory session, PersistedSessionOption persistedSessionOption) {
            if (!persistedSessionOption.isNewSession()) {
                // re-propagate objects from the storage to the new session
                populateSessionFromStorage(session);
            }
            session.getRuleRuntimeEventSupport().addEventListener(new FullReliableSessionInitializer.FullStoreRuntimeEventListener(session));
            return session;
        }

        private void populateSessionFromStorage(InternalWorkingMemory session) {
            for (EntryPoint ep : session.getEntryPoints()) {
                FullReliableObjectStore store = (FullReliableObjectStore) ((WorkingMemoryEntryPoint) ep).getObjectStore();
                store.reInit();
            }
        }

        static class FullStoreRuntimeEventListener implements RuleRuntimeEventListener {

            private final Storage<Object, Object> componentsCache;
            private final ReliablePropagationList propagationList;

            FullStoreRuntimeEventListener(InternalWorkingMemory session) {
                this.componentsCache = StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(session, "components");
                this.propagationList = (ReliablePropagationList) ((ReliableAgenda) session.getAgenda()).getPropagationList();
            }

            public void objectInserted(ObjectInsertedEvent ev) {
                componentsCache.put(PROPAGATION_LIST, propagationList);
            }

            public void objectDeleted(ObjectDeletedEvent ev) {
                componentsCache.put(PROPAGATION_LIST, propagationList);
            }

            public void objectUpdated(ObjectUpdatedEvent ev) {
                componentsCache.put(PROPAGATION_LIST, propagationList);
            }
        }
    }
}
