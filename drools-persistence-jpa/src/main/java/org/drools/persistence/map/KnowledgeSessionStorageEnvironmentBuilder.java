package org.drools.persistence.map;

import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;

public class KnowledgeSessionStorageEnvironmentBuilder implements EnvironmentBuilder {

    private MapBasedPersistenceContext persistenceContext;
    private KnowledgeSessionStorage storage;

    public KnowledgeSessionStorageEnvironmentBuilder(KnowledgeSessionStorage storage) {
        this.storage = storage;
        this.persistenceContext = new MapBasedPersistenceContext( storage );
    }
    
    /* (non-Javadoc)
     * @see org.drools.persistence.map.EnvironmentBuilder#getPersistenceContextManager()
     */
    public PersistenceContextManager getPersistenceContextManager(){
        return new MapPersistenceContextManager( persistenceContext );
    }
    
    /* (non-Javadoc)
     * @see org.drools.persistence.map.EnvironmentBuilder#getTransactionManager()
     */
    public TransactionManager getTransactionManager(){
        return new ManualTransactionManager( persistenceContext, storage );
    }
}
