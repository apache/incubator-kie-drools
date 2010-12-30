package org.drools.persistence.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;

public class MapBasedPersistenceContext
    implements
    PersistenceContext,
    NonTransactionalPersistentSession {
    
    private Map<Long, SessionInfo> ksessions;
    private boolean open;
    private AbstractStorage storage;
    
    public MapBasedPersistenceContext(AbstractStorage storage) {
        open = true;
        this.storage = storage;
        this.ksessions = new HashMap<Long, SessionInfo>();
    }
    
    public void persist(SessionInfo entity) {
        ksessions.put( entity.getId(), entity );
    }

    public SessionInfo findSessionInfo(Long sessionId) {
        SessionInfo sessionInfo = ksessions.get( sessionId );
        if(sessionInfo == null)
            sessionInfo = storage.findSessionInfo( sessionId );
        return sessionInfo;
    }

    public boolean isOpen() {
        return open;
    }

    public void joinTransaction() {
    }

    public void close() {
        open = false;
        ksessions.clear();
    }

    public void clear() {
    }

    public List<SessionInfo> getStoredObjects() {
        return new ArrayList<SessionInfo>(ksessions.values());
    }

}
