/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
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
import org.kie.internal.runtime.manager.context.EmptyContext;
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
    
    private static ThreadLocal<Map<String, RuntimeEngine>> local = new ThreadLocal<Map<String, RuntimeEngine>>() {

        @Override
        protected Map<String, RuntimeEngine> initialValue() {
            return new HashMap<String, RuntimeEngine>();
        }
        
    };
    
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
        if (local.get().get(identifier) != null) {
        	RuntimeEngine engine = local.get().get(identifier);
        	// check if engine is not already disposed as afterCompletion might be issued from another thread
        	if (engine != null && ((RuntimeEngineImpl) engine).isDisposed()) {
        		return null;
        	}
        	
        	return engine;
        }
    	if (engineInitEager) {
	        InternalTaskService internalTaskService = newTaskService(taskServiceFactory);	        
	        runtime = new RuntimeEngineImpl(factory.newKieSession(), internalTaskService);
	        ((RuntimeEngineImpl) runtime).setManager(this);
	        
	        configureRuntimeOnTaskService(internalTaskService, runtime);
	        registerDisposeCallback(runtime, new DisposeSessionTransactionSynchronization(this, runtime), runtime.getKieSession().getEnvironment());
	        registerDisposeCallback(runtime, new DestroySessionTransactionSynchronization(runtime.getKieSession()), runtime.getKieSession().getEnvironment());
	        registerItems(runtime);
	        attachManager(runtime);
    	} else {
    		runtime = new RuntimeEngineImpl(context, new PerRequestInitializer());
	        ((RuntimeEngineImpl) runtime).setManager(this);
    	}
        local.get().put(identifier, runtime);

        return runtime;
    }
    
    @Override
    public void signalEvent(String type, Object event) {
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());
        
        runtimeEngine.getKieSession().signalEvent(type, event);
        
        if (canDispose(runtimeEngine)) {
            disposeRuntimeEngine(runtimeEngine);
        }
    }
    

    @Override
    public void validate(KieSession ksession, Context<?> context) throws IllegalStateException {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
        RuntimeEngine runtimeInUse = local.get().get(identifier);
        if (runtimeInUse == null || runtimeInUse.getKieSession().getIdentifier() != ksession.getIdentifier()) {
            throw new IllegalStateException("Invalid session was used for this context " + context);
        }
    }

    @Override
    public void disposeRuntimeEngine(RuntimeEngine runtime) {
    	if (isClosed()) {
    		throw new IllegalStateException("Runtime manager " + identifier + " is already closed");
    	}
    	try {
        	if (canDispose(runtime)) {
        	    local.get().remove(identifier);
                try {
                    Long ksessionId = ((RuntimeEngineImpl)runtime).getKieSessionId();
                    factory.onDispose(ksessionId);
                    if (canDestroy(runtime)) {
                        runtime.getKieSession().destroy();
                    } else {
                        if (runtime instanceof Disposable) {
                            ((Disposable) runtime).dispose();
                        }
                    }
                    if (ksessionId != null) {
                        TimerService timerService = TimerServiceRegistry.getInstance().get(getIdentifier() + TimerServiceRegistry.TIMER_SERVICE_SUFFIX);
                        if (timerService != null) {
                            if (timerService instanceof GlobalTimerService) {
                                ((GlobalTimerService) timerService).clearTimerJobInstances(ksessionId);
                            }
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                    if (runtime instanceof Disposable) {
                        ((Disposable) runtime).dispose();
                    }
                }
                
                
        	}
    	} catch (Exception e) {
    	    local.get().remove(identifier);
    	    throw new RuntimeException(e);
    	}
    }

    @Override
    public void softDispose(RuntimeEngine runtimeEngine) {        
        super.softDispose(runtimeEngine);
        local.get().remove(identifier);
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
        configureRuntimeOnTaskService(newTaskService(taskServiceFactory), null);
    }   
    
    private class PerRequestInitializer implements RuntimeEngineInitlializer {

    	
    	@Override
    	public KieSession initKieSession(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
    		RuntimeEngine inUse = local.get().get(identifier);
    		if (inUse != null && ((RuntimeEngineImpl) inUse).internalGetKieSession() != null) {
                return inUse.getKieSession();
            }
    		KieSession ksession = factory.newKieSession();
    		((RuntimeEngineImpl)engine).internalSetKieSession(ksession);
    		registerDisposeCallback(engine, new DisposeSessionTransactionSynchronization(manager, engine), ksession.getEnvironment());
            registerDisposeCallback(engine, new DestroySessionTransactionSynchronization(ksession), ksession.getEnvironment());
            registerItems(engine);
            attachManager(engine);
    		return ksession;
    	}

    	@Override    	
    	public TaskService initTaskService(Context<?> context, InternalRuntimeManager manager, RuntimeEngine engine) {
    		InternalTaskService internalTaskService = newTaskService(taskServiceFactory);
            configureRuntimeOnTaskService(internalTaskService, engine);
            
    		return internalTaskService;
    	}

    }

}
