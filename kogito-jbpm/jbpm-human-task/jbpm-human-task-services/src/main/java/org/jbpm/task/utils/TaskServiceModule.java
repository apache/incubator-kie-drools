/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.utils;


import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;

/**
 *
 */
public class TaskServiceModule {
    private static TaskServiceModule instance;
    private TaskServiceEntryPoint taskService;
    private WeldContainer container;
    private Weld weld;
    
    public static TaskServiceModule getInstance(){
        if(instance == null){
            instance = new TaskServiceModule();
        }
        return instance;
    }

    public TaskServiceModule() {
        weld = new Weld();
        this.container = weld.initialize();
        
        this.taskService = this.container.instance().select(TaskServiceEntryPointImpl.class).get();
        //Singleton.. that we need to instantiate
        this.container.instance().select(TaskLifeCycleEventListener.class).get(); 
    }

    public TaskServiceEntryPoint getTaskService() {
        return this.taskService;
    }

    public WeldContainer getContainer() {
        return container;
    }
    
    public void dispose(){
        instance = null;
        weld.shutdown();
    }
    
    
}
