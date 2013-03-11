package org.drools.persistence;

public interface PersistenceContextManager {
    PersistenceContext getApplicationScopedPersistenceContext();
    
    PersistenceContext getCommandScopedPersistenceContext();
    
    void beginCommandScopedEntityManager();
    
    void endCommandScopedEntityManager();
    
    void clearPersistenceContext();

    void dispose();
}
