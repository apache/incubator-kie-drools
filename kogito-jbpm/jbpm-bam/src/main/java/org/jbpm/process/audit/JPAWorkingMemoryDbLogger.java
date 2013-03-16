/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.process.audit;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.drools.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Enables history log via JPA.
 * 
 */
public class JPAWorkingMemoryDbLogger extends AbstractAuditLogger {

    private static Logger logger = LoggerFactory.getLogger(JPAWorkingMemoryDbLogger.class);
    
    private static final String[] KNOWN_UT_JNDI_KEYS = new String[] {"UserTransaction", "java:jboss/UserTransaction", System.getProperty("jbpm.ut.jndi.lookup")};
    
    private boolean isJTA = true;
    private boolean sharedEM = false;
    
    private EntityManagerFactory emf;

    /*
     * for backward compatibility
     */
    public JPAWorkingMemoryDbLogger(WorkingMemory workingMemory) {
        super(workingMemory);
        InternalProcessRuntime processRuntime = ((InternalWorkingMemory) workingMemory).getProcessRuntime();
        if (processRuntime != null) {
            processRuntime.addEventListener( (ProcessEventListener) this );
        }
    }
    
    public JPAWorkingMemoryDbLogger(KieSession session) {
    	super(session);
        Boolean bool = (Boolean) env.get("IS_JTA_TRANSACTION");
        if (bool != null) {
        	isJTA = bool.booleanValue();
        }
        session.addEventListener(this);
    }
    /*
     * end of backward compatibility
     */

    public JPAWorkingMemoryDbLogger(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        NodeInstanceLog log = (NodeInstanceLog) builder.buildEvent(event);
        persist(log);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        NodeInstanceLog log = (NodeInstanceLog) builder.buildEvent(event, null);
        persist(log);   
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        VariableInstanceLog log = (VariableInstanceLog) builder.buildEvent(event);
        persist(log);   
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        ProcessInstanceLog log = (ProcessInstanceLog) builder.buildEvent(event);
        persist(log);
        
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        long processInstanceId = event.getProcessInstance().getId();
        EntityManager em = getEntityManager();
        UserTransaction ut = joinTransaction(em);
        List<ProcessInstanceLog> result = em.createQuery(
        "from ProcessInstanceLog as log where log.processInstanceId = ? and log.end is null")
            .setParameter(1, processInstanceId).getResultList();
        
        if (result != null && result.size() != 0) {
           ProcessInstanceLog log = result.get(result.size() - 1);
           
           log = (ProcessInstanceLog) builder.buildEvent(event, log);
           em.merge(log);   
        }
        if (!sharedEM) {
            flush(em, ut);
        }
    }
    
    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

        
    }
    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        
    }
    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }

    public void dispose() {
    }

    /**
     * This method creates a entity manager. 
     */
    private EntityManager getEntityManager() {
        if (env != null) {
            EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        	if (em != null) {
        		sharedEM = true;
        		return em;
        	}
            EntityManagerFactory emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
            if (emf != null) {
            	return emf.createEntityManager();
            }
        } else {
            return emf.createEntityManager();
        }
        throw new RuntimeException("Could not find EntityManager, both command-scoped EM and EMF in environment are null");
    }

    /**
     * This method persists the entity given to it. 
     * </p>
     * This method also makes sure that the entity manager used for persisting the entity, joins the existing JTA transaction. 
     * @param entity An entity to be persisted.
     */
    private void persist(Object entity) { 
        EntityManager em = getEntityManager();
        UserTransaction ut = joinTransaction(em);
        em.persist(entity);
        if (!sharedEM) {
        	flush(em, ut);
        }
    }
    
    /**
     * This method opens a new transaction, if none is currently running, and joins the entity manager/persistence context
     * to that transaction. 
     * @param em The entity manager we're using. 
     * @return {@link UserTransaction} If we've started a new transaction, then we return it so that it can be closed. 
     * @throws NotSupportedException 
     * @throws SystemException 
     * @throws Exception if something goes wrong. 
     */
    private UserTransaction joinTransaction(EntityManager em) {
        boolean newTx = false;
        UserTransaction ut = null;

        if (isJTA) {
	        try {
	        	em.joinTransaction();
	        
	        } catch (TransactionRequiredException e) {
				ut = findUserTransaction();
				try {
					if( ut != null && ut.getStatus() == Status.STATUS_NO_TRANSACTION ) { 
		                ut.begin();
		                newTx = true;
		                // since new transaction was started em must join it
		                em.joinTransaction();
		            } 
				} catch(Exception ex) {
					throw new IllegalStateException("Unable to find or open a transaction: " + ex.getMessage(), ex);
				}
				
				if (!newTx) {
	            	// rethrow TransactionRequiredException if UserTransaction was not found or started
	            	throw e;
	            }

			}
	       
	        if( newTx ) { 
	            return ut;
	        }
        }
        return null;
    }

    /**
     * This method closes the entity manager and transaction. It also makes sure that any objects associated 
     * with the entity manager/persistence context are detached. 
     * </p>
     * Obviously, if the transaction returned by the {@link #joinTransaction(EntityManager)} method is null, 
     * nothing is done with the transaction parameter.
     * @param em The entity manager.
     * @param ut The (user) transaction.
     */
    private static void flush(EntityManager em, UserTransaction ut) {
        em.flush(); // This saves any changes made
        em.clear(); // This makes sure that any returned entities are no longer attached to this entity manager/persistence context
        em.close(); // and this closes the entity manager
        try { 
            if( ut != null ) { 
                // There's a tx running, close it.
                ut.commit();
            }
        } catch(Exception e) { 
            logger.error("Unable to commit transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    


    protected static UserTransaction findUserTransaction() {
    	InitialContext context = null;
    	try {
            context = new InitialContext();
            return (UserTransaction) context.lookup( "java:comp/UserTransaction" );
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
