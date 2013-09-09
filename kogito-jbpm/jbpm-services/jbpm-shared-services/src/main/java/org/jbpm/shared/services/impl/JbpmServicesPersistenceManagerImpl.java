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


import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.drools.persistence.TransactionSynchronization;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * From the Hibernate docs: 
 * </p>
 * <pre>
 * An EntityManager is an inexpensive, non-threadsafe object that 
 * should be used once, for a single business process, a single unit 
 * of work, and then discarded. An EntityManager will not obtain 
 * a JDBC Connection (or a Datasource) unless it is needed, so 
 * you may safely open and close an EntityManager even if you are 
 * not sure that data access will be needed to serve a particular request.
 * </pre>
 * </p>
 * 
 * This class is a wrapper around the entity manager that handles 
 * all persistence operations. This way, the persistence functionality
 * can be isolated from the human-task server functionality. 
 * </p>
 * 
 * This class is only mean to be used in one thread: with every request
 * handled by the human-task server, a TaskServiceSession is created
 * with an instance of this class. Once the request has been handled, 
 * the TaskServiceSession instance and the TaskPersistenceManager
 * instance are disposed of. 
 * </p>
 */
public class JbpmServicesPersistenceManagerImpl implements JbpmServicesPersistenceManager {

    private static final Logger logger = LoggerFactory.getLogger(JbpmServicesPersistenceManagerImpl.class);
    
    private JbpmServicesTransactionManager ttxm;
   
    @Inject
    private EntityManager em;
    
    @Inject
    private EntityManagerFactory emf;

    private static ThreadLocal<LocalEntityMangerHolder> noScopeEmLocal = new ThreadLocal<LocalEntityMangerHolder>();
    
    private boolean sharedEntityManager = false;

    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";

    public JbpmServicesPersistenceManagerImpl() {
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }
    
    public void setUseSharedEntityManager(boolean sharedEntityManager)
    {
    	this.sharedEntityManager = sharedEntityManager;
    }

