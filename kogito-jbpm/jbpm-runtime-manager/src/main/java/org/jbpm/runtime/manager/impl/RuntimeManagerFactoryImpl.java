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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.runtime.manager.api.SchedulerProvider;
import org.jbpm.runtime.manager.impl.factory.InMemorySessionFactory;
import org.jbpm.runtime.manager.impl.factory.JPASessionFactory;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.jbpm.runtime.manager.impl.tx.TransactionAwareSchedulerServiceInterceptor;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;

/**
 * Main entry point class for RuntimeManager module responsible for delivering <code>RuntimeManager</code>
 * instances based on given <code>RuntimeEnvironment</code>.
 * <br/>
 * It can be used in both CDI and non CDI environments although it does not produce RuntimeManager instance for CDI 
 * automatically but would be more used as injection to other beans that might be interested in creating 
 * <code>RuntimeManager</code> instances on demand.
 * <br/>
 * This factory will try to discover several services before building RuntimeManager:
 * <ul>
 *  <li>SessionFactory - depending if persistence is enabled will select appropriate instance</li>
 *  <li>TaskServiceFactory - depending if TaskServiceFactory gets injected will select appropriate instance</li>
 *  <li>TimerService - depending if <code>SchedulerService</code> is given will create <code>GlobalTimerService</code></li>
 * </ul>
 *
 */
@ApplicationScoped
public class RuntimeManagerFactoryImpl implements RuntimeManagerFactory {
    
    @Inject
    private Instance<TaskServiceFactory> taskServiceFactoryInjected;
    
    @Override
    public RuntimeManager newSingletonRuntimeManager(RuntimeEnvironment environment) {
        
        return newSingletonRuntimeManager(environment, "default-singleton");
    }
    @Override
    public RuntimeManager newSingletonRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = getTaskServiceFactory(environment);
        
        RuntimeManager manager = new SingletonRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
        ((AbstractRuntimeManager) manager).init();

        return manager;
    }

    @Override    
    public RuntimeManager newPerRequestRuntimeManager(RuntimeEnvironment environment) {

        return newPerRequestRuntimeManager(environment, "default-per-request");
    }
    
    public RuntimeManager newPerRequestRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = getTaskServiceFactory(environment);

        RuntimeManager manager = new PerRequestRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
        ((AbstractRuntimeManager) manager).init();
        return manager;
    }

    @Override
    public RuntimeManager newPerProcessInstanceRuntimeManager(RuntimeEnvironment environment) {

        return newPerProcessInstanceRuntimeManager(environment, "default-per-pinstance");
    }
    
    public RuntimeManager newPerProcessInstanceRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = getTaskServiceFactory(environment);

        RuntimeManager manager = new PerProcessInstanceRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
        ((AbstractRuntimeManager) manager).init();
        return manager;
    }
    
    protected SessionFactory getSessionFactory(RuntimeEnvironment environment) {
        SessionFactory factory = null;
        if (environment.usePersistence()) {
            factory = new JPASessionFactory(environment);
        } else {
            factory = new InMemorySessionFactory(environment);
        }
        
        return factory;
    }

    protected TaskServiceFactory getTaskServiceFactory(RuntimeEnvironment environment) {
    	
    	// if there is an implementation of TaskServiceFactory in the environment then use it
        TaskServiceFactory taskServiceFactory = (TaskServiceFactory) ((SimpleRuntimeEnvironment)environment).getEnvironmentTemplate()
        											.get("org.kie.internal.runtime.manager.TaskServiceFactory");
        if (taskServiceFactory != null) {
        	return taskServiceFactory;
        }
        try {
            taskServiceFactory = taskServiceFactoryInjected.get();
            // since this is CDI let's make sure it has all dependencies met
            taskServiceFactory.newTaskService().toString();
        } catch (Exception e) {
            taskServiceFactory = new LocalTaskServiceFactory(environment);
        }
        
        return taskServiceFactory;
    }
    
    protected void initTimerService(RuntimeEnvironment environment, RuntimeManager manager) {
        if (environment instanceof SchedulerProvider) {
            GlobalSchedulerService schedulerService = ((SchedulerProvider) environment).getSchedulerService();  
            if (schedulerService != null) {
                TimerService globalTs = new GlobalTimerService(manager, schedulerService);
                String timerServiceId = manager.getIdentifier()  + TimerServiceRegistry.TIMER_SERVICE_SUFFIX;
                // and register it in the registry under 'default' key
                TimerServiceRegistry.getInstance().registerTimerService(timerServiceId, globalTs);
                ((SimpleRuntimeEnvironment)environment).addToConfiguration("drools.timerService", 
                        "new org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate(\""+timerServiceId+"\")");
                
                if (!schedulerService.isTransactional()) {
                    schedulerService.setInterceptor(new TransactionAwareSchedulerServiceInterceptor(environment, manager, schedulerService));
                }
            }
        }
    }
    


}
