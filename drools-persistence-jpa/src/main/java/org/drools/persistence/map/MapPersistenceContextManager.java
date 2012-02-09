package org.drools.persistence.map;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;

public class MapPersistenceContextManager
    implements
    PersistenceContextManager {

    private PersistenceContext persistenceContext;
    private TransactionManager txm;
    
    public MapPersistenceContextManager(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }
    
    public void setTransactionManager(TransactionManager txm) {
        this.txm = txm;
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

}
