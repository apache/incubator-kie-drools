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
        
        boolean useJTA = true; 
        return new JpaProcessPersistenceContext( getInternalCommandScopedEntityManager(), useJTA, locking );
    }

}
