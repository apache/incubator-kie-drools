package org.jbpm.runtime.manager.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.kie.runtime.EnvironmentName;

public class DefaultRuntimeEnvironment extends SimpleRuntimeEnvironment {

    private EntityManagerFactory emf;
    
    public DefaultRuntimeEnvironment() {
        super(new DefaultRegisterableItemsFactory());
        init();
    }
    
    public DefaultRuntimeEnvironment(EntityManagerFactory emf) {
        super(new DefaultRegisterableItemsFactory());
        this.emf = emf;
        init();
    }
    
    public void init() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }   
        addToEnvironment(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        this.mapper = new JPAMapper(emf);
    }

}
