package org.drools.persistence.map;

import java.util.List;

import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;

public interface NonTransactionalPersistentSession {

    void clear();

    List<PersistentSession> getStoredKnowledgeSessions();
    
    List<PersistentWorkItem> getStoredWorkItems();
}
