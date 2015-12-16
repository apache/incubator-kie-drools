/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.persistence.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public class MapBasedPersistenceContext
    implements
    PersistenceContext,
    NonTransactionalPersistentSession {
    
    private Map<Long, SessionInfo> ksessions;
    private Map<Long, WorkItemInfo> workItems;
    private boolean open;
    private KnowledgeSessionStorage storage;
    
    public MapBasedPersistenceContext(KnowledgeSessionStorage storage) {
        open = true;
        this.storage = storage;
        this.ksessions = new HashMap<Long, SessionInfo>();
        this.workItems = new HashMap<Long, WorkItemInfo>();
    }
    
    public SessionInfo persist(SessionInfo entity) {
        if( entity.getId() == null ) {
            entity.setId(storage.getNextStatefulKnowledgeSessionId());
        }
        ksessions.put( entity.getId(), entity );
        return entity;
    }

    public SessionInfo findSessionInfo(Long sessionId) {
        SessionInfo sessionInfo = ksessions.get( sessionId );
        if(sessionInfo == null){
            sessionInfo = storage.findSessionInfo( sessionId );
            ksessions.put( sessionId, sessionInfo );
        }
        return sessionInfo;
    }

    @Override
    public void remove(SessionInfo sessionInfo) {
        this.ksessions.remove(sessionInfo.getId());
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

    public List<SessionInfo> getStoredKnowledgeSessions() {
        return Collections.unmodifiableList( new ArrayList<SessionInfo>(ksessions.values()) );
    }
    
    public WorkItemInfo persist(WorkItemInfo workItemInfo) {
        if( workItemInfo.getId() == null){
            workItemInfo.setId( storage.getNextWorkItemId() );
        }
        workItems.put( workItemInfo.getId(), workItemInfo );
        return workItemInfo;
    }

    public List<WorkItemInfo> getStoredWorkItems() {
        return Collections.unmodifiableList( new ArrayList<WorkItemInfo>(workItems.values()) );
    }

    public WorkItemInfo findWorkItemInfo(Long id) {
        WorkItemInfo workItemInfo = workItems.get( id );
        if(workItemInfo == null)
            workItemInfo = storage.findWorkItemInfo( id );
        return workItemInfo;
    }

    public void remove(WorkItemInfo workItemInfo) {
        if( !(workItems.remove( workItemInfo.getId() ) == null) ){
            storage.remove( workItemInfo );
        }
    }

    public WorkItemInfo merge(WorkItemInfo workItemInfo) {
        storage.saveOrUpdate(workItemInfo);
        return workItemInfo;
    }

    public void lock(SessionInfo sessionInfo) {
        throw new UnsupportedOperationException("Map based persistence does not support locking.");
        
    }

    public void lock(WorkItemInfo workItemInfo) {
        throw new UnsupportedOperationException("Map based persistence does not support locking.");
    }

}
