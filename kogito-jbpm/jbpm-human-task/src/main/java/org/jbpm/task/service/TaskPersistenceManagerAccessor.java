package org.jbpm.task.service;

import org.jbpm.task.service.persistence.TaskPersistenceManager;
import org.jbpm.task.service.persistence.TaskPersistenceManagerFactory;

public abstract class TaskPersistenceManagerAccessor {

    private static volatile TaskPersistenceManagerFactory factory;
    
    protected TaskPersistenceManagerFactory getTaskPersistenceManagerFactory() { 
        TaskPersistenceManagerFactory factoryInstance = factory;
        
        if( factoryInstance != null ) { 
            return factoryInstance;
        }
        
        try { 
            Class.forName(TaskPersistenceManager.class.getName(), true, TaskPersistenceManager.class.getClassLoader());
        }
        catch( Exception e ) { 
            // do nothing.
        }
        
        return factory;
    }
    
    public static void setTaskPersistenceManagerFactory(TaskPersistenceManagerFactory tpmf) { 
        // Only setting when not null prevents abuse or resetting later..
        if( factory == null) { 
            factory = tpmf;
        }
    }
    
}
