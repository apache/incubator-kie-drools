/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.persistence.map;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualTransactionManager
    implements
    TransactionManager {
    
    private Logger logger = LoggerFactory.getLogger( getClass() );
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

    public boolean begin() {
        // There are no transactions since everything 
        //  is in memory and instantaneous
        return true;
    }

    public void commit(boolean transactionOwner) {
        // Do not check if the caller is the transactionOwner 
        //  because there's no need to "wait" for a commit
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

    public void attachPersistenceContext(Object persistenceContextObject) {
        
    }
    
}
