package org.drools.persistence.map;

import java.util.List;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public interface NonTransactionalPersistentSession {

    void clear();

    List<SessionInfo> getStoredKnowledgeSessions();
    
    List<WorkItemInfo> getStoredWorkItems();
}
