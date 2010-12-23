package org.drools.persistence.map;

import java.util.List;

import org.drools.persistence.EntityInfo;

public interface NonTransactionalPersistentSession {

    void clear();

    List<EntityInfo> getStoredObjects();
}
