
package org.jbpm.runtime.manager.impl.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

@ApplicationScoped
@SuppressWarnings("serial")
public class RuntimeManagerProducer {

    @Inject
    @Any
    private Instance<RuntimeEnvironment> environmentInstance;
    @Inject
    private RuntimeManagerFactory runtimeManagerFactory;
    
    @Produces
    @Singleton
    public RuntimeManager newSingletonRuntimeManager() {
        
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<Singleton>(){}).get();
        
        return runtimeManagerFactory.newSingletonRuntimeManager(environment);
    }
    
    @Produces
    @PerRequest
    public RuntimeManager newPerRequestRuntimeManager() {
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<PerRequest>(){}).get();
        
        return runtimeManagerFactory.newPerRequestRuntimeManager(environment);
    }
    
    @Produces
    @PerProcessInstance
    public RuntimeManager newPerProcessInstanceRuntimeManager() {
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<PerProcessInstance>(){}).get();
        
        return runtimeManagerFactory.newPerProcessInstanceRuntimeManager(environment);
    }
}
