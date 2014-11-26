package org.drools.persistence.map;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public interface KnowledgeSessionStorage {

    SessionInfo findSessionInfo(Long sessionId);

    void saveOrUpdate(SessionInfo storedObject);

    void lock(SessionInfo sessionInfo);

    void saveOrUpdate(WorkItemInfo workItemInfo);
    
    Long getNextWorkItemId();

    WorkItemInfo findWorkItemInfo(Long id);

    void remove(WorkItemInfo workItemInfo);

    void lock(WorkItemInfo workItemInfo);
    
    Long getNextStatefulKnowledgeSessionId();

}
