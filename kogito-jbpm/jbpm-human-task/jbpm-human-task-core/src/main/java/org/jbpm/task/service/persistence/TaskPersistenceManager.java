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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jbpm.task.Group;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.IncorrectParametersException;
import org.jbpm.task.service.TaskException;
import org.jbpm.task.service.TaskServiceSession;
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
public class TaskPersistenceManager {

    private static Logger logger = LoggerFactory.getLogger(TaskPersistenceManager.class);
    
    private TaskTransactionManager ttxm;
    private EntityManager em;

    private boolean sharedEntityManager = false;

    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";
    
    TaskPersistenceManager(EntityManager em) { 
        this.em = em;
        this.ttxm = new TaskLocalTransactionManager();
    }

    TaskPersistenceManager(EntityManager entityManager, TaskTransactionManager ttxm) { 
        this.em = entityManager;
        this.ttxm = ttxm;
    }

    public void setUseSharedEntityManager(boolean sharedEntityManager)
    {
    	this.sharedEntityManager = sharedEntityManager;
    }

    public boolean isSharedEntityManager()
    {
    	return sharedEntityManager;
    }
    
    //=====
    // dealing with transactions
    //=====
    
    public boolean beginTransaction() { 
        boolean txOwner = this.ttxm.begin(em);
        this.ttxm.attachPersistenceContext(em);
        return txOwner;
    }

    public void endTransaction(boolean txOwner) { 
        try { 
            ttxm.commit(em, txOwner);
        } catch(RuntimeException re) { 
            logger.error("Unable to commit, rolling back transaction.", re);
            this.ttxm.rollback(em, txOwner);
                
            throw re;
        }
    }
    
    public void rollBackTransaction(boolean txOwner) { 
        try { 
            this.ttxm.rollback(em, txOwner);
        } catch(RuntimeException re) { 
            logger.error("Unable to rollback transaction (or to mark as 'to rollback')!", re);
        }
        
    }
    
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

    //=====
    // onetime methods
    //=====
    
    /**
     * Special onetime method
     * @return
     */
    public List<DeadlineSummary> getUnescalatedDeadlines() { 
        boolean txOwner = beginTransaction();
        
        List<DeadlineSummary> resultList = getUnescalatedDeadlinesList();
        
        endTransaction(txOwner);
        return resultList;
    }

    /**
     *  Special onetime method
     * @param taskId
     * @param taskStatus
     */
    public void setTaskStatusInTransaction(final Object taskId, Status taskStatus) { 
        boolean txOwner = beginTransaction();
        
        Task task = (Task) em.find(Task.class, taskId);
        task.getTaskData().setStatus(Status.Completed);
        
        em.persist(task);
        endTransaction(txOwner);
    }
    
    
    //=====
    // In session methods
    //=====
    
    @SuppressWarnings("unchecked")
    public List<DeadlineSummary> getUnescalatedDeadlinesList() { 
        Object result = queryInTransaction("UnescalatedDeadlines");
        return (List<DeadlineSummary>) result;
    }
    
    public Object findEntity(Class<?> entityClass, Object primaryKey) { 
        return this.em.find(entityClass, primaryKey);
    }
    
    public void deleteEntity(Object entity) { 
        em.remove(entity);
    }
    
    public void saveEntity(Object entity) { 
        em.persist(entity);
    }
    
    /**
     * It is strongly suggested that you only use this method within a transaction!!
     * </p>
     * PostgreSQL and DB2 are 2 databases which, depending on your situation, will probably require this. 
     * 
     * @param queryString The JPQL query string to execute.
     * @return The result of the query.
     */
    public Query createNewQuery(String queryString ) { 
        return em.createQuery(queryString);
    }
    
    public boolean userExists(String userId) { 
        if( em.find(User.class, userId) == null ) { 
            return false;
        }
        return true;
    }
    
    public boolean groupExists(String groupId) { 
        if( em.find(Group.class, groupId) == null ) { 
            return false;
        }
        return true;
    }
    
    public List<TaskSummary> queryTasksWithUserIdAndLanguage(String queryName, String userId, String language) { 
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "language", language);
        
        return (List<TaskSummary>) queryWithParametersInTransaction(queryName, params);
    }

    public List<TaskSummary> queryTasksWithUserIdStatusAndLanguage(String queryName, String userId, List<Status> status, String language) { 
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "status", status,
                "language", language);
        
        return (List<TaskSummary>) queryWithParametersInTransaction(queryName, params);
    }

    public List<TaskSummary> queryTasksWithUserIdGroupsAndLanguage(String queryName, String userId, List<String> groupIds, String language) { 
        HashMap<String, Object> params = addParametersToMap(
                "userId", userId,
                "groupIds", groupIds,
                "language", language);

        return (List<TaskSummary>) queryWithParametersInTransaction(queryName, params);
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
            
            if (e instanceof TaskException) {
                throw (TaskException) e;
            } else {
                throw new RuntimeException(message, e);
            }
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
    
    public static HashMap<String, Object> addParametersToMap(Object ... parameterValues) { 
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        
        if( parameterValues.length % 2 != 0 ) { 
            throw new IncorrectParametersException("Expected an even number of parameters, not " + parameterValues.length);
        }
        
        for( int i = 0; i < parameterValues.length; ++i ) {
            String parameterName = null;
            if( parameterValues[i] instanceof String ) { 
                parameterName = (String) parameterValues[i];
            } else { 
                throw new IncorrectParametersException("Expected a String as the parameter name, not a " + parameterValues[i].getClass().getSimpleName());
            }
            ++i;
            parameters.put(parameterName, parameterValues[i]);
        }
        
        return parameters;
    }
}
