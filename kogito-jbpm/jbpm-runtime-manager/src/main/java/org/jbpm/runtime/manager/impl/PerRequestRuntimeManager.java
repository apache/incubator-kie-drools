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

import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.jbpm.runtime.manager.impl.tx.DestroySessionTransactionSynchronization;
import org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.InternalTaskService;

/**
 * A RuntimeManager implementation that is backed by the "Per Request" strategy. This means that for every call to 
 * <code>getRuntimeEngine</code>, a new instance will be delivered with brand new KieSession and TaskService.
 * The only exception to this is when this is invoked within the same transaction from different places. In that case, 
 * the manager caches the currently active instance in a ThreadLocal instane to avoid concurrent modifications or "loss" of data.
 * Disposing of the runtime engine manager will ensure that it is destroyed as well, so that it will get removed from 
 * the database to avoid outdated data.  
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
        this.registry.register(this);
    }
    
    @Override
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
    	checkPermission();
    	RuntimeEngine runtime = null;
        if (local.get() != null) {
            return local.get();
        }
    	if (engineInitEager) {
	        InternalTaskService internalTaskService = (InternalTaskService) taskServiceFactory.newTaskService();	        
	        runtime = new RuntimeEngineImpl(factory.newKieSession(), internalTaskService);
	        ((RuntimeEngineImpl) runtime).setManager(this);
	        
	        configureRuntimeOnTaskService(internalTaskService, runtime);
	        registerDisposeCallback(runtime, new DisposeSessionTransactionSynchronization(this, runtime));
	        registerDisposeCallback(runtime, new DestroySessionTransactionSynchronization(runtime.getKieSession()));
	        registerItems(runtime);
	        attachManager(runtime);
    	} else {
    		runtime = new RuntimeEngineImpl(context, new PerRequestInitializer());
	        ((RuntimeEngineImpl) runtime).setManager(this);
    	}
        local.set(runtime);
        return runtime;
    }
    

    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
        RuntimeEngine runtimeInUse = local.get();
        if (runtimeInUse == null || runtimeInUse.getKieSession().getIdentifier() != ksession.getIdentifier()) {
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
            if (canDestroy(runtime)) {
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
            if (!(taskServiceFactory instanceof LocalTaskServiceFactory)) {
                // if it's CDI based (meaning single application scoped bean) we need to unregister context
                removeRuntimeFromTaskService();
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
    	TaskContentRegistry.get().addMarshallerContext(getIdentifier(), 
    			new ContentMarshallerContext(environment.getEnvironment(), environment.getClassLoader()));
        configureRuntimeOnTaskService((InternalTaskService) taskServiceFactory.newTaskService(), null);
    }
    
    private class PerRequestInitializer implements RuntimeEngineInitlializer {

    	
    	@Override
    	public KieSession initKieSession(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
    		RuntimeEngine inUse = local.get();
    		if (inUse != null && ((RuntimeEngineImpl) inUse).internalGetKieSession() != null) {
                return inUse.getKieSession();
            }
    		KieSession ksession = factory.newKieSession();
    		((RuntimeEngineImpl)engine).internalSetKieSession(ksession);
    		registerDisposeCallback(engine, new DisposeSessionTransactionSynchronization(manager, engine));
            registerDisposeCallback(engine, new DestroySessionTransactionSynchronization(ksession));
            registerItems(engine);
            attachManager(engine);
    		return ksession;
    	}

    	@Override    	
    	public TaskService initTaskService(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
    		InternalTaskService internalTaskService = (InternalTaskService) taskServiceFactory.newTaskService();
            configureRuntimeOnTaskService(internalTaskService, engine);
            
    		return internalTaskService;
    	}

    }

}
