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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

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
        UserTransaction ut = findUserTransaction();
        if( ut == null ) { 
            throw new IllegalStateException("Unable to find JTA transaction." );
        }
        try {
            if( ut.getStatus() == Status.STATUS_NO_TRANSACTION ) { 
                ut.begin();
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
            return ut;
        }
        return USER_MANAGED_TRANSACTION;
    }

    protected static UserTransaction findUserTransaction() {
        InitialContext context = null;
        try { 
            context = new InitialContext();
        } catch( Exception e ) { 
           throw new IllegalStateException("Unable to initialized " + InitialContext.class.getName() + " instance.", e);
        }
        try {
            return (UserTransaction) context.lookup( "java:comp/UserTransaction" );
        } catch ( NamingException ex ) {
            for (String utLookup : KNOWN_UT_JNDI_KEYS) {
                if (utLookup != null) {
                    try {
                        UserTransaction ut = (UserTransaction) context.lookup(utLookup);
                        return ut;
                    } catch (NamingException e) {
                        logger.debug("User Transaction not found in JNDI under {}", utLookup);
                        
                    }
                }
            }
            logger.warn("No user transaction found under known names");
            return null;
        }
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
        UserTransaction ut = null;
        if( ! (transaction instanceof UserTransaction) ) { 
           throw new IllegalStateException("This persistence strategy only deals with UserTransaction instances!" );
        } else if( transaction != null ){ 
           ut = (UserTransaction) transaction;
        }
        
        try { 
            if( ut != null ) { 
                // There's a tx running, close it.
                ut.commit();
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
