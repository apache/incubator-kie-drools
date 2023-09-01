package org.drools.persistence.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionSynchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualTransactionManager
    implements
    TransactionManager {
    
    private Logger logger = LoggerFactory.getLogger( getClass() );
    private NonTransactionalPersistentSession session;
    private KnowledgeSessionStorage storage;
    private TransactionSynchronization transactionSynchronization;

    private static final ThreadLocal <Map<Object, Object>> transactionResources = new ThreadLocal<Map<Object, Object>>(){
        @Override
        protected Map<Object, Object> initialValue() {
            return Collections.synchronizedMap(new HashMap<Object, Object>());
        }
    };
    
    public ManualTransactionManager(NonTransactionalPersistentSession session,
                                    KnowledgeSessionStorage storage) {
        this.session = session;
        this.storage = storage;
    }
    
    public int getStatus() {
        return 0;
    }

    public boolean begin() {
        // There are no transactions since everything 
        //  is in memory and instantaneous
        return true;
    }

    public void commit(boolean transactionOwner) {
        // Do not check if the caller is the transactionOwner 
        //  because there's no need to "wait" for a commit
        try{
            for(PersistentSession session : session.getStoredKnowledgeSessions()){
                session.transform();
                storage.saveOrUpdate(session);
            }
            
            for(PersistentWorkItem workItem : session.getStoredWorkItems()){
                workItem.transform();
                storage.saveOrUpdate( workItem );
            }
            try{
                transactionSynchronization.afterCompletion(TransactionManager.STATUS_COMMITTED);
            } catch (RuntimeException re){
                logger.warn("Unable to synchronize transaction after commit, see cause.", re);
            }
        } catch (RuntimeException re) {
            transactionSynchronization.afterCompletion(TransactionManager.STATUS_ROLLEDBACK);
        }
        // We shouldn't clear session here because by doing so 
        //  we lose track of this objects on successive interactions
    }

    public void rollback(boolean transactionOwner) {
    }

    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        this.transactionSynchronization = ts;
    }

    @Override
    public void putResource(Object key, Object resource) {
        transactionResources.get().put(key, resource);
    }

    @Override
    public Object getResource(Object key) {
        return transactionResources.get().get(key);
    }
}
