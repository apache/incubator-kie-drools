package org.jbpm.runtime.manager.impl.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.TaskServiceFactory;

@ApplicationScoped
public class CDITaskServiceFactory implements TaskServiceFactory {

    @Inject
    private TaskService taskService;
    
    @Override
    public TaskService newTaskService() {
        return taskService;
    }

    @Override
    public void close() {

    }

}
