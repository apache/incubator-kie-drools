package org.drools.persistence.map;

import org.drools.persistence.info.SessionInfo;

public interface KnowledgeSessionStorage {

    SessionInfo findSessionInfo(Long id);

    void saveOrUpdate(SessionInfo storedObject);

}
