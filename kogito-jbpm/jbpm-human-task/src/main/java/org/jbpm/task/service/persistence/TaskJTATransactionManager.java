/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.task.service.persistence;

import javax.persistence.EntityManager;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.jta.JtaTransactionManager;

class TaskJTATransactionManager implements TaskTransactionManager {

    private org.drools.persistence.TransactionManager tm;

    TaskJTATransactionManager() {
        this.tm = new JtaTransactionManager(null, null, null);
    }

    public void attachPersistenceContext(EntityManager em) { 
        em.joinTransaction();
    }
    
    public boolean begin(EntityManager em) {
        int status = getStatus(em);
        boolean begun = false;
        if( status == TransactionManager.STATUS_NO_TRANSACTION 
            || status == TransactionManager.STATUS_COMMITTED
            || status == TransactionManager.STATUS_ROLLEDBACK ) { 
            begun =  tm.begin();
        }
        return begun;
    }

    public void commit(EntityManager em, boolean txOwner) {
        tm.commit(txOwner);
    }

    public void rollback(EntityManager em, boolean txOwner) {
        switch(tm.getStatus()) { 
        case TransactionManager.STATUS_COMMITTED:
        case TransactionManager.STATUS_NO_TRANSACTION:
        case TransactionManager.STATUS_ROLLEDBACK:
            // do nothing
            break;
        default:
            tm.rollback(txOwner);
        }
    }

    public int getStatus(EntityManager em) {
        return tm.getStatus();
    }

    public void dispose() {
        tm = null;
    }
    
}
