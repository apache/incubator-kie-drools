package org.drools.persistence.map;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

import java.util.List;

public interface NonTransactionalPersistentSession {

    void clear();

    List<SessionInfo> getStoredKnowledgeSessions();

    List<WorkItemInfo> getStoredWorkItems();
}
