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

import org.jbpm.runtime.manager.impl.factory.CDITaskServiceFactory;
import org.jbpm.runtime.manager.impl.tx.DestroySessionTransactionSynchronization;
import org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.task.api.InternalTaskService;

/**
 * RuntimeManager implementation that is backed by "Per Request" strategy - meaning that for every call to 
 * <code>getRuntimeEngine</code> new instance will be delivered with brand new KieSession and TaskService.
 * The only exception to this is when invoking within same transaction from different places - as then manager
 *  caches currently active instance in ThreadLocal to avoid concurrent modifications - or "lost" of data.
 * On dispose of runtime engine manager will ensure that it is destroyed as well so it will get removed from 
 * data base to avoid out dated data.  
 * <br/>
 * This implementation does not require any special <code>Context</code> to proceed.
 *
 */
public class PerRequestRuntimeManager extends AbstractRuntimeManager {

    private SessionFactory factory;
    private TaskServiceFactory taskServiceFactory;
    
    private static ThreadLocal<RuntimeEngine> local = new ThreadLocal<RuntimeEngine>();
    
    public PerRequestRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        super(environment, identifier);
        this.factory = factory;
        this.taskServiceFactory = taskServiceFactory;
        activeManagers.add(identifier);
    }
    
    @Override
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
        if (local.get() != null) {
            return local.get();
        }
        InternalTaskService internalTaskService = (InternalTaskService) taskServiceFactory.newTaskService();
        configureRuntimeOnTaskService(internalTaskService);
        RuntimeEngine runtime = new RuntimeEngineImpl(factory.newKieSession(), internalTaskService);
        ((RuntimeEngineImpl) runtime).setManager(this);
        registerDisposeCallback(runtime, new DisposeSessionTransactionSynchronization(this, runtime));
        registerDisposeCallback(runtime, new DestroySessionTransactionSynchronization(runtime.getKieSession()));
        registerItems(runtime);
        attachManager(runtime);
        local.set(runtime);
        return runtime;
    }
    

    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
        RuntimeEngine runtimeInUse = local.get();
        if (runtimeInUse == null || runtimeInUse.getKieSession().getId() != ksession.getId()) {
            throw new IllegalStateException("Invalid session was used for this context " + context);
        }
    }

    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
        local.set(null);
        try {
            if (canDestroy()) {
                runtime.getKieSession().destroy();
            } else {
                if (runtime instanceof Disposable) {
                    ((Disposable) runtime).dispose();
                }
            }
        } catch (Exception e) {
            // do nothing
            if (runtime instanceof Disposable) {
                ((Disposable) runtime).dispose();
            }
        }
    }

    @Override
    public void close() {
        try {
            if (taskServiceFactory instanceof CDITaskServiceFactory) {
                // if it's CDI based (meaning single application scoped bean) we need to unregister context
                removeRuntimeFromTaskService((InternalTaskService) taskServiceFactory.newTaskService());
            }
        } catch(Exception e) {
           // do nothing 
        }
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

    @Override
    public void init() {
        configureRuntimeOnTaskService((InternalTaskService) taskServiceFactory.newTaskService());
    }

}
