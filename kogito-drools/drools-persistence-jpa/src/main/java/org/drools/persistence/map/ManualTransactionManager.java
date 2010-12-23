package org.drools.persistence.map;

import org.drools.persistence.EntityInfo;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;

public class ManualTransactionManager
    implements
    TransactionManager {
    
    private NonTransactionalPersistentSession session;
    private AbstractStorage storage;
    
    public ManualTransactionManager(NonTransactionalPersistentSession session,
                                    AbstractStorage storage) {
        this.session = session;
        this.storage = storage;
    }
    
    public int getStatus() {
        return 0;
    }

    public void begin() {
        session.clear();
    }

    public void commit() {
        for(EntityInfo storedObject : session.getStoredObjects()){
            storage.saveOrUpdate(storedObject);
        }
    }

    public void rollback() {
    }

    public void registerTransactionSynchronization(TransactionSynchronization ts) {

    }
}
