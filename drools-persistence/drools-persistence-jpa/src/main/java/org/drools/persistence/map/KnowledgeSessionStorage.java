package org.drools.persistence.map;

import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;

public interface KnowledgeSessionStorage {

    PersistentSession findSessionInfo(Long sessionId);

    void saveOrUpdate(PersistentSession storedObject);

    void lock(PersistentSession session);

    void saveOrUpdate(PersistentWorkItem workItem);
    
    Long getNextWorkItemId();

    PersistentWorkItem findWorkItemInfo(Long id);

    void remove(PersistentWorkItem workItem);

    void lock(PersistentWorkItem workItem);
    
    Long getNextStatefulKnowledgeSessionId();

}
