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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jbpm.task.Group;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.query.TaskSummary;
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

    TaskPersistenceManager(EntityManager em) { 
        this.em = em;
        this.ttxm = new TaskLocalTransactionManager();
    }

    TaskPersistenceManager(EntityManager entityManager, TaskTransactionManager ttxm) { 
        this.em = entityManager;
        this.ttxm = ttxm;
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
        return em.createNamedQuery("UnescalatedDeadlines").getResultList();
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
    
    public Query createQuery(String queryName) { 
        return em.createNamedQuery(queryName);
    }
    
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
        Query query = createQuery(queryName);
        query.setParameter("userId", userId);
        query.setParameter("language", language);
        Object resultListObject = query.getResultList();

        return (List<TaskSummary>) resultListObject;
    }

    public List<TaskSummary> queryTasksWithUserIdStatusAndLanguage(String queryName, String userId, List<Status> status, String language) { 
        Query query = createQuery(queryName);
        query.setParameter("userId", userId);
        query.setParameter("status", status);
        query.setParameter("language", language);
        Object resultListObject = query.getResultList();

        return (List<TaskSummary>) resultListObject;
    }


 
}