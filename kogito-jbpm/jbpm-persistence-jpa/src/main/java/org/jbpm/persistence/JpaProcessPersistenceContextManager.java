package org.jbpm.persistence;

import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

public class JpaProcessPersistenceContextManager extends JpaPersistenceContextManager
    implements
    ProcessPersistenceContextManager {

    public JpaProcessPersistenceContextManager(Environment env) {
        super( env );
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        Boolean locking = (Boolean) env.get(EnvironmentName.USE_PESSIMISTIC_LOCKING);
        if( locking == null ) { 
            locking = false;
        }
        /**
        Boolean local = (Boolean) env.get(EnvironmentName.USE_LOCAL_TRANSACTIONS);
        if( local == null ) { 
            local = false;
        }
        boolean useJTA = ! local; // for clarity's sake
        */
        
        boolean useJTA = true; 
        return new JpaProcessPersistenceContext( cmdScopedEntityManager, useJTA, locking );
    }

}
