package org.jbpm.persistence;

import org.drools.persistence.map.MapPersistenceContextManager;

public class MapProcessPersistenceContextManager extends MapPersistenceContextManager
    implements
    ProcessPersistenceContextManager {

    private ProcessPersistenceContext persistenceContext;

    public MapProcessPersistenceContextManager(ProcessPersistenceContext persistenceContext) {
        super( persistenceContext );
        this.persistenceContext = persistenceContext;
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        return persistenceContext;
    }

}
