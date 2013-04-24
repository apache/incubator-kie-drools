package org.jbpm.runtime.manager.impl;

import org.jbpm.runtime.manager.impl.task.SynchronizedTaskService;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.InternalTaskService;

public class SynchronizedRuntimeImpl extends RuntimeEngineImpl {

    private TaskService synchronizedTaskService;
    
    public SynchronizedRuntimeImpl(KieSession ksession, InternalTaskService taskService) {
        super(ksession, taskService);
        this.synchronizedTaskService = new SynchronizedTaskService(ksession, taskService);
    }

    @Override
    public TaskService getTaskService() {

        return this.synchronizedTaskService;
    }

}
