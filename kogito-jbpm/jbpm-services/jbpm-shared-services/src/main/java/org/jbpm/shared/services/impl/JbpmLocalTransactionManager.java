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
package org.jbpm.shared.services.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.drools.persistence.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;

public class JbpmLocalTransactionManager implements JbpmServicesTransactionManager {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public JbpmLocalTransactionManager() { 
        // empty constructor
    }
    
    public void attachPersistenceContext(EntityManager em) { 
        // no-op for entity transactions
    }
    
    public boolean begin(EntityManager em) {
        boolean begun = false;
        if( getStatus(em) == TransactionManager.STATUS_NO_TRANSACTION ) { 
            try {
                em.getTransaction().begin();
                begun = true;
            } catch (Exception e) {
                logger.warn("Unable to begin transaction", e);
                throw new RuntimeException("Unable to begin transaction", e);
            }
        }
        return begun;
    }

    public void commit(EntityManager em, boolean txOwner) { 
        try { 
            if( txOwner ) { 
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            logger.warn("Unable to commit transaction", e);
            throw new RuntimeException("Unable to commit transaction", e);
        }
    }

    public void rollback(EntityManager em, boolean txOwner) {
        if( getStatus(em) == TransactionManager.STATUS_NO_TRANSACTION ) { 
            return;
        }
        
        try {
            if( txOwner ) { 
                em.getTransaction().rollback();
            }
            else { 
                em.getTransaction().setRollbackOnly();
            }
        } catch (Exception e) {
            logger.warn("Unable to rollback transaction", e);
            throw new RuntimeException("Unable to rollback transaction", e);
        }
    }

    public int getStatus(EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        if( tx.isActive() ) { 
            return TransactionManager.STATUS_ACTIVE;
        }
        else { 
            return TransactionManager.STATUS_NO_TRANSACTION;
        }
    }

    public void dispose() {
        // no-op
    }

}
