package org.jbpm.runtime.manager.impl;

import org.jbpm.runtime.manager.impl.task.SynchronizedTaskService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.runtime.KieSession;

public class SynchronizedRuntimeImpl extends RuntimeImpl {

    private TaskServiceEntryPoint synchronizedTaskService;
    
    public SynchronizedRuntimeImpl(KieSession ksession, TaskServiceEntryPoint taskService) {
        super(ksession, taskService);
        this.synchronizedTaskService = new SynchronizedTaskService(ksession, taskService);
    }

    @Override
    public TaskServiceEntryPoint getTaskService() {

        return this.synchronizedTaskService;
    }

}
