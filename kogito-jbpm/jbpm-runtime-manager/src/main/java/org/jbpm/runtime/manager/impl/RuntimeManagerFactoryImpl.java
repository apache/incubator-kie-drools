package org.jbpm.runtime.manager.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.runtime.manager.impl.factory.InMemorySessionFactory;
import org.jbpm.runtime.manager.impl.factory.JPASessionFactory;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

@ApplicationScoped
public class RuntimeManagerFactoryImpl implements RuntimeManagerFactory {
    
    @Override
    @Produces
    @Singleton
    public RuntimeManager newSingletonRuntimeManager(@Singleton RuntimeEnvironment environment) {
        
        return newSingletonRuntimeManager(environment, "default-singleton");
    }
    @Override
    public RuntimeManager newSingletonRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);
        
        RuntimeManager manager = new SingletonRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
        ((SingletonRuntimeManager) manager).init();

        return manager;
    }

    @Override
    @Produces
    @PerRequest
    public RuntimeManager newPerRequestRuntimeManager(@PerRequest RuntimeEnvironment environment) {

        return newPerRequestRuntimeManager(environment, "default-per-request");
    }
    
    public RuntimeManager newPerRequestRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);

        RuntimeManager manager = new PerRequestRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
        return manager;
    }

    @Override
    @Produces
    @PerProcessInstance
    public RuntimeManager newPerProcessInstanceRuntimeManager(@PerProcessInstance RuntimeEnvironment environment) {

        return newPerProcessInstanceRuntimeManager(environment, "default-per-pinstance");
    }
    
    public RuntimeManager newPerProcessInstanceRuntimeManager(RuntimeEnvironment environment, String identifier) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);

        RuntimeManager manager = new PerProcessInstanceRuntimeManager(environment, factory, taskServiceFactory, identifier);
        initTimerService(environment, manager);
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
    
    protected void initTimerService(RuntimeEnvironment environment, RuntimeManager manager) {
        if (environment instanceof SchedulerProvider) {
            GlobalSchedulerService schedulerService = ((SchedulerProvider) environment).getSchedulerService();  
            if (schedulerService != null) {
                TimerService globalTs = new GlobalTimerService(manager, schedulerService);
                String timerServiceId = manager.getIdentifier() + "-timerServiceId";
                // and register it in the registry under 'default' key
                TimerServiceRegistry.getInstance().registerTimerService(timerServiceId, globalTs);
                ((SimpleRuntimeEnvironment)environment).addToConfiguration("drools.timerService", 
                        "new org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate(\""+timerServiceId+"\")");
            }
        }
    }

}
