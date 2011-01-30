package org.jbpm.persistence;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.map.EnvironmentBuilder;

public class ProcessStorageEnvironmentBuilder
    implements
    EnvironmentBuilder {

    private ProcessStorage storage;
    private MapBasedProcessPersistenceContext context;
    
    public ProcessStorageEnvironmentBuilder(ProcessStorage storage) {
        this.storage = storage;
        this.context = new MapBasedProcessPersistenceContext( storage );
    }

    public ProcessPersistenceContextManager getPersistenceContextManager() {
        return new MapProcessPersistenceContextManager( context ) ;
    }

    public TransactionManager getTransactionManager() {
        return new ManualProcessTransactionManager( context, storage );
    }

}
