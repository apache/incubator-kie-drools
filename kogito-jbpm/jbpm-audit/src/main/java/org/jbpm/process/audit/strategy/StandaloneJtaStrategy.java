/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy is used by instances that are<ul>
 * <li>used outside the {@link KieSession}</li>
 * <li>use their own {@link EntityManager} instance per operation</li>
 * </ul>
 */
public class StandaloneJtaStrategy implements PersistenceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(JPAWorkingMemoryDbLogger.class);

    private static final String[] KNOWN_UT_JNDI_KEYS = new String[] {"UserTransaction", "java:jboss/UserTransaction", System.getProperty("jbpm.ut.jndi.lookup")};

    protected EntityManagerFactory emf;
   
    private static final Object USER_MANAGED_TRANSACTION = new Object();
    
    public StandaloneJtaStrategy(EntityManagerFactory emf) { 
        this.emf = emf;
    }
    
    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        boolean newTx = false;
        
        TransactionManager txm = TransactionManagerFactory.get().newTransactionManager();
                
        if( txm == null ) { 
            throw new IllegalStateException("Unable to find JTA transaction." );
        }
        try {
            if( txm.getStatus() == TransactionManager.STATUS_NO_TRANSACTION ) { 
                txm.begin();
                newTx = true;
                // since new transaction was started em must join it
            } 
        } catch(Exception ex) {
            throw new IllegalStateException("Unable to find or open a transaction: " + ex.getMessage(), ex);
        }

        try { 
            em.joinTransaction();
        } catch( Exception e) { 
            throw new IllegalStateException("Unable to join EntityManager to transaction: " + e.getMessage(), e);
        }

        if( newTx ) { 
            return txm;
        }
        return USER_MANAGED_TRANSACTION;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        commitTransaction(transaction);
        
        em.clear();
        em.close();
    }

    protected void commitTransaction(Object transaction) {
        if( transaction == USER_MANAGED_TRANSACTION ) { 
            return;
        }
        TransactionManager txm  = (TransactionManager) transaction;
        
        
        try { 
            if( txm != null ) { 
                // There's a tx running, close it.
                txm.commit(true);
            }
        } catch(Exception e) { 
            logger.error("Unable to commit transaction: ", e);
        }
    }

    @Override
    public void dispose() {
        // NEVER close the emf, you don't know what it is also being used for!
        emf = null;
    }
}
