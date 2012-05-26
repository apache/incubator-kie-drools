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

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.drools.persistence.TransactionManager;

class TaskSpringTransactionManager implements TaskTransactionManager {

    public final static int STATUS_ROLLBACK_ONLY = 5;

    private TransactionManager tm;
    private boolean useJTA;

    TaskSpringTransactionManager(TransactionManager springTransactionManager, boolean useJTA) {
        this.tm = springTransactionManager;
        this.useJTA = useJTA;
    }

    public void attachPersistenceContext(EntityManager em) { 
        if( useJTA ) { 
            em.joinTransaction();
        }
    }
    
    public boolean begin(final EntityManager em) {
        boolean owner =  tm.begin();
        return owner;
    }

    public void commit(EntityManager em, boolean txOwner) {
        tm.commit(txOwner);
    }

    public void rollback(EntityManager em, boolean txOwner) {
        int status = tm.getStatus();
        switch(status) { 
        case TransactionManager.STATUS_NO_TRANSACTION:
        case TransactionManager.STATUS_COMMITTED:
        case TransactionManager.STATUS_ROLLEDBACK:
            // do nothing
            break;
        case TransactionManager.STATUS_ACTIVE:
        case STATUS_ROLLBACK_ONLY:
            tm.rollback(txOwner);
            break;
        case TransactionManager.STATUS_UNKNOWN:
        default:
            throw new RuntimeException("Unknown transaction state when rolling back.");
        }
    }

    public int getStatus(EntityManager em) {
        return tm.getStatus();
    }

    public void dispose() {
        int status = tm.getStatus();
        if( status == TransactionManager.STATUS_ACTIVE ) { 
            try { 
                tm.commit(true);
            } catch( RuntimeException t ) { 
                // do nothing
            }
        } else if( status == STATUS_ROLLBACK_ONLY ) { 
            try { 
                tm.rollback(true);
            } catch( RuntimeException t ) { 
                // do nothing
            }
        }
        tm = null;
    }
    

    
}
