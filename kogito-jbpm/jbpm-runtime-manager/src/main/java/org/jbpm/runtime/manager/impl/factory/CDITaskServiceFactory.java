package org.jbpm.runtime.manager.impl.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.task.api.TaskService;

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
