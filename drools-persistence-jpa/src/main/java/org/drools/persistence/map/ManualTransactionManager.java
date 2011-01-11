package org.drools.persistence.map;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public class ManualTransactionManager
    implements
    TransactionManager {
    
    private NonTransactionalPersistentSession session;
    private KnowledgeSessionStorage storage;
    private TransactionSynchronization transactionSynchronization;
    
    public ManualTransactionManager(NonTransactionalPersistentSession session,
                                    KnowledgeSessionStorage storage) {
        this.session = session;
        this.storage = storage;
    }
    
    public int getStatus() {
        return 0;
    }

    public void begin() {
        //session.clear();
    }

    public void commit() {
        try{
            for(SessionInfo sessionInfo : session.getStoredKnowledgeSessions()){
                storage.saveOrUpdate(sessionInfo);
            }
            
            for(WorkItemInfo workItemInfo : session.getStoredWorkItems()){
                storage.saveOrUpdate( workItemInfo );
            }
            //session.clear();
            try{
                transactionSynchronization.afterCompletion(TransactionManager.STATUS_COMMITTED);
            } catch (RuntimeException re){
                //FIXME log error
            }
        } catch (RuntimeException re) {
            transactionSynchronization.afterCompletion(TransactionManager.STATUS_ROLLEDBACK);
        }
    }

    public void rollback() {
    }

    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        this.transactionSynchronization = ts;
    }
}
