package org.jbpm.runtime.manager.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

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
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);
        
        RuntimeManager manager = new SingletonRuntimeManager(environment, factory, taskServiceFactory);
        ((SingletonRuntimeManager) manager).init();
        return manager;
    }

    @Override
    @Produces
    @PerRequest
    public RuntimeManager newPerRequestRuntimeManager(@PerRequest RuntimeEnvironment environment) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);

        RuntimeManager manager = new PerRequestRuntimeManager(environment, factory, taskServiceFactory);
        return manager;
    }

    @Override
    @Produces
    @PerProcessInstance
    public RuntimeManager newPerProcessInstanceRuntimeManager(@PerProcessInstance RuntimeEnvironment environment) {
        SessionFactory factory = getSessionFactory(environment);
        TaskServiceFactory taskServiceFactory = new LocalTaskServiceFactory(environment);

        RuntimeManager manager = new PerProcessInstanceRuntimeManager(environment, factory, taskServiceFactory);
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

}
