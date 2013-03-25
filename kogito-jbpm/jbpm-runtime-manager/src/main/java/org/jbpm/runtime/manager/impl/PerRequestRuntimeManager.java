package org.jbpm.runtime.manager.impl;

import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;

public class PerRequestRuntimeManager extends AbstractRuntimeManager {

    private SessionFactory factory;
    private TaskServiceFactory taskServiceFactory;
    
    private static ThreadLocal<org.kie.internal.runtime.manager.Runtime> local = new ThreadLocal<org.kie.internal.runtime.manager.Runtime>();
    
    public PerRequestRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
    }
    
    @Override
    public org.kie.internal.runtime.manager.Runtime getRuntime(Context context) {
        if (local.get() != null) {
            return local.get();
        }
        Runtime runtime = new RuntimeImpl(factory.newKieSession(), taskServiceFactory.newTaskService());
        ((RuntimeImpl) runtime).setManager(this);
        registerDisposeCallback(runtime);
        registerItems(runtime);
        attachManager(runtime);
        local.set(runtime);
        return runtime;
    }
    

    @Override
    public void validate(KieSession ksession, Context context) throws IllegalStateException {
        Runtime runtimeInUse = local.get();
        if (runtimeInUse == null || runtimeInUse.getKieSession().getId() != ksession.getId()) {
            throw new IllegalStateException("Invalid session was used for this context " + context);
        }
    }

    @Override
    public void disposeRuntime(Runtime runtime) {
        local.set(null);
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
        super.close();
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
