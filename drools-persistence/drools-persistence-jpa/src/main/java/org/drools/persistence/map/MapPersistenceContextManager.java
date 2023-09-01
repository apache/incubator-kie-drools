package org.drools.persistence.map;

import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistenceContextManager;

public class MapPersistenceContextManager
    implements
    PersistenceContextManager {

    private PersistenceContext persistenceContext;
    
    public MapPersistenceContextManager(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }
    
    public PersistenceContext getApplicationScopedPersistenceContext() {
        return persistenceContext;
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return persistenceContext;
    }

    public void beginCommandScopedEntityManager() {
    }

    public void endCommandScopedEntityManager() {
    }

    public void dispose() {
        persistenceContext.close();
    }

    public void clearPersistenceContext() {

    }

}
