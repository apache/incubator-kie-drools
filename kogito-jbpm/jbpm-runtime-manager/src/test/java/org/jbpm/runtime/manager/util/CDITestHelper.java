package org.jbpm.runtime.manager.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.runtime.manager.cdi.qualifier.Singleton;

@ApplicationScoped
public class CDITestHelper {

    private EntityManagerFactory emf;
    
    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment(emf);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2);
        
        return environment;
    }
    
    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa"); 
        }
        
        return this.emf;
    }
}
