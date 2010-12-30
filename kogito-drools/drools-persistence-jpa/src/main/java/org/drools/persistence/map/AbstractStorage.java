package org.drools.persistence.map;

import org.drools.persistence.info.SessionInfo;

public interface AbstractStorage {

    SessionInfo findSessionInfo(Long id);

    void saveOrUpdate(SessionInfo storedObject);

}
