package org.jbpm.runtime.manager.impl.factory;

import javax.persistence.EntityManagerFactory;

import org.jbpm.task.HumanTaskServiceFactory;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.TaskServiceFactory;

public class LocalTaskServiceFactory implements TaskServiceFactory<TaskServiceEntryPoint> {

    private RuntimeEnvironment runtimeEnvironment;
    
    public LocalTaskServiceFactory(RuntimeEnvironment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }
    @Override
    public TaskServiceEntryPoint newTaskService() {
        EntityManagerFactory emf = (EntityManagerFactory) 
                runtimeEnvironment.getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        if (emf != null) {
            HumanTaskServiceFactory.setEntityManagerFactory(emf);
            TaskServiceEntryPoint internalTaskService = HumanTaskServiceFactory.newTaskService(); 

            return internalTaskService;
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        
    }

}
