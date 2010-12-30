package org.drools.persistence.map;

import java.util.List;

import org.drools.persistence.info.SessionInfo;

public interface NonTransactionalPersistentSession {

    void clear();

    List<SessionInfo> getStoredObjects();
}
