package org.jbpm.task.service.persistence;

import javax.persistence.EntityManagerFactory;

import org.jbpm.task.service.TaskPersistenceManagerAccessor;

class TaskPersistenceManagerFactory extends TaskPersistenceManagerAccessor {

    @Override
    protected TaskPersistenceManager newTaskPersistenceManager(EntityManagerFactory emf) {
        return new TaskPersistenceManager(emf);
    }
    
}
