package org.jbpm.runtime.manager.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.runtime.KieSession;
import org.kie.runtime.manager.Disposable;
import org.kie.runtime.manager.DisposeListener;
import org.kie.runtime.manager.Runtime;

public class RuntimeImpl implements Runtime, Disposable {

    private KieSession ksession;
    private TaskServiceEntryPoint taskService;
    
    private boolean disposed = false;
    
    private List<DisposeListener> listeners = new CopyOnWriteArrayList<DisposeListener>();
    
    public RuntimeImpl(KieSession ksession, TaskServiceEntryPoint taskService) {
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
    public TaskServiceEntryPoint getTaskService() {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
        return this.taskService;
    }

    @Override
    public void dispose() {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
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

    @Override
    public void addDisposeListener(DisposeListener listener) {
        if (this.disposed) {
            throw new IllegalStateException("This runtime is already diposed");
        }
        this.listeners.add(listener);
    }

}
