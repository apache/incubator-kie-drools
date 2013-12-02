/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.kie.services.test;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;


/**
 *
 */
@ApplicationScoped
public class TestEnvironmentProducer {
    
    private EntityManagerFactory emf;      
    
    @PersistenceUnit(unitName = "org.jbpm.domain")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            // this needs to be here for non EE containers

            this.emf = Persistence.createEntityManagerFactory("org.jbpm.domain");

        }
        return this.emf;
    }
    
    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        Properties properties= new Properties();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .entityManagerFactory(emf).userGroupCallback( new JBossUserGroupCallbackImpl(properties))
                .get();
        return environment;
    }
}
