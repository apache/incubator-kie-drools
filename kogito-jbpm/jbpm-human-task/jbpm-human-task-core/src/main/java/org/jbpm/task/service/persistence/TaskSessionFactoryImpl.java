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

import org.jbpm.task.admin.TasksAdmin;
import org.jbpm.task.admin.TasksAdminImpl;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.event.TaskEventsAdminImpl;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;

/**
 * DO NOT USE THIS CLASS. THIS IS AN INTERNAL JBPM task. 
 * 
 * If you use this class outside of the org.jbpm.task.service package 
 * (and possibly even then), you ARE ON YOUR OWN. 
 * 
 * IF YOUR CODE BREAKS WHEN THIS CHANGES, GOOD LUCK! 
 * 
 */
public class TaskSessionFactoryImpl implements TaskSessionFactory {

    private final EntityManagerFactory emf;
    private final TaskService taskService;
    private final boolean useJTA;
    
    public TaskSessionFactoryImpl(TaskService taskService, EntityManagerFactory emf) {
        this.emf = emf;
        this.taskService = taskService;
        useJTA = useJTATransactions(emf);
    }

    static boolean useJTATransactions(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        
        boolean useJTA = false;
        try { 
            em.getTransaction();
        } catch(Exception e) { 
            boolean illegalStateExceptionThrown = false;
            Throwable cause = e;
            while( cause != null && ! illegalStateExceptionThrown ) { 
                illegalStateExceptionThrown = (cause instanceof IllegalStateException);
                cause = cause.getCause();
            }
            if( illegalStateExceptionThrown ) { 
                useJTA = true;
            }
            else { 
                // this resource is not JTA
                throw new RuntimeException("Unable to determine persistence-unit type (JTA/Local)", e);
            }
        }
    
        return useJTA;
    }
    
    public TaskServiceSession createTaskServiceSession() {
        TaskPersistenceManager tpm;
        if( useJTA ) { 
            tpm = new TaskPersistenceManager(emf.createEntityManager(), new TaskJTATransactionManager());
        }
        else { 
            tpm = new TaskPersistenceManager(emf.createEntityManager());
        }
        return new TaskServiceSession(taskService, tpm);
    }

    public TasksAdmin createTaskAdmin() {
        TaskPersistenceManager tpm;
        if( useJTA ) { 
            tpm = new TaskPersistenceManager(emf.createEntityManager(), new TaskJTATransactionManager());
        }
        else { 
            tpm = new TaskPersistenceManager(emf.createEntityManager());
        }
        return new TasksAdminImpl(tpm);
    }
    
    public TaskEventsAdmin createTaskEventsAdmin() {
        TaskPersistenceManager tpm;
        if( useJTA ) { 
            tpm = new TaskPersistenceManager(emf.createEntityManager(), new TaskJTATransactionManager());
        }
        else { 
            tpm = new TaskPersistenceManager(emf.createEntityManager());
        }
        return new TaskEventsAdminImpl(tpm);
    }

    
}
