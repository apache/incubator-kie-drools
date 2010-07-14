package org.drools.persistence.session;

import javax.persistence.EntityManager;

public interface JpaManager {
    EntityManager getApplicationScopedEntityManager();
    
    EntityManager getCommandScopedEntityManager();
    
    void beginCommandScopedEntityManager();
    
    void endCommandScopedEntityManager();

    void dispose();
}
