package org.drools.persistence;

import org.drools.persistence.info.SessionInfo;

public interface PersistenceContext {

    void persist(SessionInfo entity);

    public SessionInfo findSessionInfo(Long id);

    boolean isOpen();

    void joinTransaction();

    void close();

}
