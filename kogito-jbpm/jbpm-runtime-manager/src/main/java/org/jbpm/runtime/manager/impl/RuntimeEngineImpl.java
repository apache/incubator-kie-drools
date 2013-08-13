/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;

/**
 * Implementation of the <code>RuntimeEngine</code> that additionally implement <code>Disposable</code>
 * interface to allow other components to register listeners on it. Usual case is that listeners
 * and work item handlers might be interested in receiving notification when runtime engine is disposed
 * to deactivate itself too and not receive other events.
 * 
 *
 */
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
            } catch(IllegalStateException e){
                // do nothing most likely ksession was already disposed
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

    public boolean isDisposed() {
        return disposed;
    }

}
