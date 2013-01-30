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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.jta.JtaTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TaskJTATransactionManager implements TaskTransactionManager {

	private static final Logger logger = LoggerFactory.getLogger(TaskJTATransactionManager.class);
	
	private static final String[] KNOWN_UT_JNDI_KEYS = new String[] {"UserTransaction", "java:jboss/UserTransaction", System.getProperty("jbpm.ut.jndi.lookup")};
	
    private org.drools.persistence.TransactionManager tm;

    TaskJTATransactionManager() {
        this.tm = new JtaTransactionManager(findUserTransaction(), null, null);
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
        em.flush();
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
    
    protected UserTransaction findUserTransaction() {
    	InitialContext context = null;
    	try {
            context = new InitialContext();
            return (UserTransaction) context.lookup( JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME );
        } catch ( NamingException ex ) {
        	
        	for (String utLookup : KNOWN_UT_JNDI_KEYS) {
        		if (utLookup != null) {
		        	try {
		        		UserTransaction ut = (UserTransaction) context.lookup(utLookup);
		        		return ut;
					} catch (NamingException e) {
						logger.debug("User Transaction not found in JNDI under " + utLookup);
						
					}
        		}
        	}
        	logger.warn("No user transaction found under known names");
        	return null;
        }
    }
}
