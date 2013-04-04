package org.jbpm.runtime.manager.impl;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

public class RuntimeEnvironmentBuilder {

    private SimpleRuntimeEnvironment runtimeEnvironment;
    
    public RuntimeEnvironmentBuilder() {
        this.runtimeEnvironment = new SimpleRuntimeEnvironment();
    }
    
    private RuntimeEnvironmentBuilder(SimpleRuntimeEnvironment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }
    
    public static RuntimeEnvironmentBuilder getEmpty() {
        return new RuntimeEnvironmentBuilder();
    }
    
    public static RuntimeEnvironmentBuilder getDefault() {
        return new RuntimeEnvironmentBuilder(new DefaultRuntimeEnvironment());
    }
    
    public static RuntimeEnvironmentBuilder getDefaultInMemory() {
        RuntimeEnvironmentBuilder builder = new RuntimeEnvironmentBuilder(new DefaultRuntimeEnvironment(null, false));
        builder
        .addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName())
        .addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        
        return builder;
    }
    
    public RuntimeEnvironmentBuilder persistence(boolean persistenceEnabled) {
        this.runtimeEnvironment.setUsePersistence(persistenceEnabled);
        
        return this;
    }
    
    public RuntimeEnvironmentBuilder entityManagerFactory(EntityManagerFactory emf) {
        this.runtimeEnvironment.setEmf(emf);
        
        return this;
    }
    
    public RuntimeEnvironmentBuilder addAsset(Resource asset, ResourceType type) {
        this.runtimeEnvironment.addAsset(asset, type);
        return this;
    }
    
    public RuntimeEnvironmentBuilder addEnvironmentEntry(String name, Object value) {
        this.runtimeEnvironment.addToEnvironment(name, value);
        
        return this;
    }
    
    public RuntimeEnvironmentBuilder addConfiguration(String name, String value) {
        this.runtimeEnvironment.addToConfiguration(name, value);
        
        return this;
    }
    
    public RuntimeEnvironmentBuilder knowledgeBase(KieBase kbase) { 
        this.runtimeEnvironment.setKieBase(kbase);
        
        return this;
    }
    
    public RuntimeEnvironmentBuilder userGroupCallback(UserGroupCallback callback) {
        this.runtimeEnvironment.setUserGroupCallback(callback);
    
        return this;
    }
    
    public RuntimeEnvironmentBuilder mapper(Mapper mapper) {
        this.runtimeEnvironment.setMapper(mapper);
    
        return this;
    }
    
    public RuntimeEnvironmentBuilder registerableItemsFactory(RegisterableItemsFactory factory) { 
        this.runtimeEnvironment.setRegisterableItemsFactory(factory);
        
        return this;
    }
    
    public RuntimeEnvironment get() {
        this.runtimeEnvironment.init();
        return this.runtimeEnvironment;
    }

    public RuntimeEnvironmentBuilder schedulerService(GlobalSchedulerService globalScheduler) {
        
        this.runtimeEnvironment.setSchedulerService(globalScheduler);
        return this;
    }
}
