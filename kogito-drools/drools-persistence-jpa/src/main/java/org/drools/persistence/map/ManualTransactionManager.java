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
    }

    public void commit() {
        try{
            for(SessionInfo sessionInfo : session.getStoredKnowledgeSessions()){
                sessionInfo.update();
                storage.saveOrUpdate(sessionInfo);
            }
            
            for(WorkItemInfo workItemInfo : session.getStoredWorkItems()){
                workItemInfo.update();
                storage.saveOrUpdate( workItemInfo );
            }
            try{
                transactionSynchronization.afterCompletion(TransactionManager.STATUS_COMMITTED);
            } catch (RuntimeException re){
                //FIXME log error
            }
        } catch (RuntimeException re) {
            transactionSynchronization.afterCompletion(TransactionManager.STATUS_ROLLEDBACK);
        }
        //we shouldn't clear session here b/c doing so we lose the track of this objects on successive
        //interactions
    }

    public void rollback() {
    }

    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        this.transactionSynchronization = ts;
    }
}
