package org.jbpm.runtime.manager.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.impl.ThreadPoolSchedulerService;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.kie.api.runtime.EnvironmentName;

public class DefaultRuntimeEnvironment extends SimpleRuntimeEnvironment {

    public DefaultRuntimeEnvironment() {
        super(new DefaultRegisterableItemsFactory());
        this.usePersistence = true;
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf) {
        this(emf, new ThreadPoolSchedulerService(1));
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf, GlobalSchedulerService globalSchedulerService) {
        super(new DefaultRegisterableItemsFactory());
        this.emf = emf;
        this.schedulerService = globalSchedulerService;
        this.usePersistence = true;
        // TODO is this the right one to be default?
        this.userGroupCallback = new MvelUserGroupCallbackImpl();
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf, boolean usePersistence) {
        super(new DefaultRegisterableItemsFactory());
        this.usePersistence = usePersistence;
        this.emf = emf;
        // TODO is this the right one to be default?
        this.userGroupCallback = new MvelUserGroupCallbackImpl();
    }
    
    public void init() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }   
        addToEnvironment(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        if (this.mapper == null) {
            if (this.usePersistence) {
                this.mapper = new JPAMapper(emf);
            } else {
                this.mapper = new InMemoryMapper();
            }
        }
    }

}
