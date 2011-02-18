package org.drools.persistence;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public interface PersistenceContext {

    void persist(SessionInfo entity);

    public SessionInfo findSessionInfo(Integer id);

    boolean isOpen();

    void joinTransaction();

    void close();

    void persist(WorkItemInfo workItemInfo);

    WorkItemInfo findWorkItemInfo(Long id);

    void remove(WorkItemInfo workItemInfo);

    WorkItemInfo merge(WorkItemInfo workItemInfo);

}
