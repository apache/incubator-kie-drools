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


import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.jboss.seam.transaction.Transactional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;

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
@Transactional
public class JbpmServicesPersistenceManagerImpl implements JbpmServicesPersistenceManager {

    private static Logger logger = LoggerFactory.getLogger(JbpmServicesPersistenceManagerImpl.class);
    
    private JbpmServicesTransactionManager ttxm;
   
    @Inject
    private EntityManager em;
    
    @Inject
    private EntityManagerFactory emf;

    private boolean sharedEntityManager = false;

    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";

    public JbpmServicesPersistenceManagerImpl() {
    }

    public EntityManager getEm() {
        return em;
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

    public void setTransactionManager(JbpmServicesTransactionManager ttxm) {
        this.ttxm = ttxm;
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
            result = em.createQuery(updateString).executeUpdate();
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
            find = em.find(entityClass, primaryKey);
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
            em.remove(entity);
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
            em.persist(entity);
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
            
            mergedEntity = em.merge(entity);
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
    public Object queryStringWithParametersInTransaction(String queryString, Map<String, Object> params) { 
        Object result = null;
        
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            result = queryStringWithParameters(queryString, params, false);
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
            boolean txOwner = ttxm.begin(em);
            this.ttxm.attachPersistenceContext(em);
            return txOwner;
        }
        return false;
    }

    public void endTransaction(boolean txOwner) { 
        if( ttxm != null){
            try { 
                ttxm.commit(em, txOwner);
            } catch(RuntimeException re) { 
                logger.error("Unable to commit, rolling back transaction.", re);
                this.ttxm.rollback(em, txOwner);

                throw re;
            }
        }
    }
    
    public void rollBackTransaction(boolean txOwner) { 
        if(ttxm != null){
            try { 
                this.ttxm.rollback(em, txOwner);
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
        if( em == null ) { 
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
        boolean closeEm = em.isOpen();
        if ( closeEm  ) { 
            try { 
                ttxm.dispose();
                em.clear();
            }
            catch( Throwable t ) { 
                // Don't worry about it, we're cleaning up. 
            }
            try { 
                em.close();
            }
            catch( Exception e ) { 
                // Don't worry about it, we're cleaning up.
            }
        }
        
        this.em = null;
        this.ttxm = null;
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
    public Object queryWithParametersInTransaction(String queryName, Map<String, Object> params, boolean singleResult) { 
        Object result = null;
        
        boolean txOwner = false;
        boolean operationSuccessful = false;
        boolean txStarted = false;
        try {
            txOwner = beginTransaction();
            txStarted = true;
            
            result = queryWithParameters(queryName, params, singleResult); 
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

    public Object queryWithParametersInTransaction(String queryName, Map<String, Object> params) { 
        return queryWithParametersInTransaction(queryName, params, false);
    }
    
    public Object queryInTransaction(String queryName) { 
        return queryWithParametersInTransaction(queryName, null, false);
    }
    
    /**
     * This method should ONLY be called by the {@link TaskPersistenceManager#queryWithParametersInTransaction(String, Map, boolean)}
     * method.
     * 
     * @param queryName the named query to execute.
     * @param params The parameters
     * @param singleResult If true, only retrieve a single result, otherwise return the result list. 
     * 
     * @return The result of the query. 
     */
    private Object queryWithParameters(String queryName, Map<String, Object> params, boolean singleResult) { 
        Query query = em.createNamedQuery(queryName);
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
    
    
    private Object queryStringWithParameters(String string, Map<String, Object> params, boolean singleResult) { 
        Query query = em.createQuery(string);
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

    

   
}
