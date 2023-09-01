package org.drools.persistence.map;

import org.drools.persistence.api.PersistenceContextManager;
import org.drools.persistence.api.TransactionManager;

public class KnowledgeSessionStorageEnvironmentBuilder implements EnvironmentBuilder {

    private MapBasedPersistenceContext persistenceContext;
    private KnowledgeSessionStorage storage;

    public KnowledgeSessionStorageEnvironmentBuilder(KnowledgeSessionStorage storage) {
        this.storage = storage;
        this.persistenceContext = new MapBasedPersistenceContext( storage );
    }
    
    /* (non-Javadoc)
     * @see org.kie.api.persistence.map.EnvironmentBuilder#getPersistenceContextManager()
     */
    public PersistenceContextManager getPersistenceContextManager(){
        return new MapPersistenceContextManager( persistenceContext );
    }
    
    /* (non-Javadoc)
     * @see org.kie.api.persistence.map.EnvironmentBuilder#getTransactionManager()
     */
    public TransactionManager getTransactionManager(){
        return new ManualTransactionManager( persistenceContext, storage );
    }
}
