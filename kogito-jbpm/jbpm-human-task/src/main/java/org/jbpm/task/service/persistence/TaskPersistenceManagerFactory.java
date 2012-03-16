package org.jbpm.task.service.persistence;

import javax.persistence.EntityManagerFactory;

public class TaskPersistenceManagerFactory {

    TaskPersistenceManagerFactory() {
        // package scope constructor on purpose! (Accessor pattern)    
    }
    
    public TaskPersistenceManager newTaskPersistenceManager(EntityManagerFactory emf) {
        return new TaskPersistenceManager(emf);
    }
    
}
