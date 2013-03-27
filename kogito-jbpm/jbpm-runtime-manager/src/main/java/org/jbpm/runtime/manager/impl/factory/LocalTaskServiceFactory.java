package org.jbpm.runtime.manager.impl.factory;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.task.api.TaskService;

public class LocalTaskServiceFactory implements TaskServiceFactory {

    private RuntimeEnvironment runtimeEnvironment;
    
    public LocalTaskServiceFactory(RuntimeEnvironment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }
    @Override
    public TaskService newTaskService() {
        EntityManagerFactory emf = (EntityManagerFactory) 
                runtimeEnvironment.getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        if (emf != null) {
            
            TaskService internalTaskService =   HumanTaskServiceFactory.newTaskServiceConfigurator()
            .transactionManager(new JbpmJTATransactionManager())
            .entityManagerFactory(emf)
            .userGroupCallback(runtimeEnvironment.getUserGroupCallback())
            .getTaskService();
                        
            return internalTaskService;
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        
    }

}
