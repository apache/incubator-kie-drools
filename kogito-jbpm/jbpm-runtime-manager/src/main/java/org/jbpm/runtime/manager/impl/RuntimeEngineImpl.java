package org.jbpm.runtime.manager.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.task.api.TaskService;

public class RuntimeEngineImpl implements RuntimeEngine, Disposable {

    private KieSession ksession;
    private TaskService taskService;
    
    private RuntimeManager manager;
    
    private boolean disposed = false;
    
    private List<DisposeListener> listeners = new CopyOnWriteArrayList<DisposeListener>();
    
    public RuntimeEngineImpl(KieSession ksession, TaskService taskService) {
        this.ksession = ksession;
        this.taskService = taskService;
    }
    
    @Override
    public KieSession getKieSession() {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
        return this.ksession;
    }

    @Override
    public TaskService getTaskService() {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
        return this.taskService;
    }

    @Override
    public void dispose() {
        if (!this.disposed) {         
            // first call listeners and then dispose itself
            for (DisposeListener listener : listeners) {
                listener.onDispose(this);
            }
            try {
                ksession.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.disposed = true;
        }
    }

    @Override
    public void addDisposeListener(DisposeListener listener) {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
        this.listeners.add(listener);
    }

    public RuntimeManager getManager() {
        return manager;
    }

    public void setManager(RuntimeManager manager) {
        this.manager = manager;
    }

}
