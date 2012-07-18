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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.TransactionManager;
import org.jbpm.task.admin.TasksAdmin;
import org.jbpm.task.admin.TasksAdminImpl;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.event.TaskEventsAdminImpl;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;

/**
 * THE ONLY ACCEPTED USE OF THIS CLASS IS AS AN INJECTED CLASS.
 *  
 * DO NOT USE THIS CLASS IN ANY OTHER WAY. 
 * 
 * If you otherwise use this class outside of the org.jbpm.task.service package 
 * (and possibly even then), you ARE ON YOUR OWN. 
 * 
 * IF YOUR CODE BREAKS WHEN THIS CHANGES, GOOD LUCK! 
 * 
 */
public class TaskSessionSpringFactoryImpl implements TaskSessionFactory {

    private EntityManagerFactory emf;
    private EntityManager springEM;
    private Boolean useEMF = null;
    
    private TransactionManager springTransactionManager;
    private boolean useJTA;

    private TaskService taskService;
   
    public TaskSessionSpringFactoryImpl() { 
        // Default constructor
    }
    
    public void setEntityManagerFactory(EntityManagerFactory emf) { 
        this.emf = emf;
        if( this.springEM == null ) { 
            useEMF = true;
        }
    }

    public void setEntityManager(EntityManager em) { 
        this.springEM = em;
        useEMF = false;
    }
    
    public void setTransactionManager(TransactionManager txm) { 
        this.springTransactionManager = txm;
    }
    
    public void setUseJTA(boolean useJTA) { 
        this.useJTA = useJTA;
    }
    
    public void setTaskService(TaskService taskService) { 
        this.taskService = taskService;
    }

    public TaskServiceSession createTaskServiceSession() {
        TaskPersistenceManager tpm;
     
        TaskSpringTransactionManager ttxm = new TaskSpringTransactionManager(springTransactionManager, useJTA);
        if (useEMF) {
            tpm = new TaskPersistenceManager(emf.createEntityManager(), ttxm);
        } else {
            tpm = new TaskPersistenceManager(springEM, ttxm);
            tpm.setUseSharedEntityManager(true);
            //Must comment out this line setUseJTA(true) does not work with this line
            //ttxm.begin(null);
        }
        return new TaskServiceSession(taskService, tpm);
    }

    public TasksAdmin createTaskAdmin() {
        TaskPersistenceManager tpm;
        TaskSpringTransactionManager ttxm = new TaskSpringTransactionManager(springTransactionManager, useJTA);
        if (useEMF) {
            tpm = new TaskPersistenceManager(emf.createEntityManager(), ttxm);
        } else {
            tpm = new TaskPersistenceManager(springEM, ttxm);
            tpm.setUseSharedEntityManager(true);
        }
        return new TasksAdminImpl(tpm);
    }

    public void initialize() { 
        this.taskService.setTaskSessionFactory(this);
        this.taskService.initialize();
    }

    public TaskEventsAdmin createTaskEventsAdmin() {
        TaskPersistenceManager tpm;
        TaskSpringTransactionManager ttxm = new TaskSpringTransactionManager(springTransactionManager, useJTA);
        if (useEMF) {
            tpm = new TaskPersistenceManager(emf.createEntityManager(), ttxm);
        } else {
            tpm = new TaskPersistenceManager(springEM, ttxm);
        }
        return new TaskEventsAdminImpl(tpm);
    }
    
}
