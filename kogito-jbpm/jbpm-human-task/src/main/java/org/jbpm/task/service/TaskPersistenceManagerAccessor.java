package org.jbpm.task.service;

import javax.persistence.EntityManagerFactory;

import org.jbpm.task.service.persistence.TaskPersistenceManager;

public abstract class TaskPersistenceManagerAccessor {

    private static volatile TaskPersistenceManagerAccessor factory;
    
    public static TaskPersistenceManagerAccessor getFactory() { 
        TaskPersistenceManagerAccessor accessorImpl = factory;
        
        if( accessorImpl != null ) { 
            return accessorImpl;
        }
        
        try { 
            Class.forName(TaskPersistenceManager.class.getName());
        }
        catch( Exception e ) { 
            // do nothing.
        }
        
        return factory;
    }
    
    public static void setFactory(TaskPersistenceManagerAccessor tpma) { 
        if( factory != null) { 
            throw new IllegalStateException();
        }
        factory = tpma;
    }
    
    protected abstract TaskPersistenceManager newTaskPersistenceManager(EntityManagerFactory emf);
    
}
