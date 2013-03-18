package org.jbpm.runtime.manager.impl;

import org.jbpm.runtime.manager.impl.task.SynchronizedTaskService;
import org.kie.api.runtime.KieSession;
import org.kie.internal.task.api.TaskService;

public class SynchronizedRuntimeImpl extends RuntimeImpl {

    private TaskService synchronizedTaskService;
    
    public SynchronizedRuntimeImpl(KieSession ksession, TaskService taskService) {
        super(ksession, taskService);
        this.synchronizedTaskService = new SynchronizedTaskService(ksession, taskService);
    }

    @Override
    public TaskService getTaskService() {

        return this.synchronizedTaskService;
    }

}
