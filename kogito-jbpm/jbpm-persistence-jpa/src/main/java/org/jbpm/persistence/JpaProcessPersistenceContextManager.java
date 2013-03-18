package org.jbpm.persistence;

import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.kie.api.runtime.Environment;

public class JpaProcessPersistenceContextManager extends JpaPersistenceContextManager
    implements
    ProcessPersistenceContextManager {

    public JpaProcessPersistenceContextManager(Environment env) {
        super( env );
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        return new JpaProcessPersistenceContext( cmdScopedEntityManager );
    }

}
