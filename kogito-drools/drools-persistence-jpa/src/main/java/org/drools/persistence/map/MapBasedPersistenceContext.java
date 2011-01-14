package org.drools.persistence.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    
    public void persist(SessionInfo entity) {
        ksessions.put( entity.getId(), entity );
    }

    public SessionInfo findSessionInfo(Long sessionId) {
        SessionInfo sessionInfo = ksessions.get( sessionId );
        if(sessionInfo == null){
            sessionInfo = storage.findSessionInfo( sessionId );
            ksessions.put( sessionId, sessionInfo );
        }
        return sessionInfo;
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
    
    public void persist(WorkItemInfo workItemInfo) {
        if( workItemInfo.getId() != null){
            workItemInfo.setId( storage.getNextWorkItemId() );
        }
        workItems.put( workItemInfo.getId(), workItemInfo );
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
        return workItemInfo;
    }

}
