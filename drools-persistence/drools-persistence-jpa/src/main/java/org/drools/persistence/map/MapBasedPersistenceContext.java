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
package org.drools.persistence.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;

public class MapBasedPersistenceContext
    implements
    PersistenceContext,
    NonTransactionalPersistentSession {
    
    private Map<Long, PersistentSession> ksessions;
    private Map<Long, PersistentWorkItem> workItems;
    private boolean open;
    private KnowledgeSessionStorage storage;
    
    public MapBasedPersistenceContext(KnowledgeSessionStorage storage) {
        open = true;
        this.storage = storage;
        this.ksessions = new HashMap<>();
        this.workItems = new HashMap<>();
    }
    
    public PersistentSession persist(PersistentSession entity) {
        if( entity.getId() == null ) {
            entity.setId(storage.getNextStatefulKnowledgeSessionId());
        }
        ksessions.put( entity.getId(), entity );
        return entity;
    }

    public PersistentSession findSession(Long sessionId) {
        PersistentSession session = ksessions.get( sessionId );
        if(session == null){
            session = storage.findSessionInfo( sessionId );
            ksessions.put( sessionId, session);
        }
        return session;
    }

    @Override
    public void remove(PersistentSession session) {
        this.ksessions.remove(session.getId());
    }

    public boolean isOpen() {
        return open;
    }

    public void joinTransaction() {
    }

    public void close() {
        open = false;
        clear();
    }

    public void clear() {
        clearAll();
    }

    private void clearAll() {
        ksessions.clear();
        workItems.clear();
    }

    public List<PersistentSession> getStoredKnowledgeSessions() {
        return Collections.unmodifiableList( new ArrayList<PersistentSession>(ksessions.values()) );
    }
    
    public PersistentWorkItem persist(PersistentWorkItem workItem) {
        if( workItem.getId() == null){
            workItem.setId( storage.getNextWorkItemId() );
        }
        workItems.put( workItem.getId(), workItem );
        return workItem;
    }

    public List<PersistentWorkItem> getStoredWorkItems() {
        return Collections.unmodifiableList( new ArrayList<PersistentWorkItem>(workItems.values()) );
    }

    public PersistentWorkItem findWorkItem(Long id) {
        PersistentWorkItem workItem = workItems.get( id );
        if(workItem == null)
            workItem = storage.findWorkItemInfo( id );
        return workItem;
    }

    public void remove(PersistentWorkItem workItem) {
        if( !(workItems.remove( workItem.getId() ) == null) ){
            storage.remove( workItem );
        }
    }

    public PersistentWorkItem merge(PersistentWorkItem workItem) {
        storage.saveOrUpdate(workItem);
        return workItem;
    }

    public void lock(PersistentSession session) {
        throw new UnsupportedOperationException("Map based persistence does not support locking.");
        
    }

    public void lock(PersistentWorkItem workItem) {
        throw new UnsupportedOperationException("Map based persistence does not support locking.");
    }

}
