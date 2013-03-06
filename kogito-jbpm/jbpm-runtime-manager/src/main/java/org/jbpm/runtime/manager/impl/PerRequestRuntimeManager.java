package org.jbpm.runtime.manager.impl;

import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.runtime.manager.Context;
import org.kie.runtime.manager.Disposable;
import org.kie.runtime.manager.Runtime;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.SessionFactory;
import org.kie.runtime.manager.TaskServiceFactory;

public class PerRequestRuntimeManager extends AbstractRuntimeManager {

    private SessionFactory factory;
    private TaskServiceFactory<TaskServiceEntryPoint> taskServiceFactory;
    
    public PerRequestRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory) {
        super(environment);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
    }
    
    @Override
    public org.kie.runtime.manager.Runtime getRuntime(Context context) {

        Runtime runtime = new RuntimeImpl(factory.newKieSession(), taskServiceFactory.newTaskService());
        registerDisposeCallback(runtime);
        registerItems(runtime);
        return runtime;
    }

    @Override
    public void disposeRuntime(Runtime runtime) {
        
        try {
            runtime.getKieSession().destroy();
        } catch (Exception e) {
            // do nothing
            if (runtime instanceof Disposable) {
                ((Disposable) runtime).dispose();
            }
        }
    }

    @Override
    public void close() {
        factory.close();
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public TaskServiceFactory getTaskServiceFactory() {
        return taskServiceFactory;
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        this.taskServiceFactory = taskServiceFactory;
    }

}