    public boolean isSharedEntityManager()
    {
    	return sharedEntityManager;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setTransactionManager(JbpmServicesTransactionManager ttxm) {
        this.ttxm = ttxm;
    }
    public boolean hasTransactionManager() {
        return this.ttxm != null;
    }
    
    @Override
    public int executeUpdateString(String updateString) {
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        int result = 0;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            result = getEm().createQuery(updateString).executeUpdate();
            operationSuccessful = true;
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            throw new RuntimeException(message, e);
            
        }
        return result;
    }
     
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) { 
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        T find = null;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            find = getEm().find(entityClass, primaryKey);
            operationSuccessful = true;
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            throw new RuntimeException(message, e);
            
        }
             
       return find;
    }
    
    @Override
    public void remove(Object entity) { 
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            getEm().remove(entity);
            operationSuccessful = true;
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            throw new RuntimeException(message, e);
            
        }
        
    }
    
    @Override
    public void persist(Object entity) { 
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            getEm().persist(entity);
            operationSuccessful = true;
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            throw new RuntimeException(message, e);
            
        }
        
    }
    
    @Override
    public <T> T merge(T entity) { 
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        T mergedEntity = null;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            
            mergedEntity = getEm().merge(entity);
            operationSuccessful = true;
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
            
            throw new RuntimeException(message, e);
            
        }
        return mergedEntity;
        
    }
    
    @Override
    public Object queryAndLockStringWithParametersInTransaction(String queryString, Map<String, Object> params, boolean singleResult){
        Object result = null;
        
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            result = queryStringWithParameters(queryString, params, singleResult, LockModeType.PESSIMISTIC_WRITE);
            operationSuccessful = true;   
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
             throw new RuntimeException(message, e);
           
        }
        return result;
    }

    @Override
    public Object queryStringWithParametersInTransaction(String queryString, Map<String, Object> params) { 
        Object result = null;
        
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            result = queryStringWithParameters(queryString, params, false, null);
             operationSuccessful = true;   
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message; 
            if( !txStarted ) { message = "Could not start transaction."; }
            else if( !operationSuccessful ) { message = "Operation failed"; }
            else { message = "Could not commit transaction"; }
             throw new RuntimeException(message, e);
           
        }
        return result;
            
    }
    
    @Override
    public Object queryStringInTransaction(String queryString) {
        return queryStringWithParametersInTransaction(queryString, null);
    }
   
    
    
    
    
    //=====
    // dealing with transactions
    //=====
    
    public boolean beginTransaction() { 
        if(ttxm != null){
            boolean txOwner = ttxm.begin(getEm());
            this.ttxm.attachPersistenceContext(getEm());
            registerTxSync();
            return txOwner;
        }
        return false;
    }


    public void endTransaction(boolean txOwner) { 
        if( ttxm != null){
            try { 
                ttxm.commit(getEm(), txOwner);
            } catch(RuntimeException re) { 
                logger.error("Unable to commit, rolling back transaction.", re);
                this.ttxm.rollback(getEm(), txOwner);

                throw re;
            }
        }
    }
    
    public void rollBackTransaction(boolean txOwner) { 
        if(ttxm != null){
            try { 
                this.ttxm.rollback(getEm(), txOwner);
            } catch(RuntimeException re) { 
                logger.error("Unable to rollback transaction (or to mark as 'to rollback')!", re);
            }
        }
        
    }
    
    @Override
    public void dispose() { 
        endPersistenceContext();
    }
    
    public void endPersistenceContext() { 
        if( getEm() == null ) { 
            ttxm = null;
            return;
        }
        
        if (sharedEntityManager) {
/*
        	try {
        		ttxm.dispose();        		
        	} catch( Throwable t ) { 
                // Don't worry about it, we're cleaning up.
            	logger.error("taskPersistenceManager.endPersistenceContext()::sharedEntityManager", t);
            }
        	this.ttxm = null;
*/
        	return;
        }
        boolean closeEm = getEm().isOpen();
        if ( closeEm  ) { 
            try { 
                ttxm.dispose();
                getEm().clear();
            }
            catch( Throwable t ) { 
                // Don't worry about it, we're cleaning up. 
            }
            try { 
            	getEm().close();
            }
            catch( Exception e ) { 
                // Don't worry about it, we're cleaning up.
            }
        }
        
        this.em = null;
        noScopeEmLocal.set(null);
        this.ttxm = null;
    }

   public EntityManager getEm() {
        try {
            if (this.em != null) {
                this.em.toString();  
                return this.em;
            } else {
                return this.emf.createEntityManager();
            }
        } catch (ContextNotActiveException e) {
            /*
             * Special handling in case there is no context (RequestScoped in most cases) available
             * so we create new EntityManager to be used for the life time of current transaction.
             * It will be reset by the transaction synchronization on transaction completion.
             * This is usually used with background tasks triggered by timers so no RequestScope
             */
            LocalEntityMangerHolder noScopeEntityManager = noScopeEmLocal.get();
            logger.debug("No ctx available trying to use no scoped entity manager {}", noScopeEntityManager);
            if (noScopeEntityManager == null) {                
                noScopeEntityManager = new LocalEntityMangerHolder(emf.createEntityManager());
                logger.debug("local (no scoped) entity manager was not set, creating new entity manager {}", noScopeEntityManager);
                noScopeEmLocal.set(noScopeEntityManager);
         
            }
            return noScopeEntityManager.getEntityManager();
        }

    }


    /**
     * This method runs a query within a transaction and returns the result. 
     * 
     * This logic is unfortunately duplicated in {@link TaskServiceSession#doOperationInTransaction}. 
     * If you change the logic here, please make sure to change the logic there as well (and vice versa). 
     * 
     * @param queryName
     * @param params
     * @return
     */   
    public Object queryWithParametersInTransaction(String queryName,
            Map<String, Object> params, boolean singleResult) {
        Object result = null;

        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;

            result = queryWithParameters(queryName, params, singleResult, null);
            operationSuccessful = true;

            endTransaction(txOwner);
        } catch (Exception e) {
            rollBackTransaction(txOwner);

            String message;
            if (!txStarted) {
                message = "Could not start transaction.";
            } else if (!operationSuccessful) {
                message = "Operation failed";
            } else {
                message = "Could not commit transaction";
            }
            throw new RuntimeException(message, e);

        }
        return result;
    }
   
    @Override
    public Object queryAndLockWithParametersInTransaction(String queryName, Map<String, Object> params, boolean singleResult) { 
        Object result = null;
        
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            
            result = queryWithParameters(queryName, params, singleResult, LockModeType.PESSIMISTIC_WRITE); 
            operationSuccessful = true;   
            
            endTransaction(txOwner);
        } catch(Exception e) {
            rollBackTransaction(txOwner);
            
            String message;
            if (!txStarted) {
                message = "Could not start transaction.";
            } else if (!operationSuccessful) {
                message = "Operation failed";
            } else {
                message = "Could not commit transaction";
            }
            throw new RuntimeException(message, e);
           
        }
        return result;
    }


    public Object queryWithParametersInTransaction(String queryName, Map<String, Object> params) { 
        return queryWithParametersInTransaction(queryName, params, false);
    }
    
    public Object queryInTransaction(String queryName) { 
        return queryWithParametersInTransaction(queryName, null, false);
    }
    
    /**
     * 
     * @param queryName the named query to execute.
     * @param params The parameters
     * @param singleResult If true, only retrieve a single result, otherwise return the result list. 
     * 
     * @return The result of the query. 
     */
    private Object queryWithParameters(String queryName, Map<String, Object> params, boolean singleResult, LockModeType lockMode) { 
        Query query = getEm().createNamedQuery(queryName);
        if (lockMode != null) {
            query.setLockMode(lockMode);
        }
        if( params != null && ! params.isEmpty() ) { 
            for( String name : params.keySet() ) { 
                if( FIRST_RESULT.equals(name) ) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if( MAX_RESULTS.equals(name) ) { 
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                query.setParameter(name, params.get(name) );
            }
        }
        if( singleResult ) { 
            return query.getSingleResult();
        }
        return query.getResultList();
    }
    
    
    private Object queryStringWithParameters(String string, Map<String, Object> params, boolean singleResult, LockModeType lockMode) { 
        Query query = getEm().createQuery(string);
        if (lockMode != null) {
            query.setLockMode(lockMode);
        }
        if( params != null && ! params.isEmpty() ) { 
            for( String name : params.keySet() ) { 
                if( FIRST_RESULT.equals(name) ) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if( MAX_RESULTS.equals(name) ) { 
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                query.setParameter(name, params.get(name) );
            }
        }
        if( singleResult ) { 
            return query.getSingleResult();
        }
        return query.getResultList();
    }
    
    public HashMap<String, Object> addParametersToMap(Object ... parameterValues) { 
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        
        if( parameterValues.length % 2 != 0 ) { 
            throw new RuntimeException("Expected an even number of parameters, not " + parameterValues.length);
        }
        
        for( int i = 0; i < parameterValues.length; ++i ) {
            String parameterName = null;
            if( parameterValues[i] instanceof String ) { 
                parameterName = (String) parameterValues[i];
            } else { 
                throw new RuntimeException("Expected a String as the parameter name, not a " + parameterValues[i].getClass().getSimpleName());
            }
            ++i;
            parameters.put(parameterName, parameterValues[i]);
        }
        
        return parameters;
    }

    private void registerTxSync() {
        LocalEntityMangerHolder holder = noScopeEmLocal.get();
        if (holder != null && !holder.isRegistered()) {
            logger.debug("Registering transaction sync for local (no scoped) entity manager");
            ttxm.registerTXSynchronization(new TransactionSynchronization() {
                
                @Override
                public void beforeCompletion() {                        
                }
                
                @Override
                public void afterCompletion(int status) {
                    logger.debug("Cleaning local (no scoped) entity manager on tx completion");
                    noScopeEmLocal.set(null);
                }
            });
            holder.setRegistered(true);
        }
    }    

    private class LocalEntityMangerHolder {
        private boolean registered;
        private EntityManager entityManager;

        public LocalEntityMangerHolder(EntityManager em) {
            this.entityManager = em;
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        @Override
        public String toString() {
            return "LocalEntityMangerHolder [registered=" + registered
                    + ", entityManager=" + entityManager + "]";
        }
    }
}
