package org.drools.persistence.map;

import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;

public class AbstractStorageEnvironmentBuilder {

    private MapBasedPersistenceContext persistenceContext;
    private AbstractStorage storage;

    public AbstractStorageEnvironmentBuilder(AbstractStorage storage) {
        this.storage = storage;
        this.persistenceContext = new MapBasedPersistenceContext( storage );
    }
    
    public PersistenceContextManager getPersistenceContextManager(){
        return new MapPersistenceContextManager( persistenceContext );
    }
    
    public TransactionManager getTransactionManager(){
        return new ManualTransactionManager( persistenceContext, storage );
    }
}
