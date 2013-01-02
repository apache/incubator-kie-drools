package org.jbpm.runtime.manager.impl;

import java.util.Properties;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

public class SimpleRuntimeEnvironment implements RuntimeEnvironment, SchedulerProvider {
    
    protected Environment environment;
    protected KieSessionConfiguration configuration;
    protected KieBase kbase;
    protected KnowledgeBuilder kbuilder;
    protected RegisterableItemsFactory registerableItemsFactory;
    protected Mapper mapper;
    protected UserGroupCallback userGroupCallback;
    protected GlobalSchedulerService schedulerService;
    
    protected Properties sessionConfigProperties;
    
    public SimpleRuntimeEnvironment() {
        this.environment = EnvironmentFactory.newEnvironment();
        this.kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        this.registerableItemsFactory = new SimpleRegisterableItemsFactory();
        this.mapper = new InMemoryMapper();
    }
    public SimpleRuntimeEnvironment(RegisterableItemsFactory registerableItemsFactory) {
        this.environment = EnvironmentFactory.newEnvironment();
        this.kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        this.registerableItemsFactory = registerableItemsFactory;
        this.mapper = new InMemoryMapper();
    }
    
    public void addAsset(Resource resource, ResourceType type) {
        this.kbuilder.add(resource, type);
    }
    
    public void addToEnvironment(String name, Object value) {
        this.environment.set(name, value);
    }
    
    public void addToConfiguration(String name, String value) {
        if (this.sessionConfigProperties == null) {
            this.sessionConfigProperties = new Properties();
        }
        this.sessionConfigProperties.setProperty(name, value);
    }

    @Override
    public KieBase getKieBase() {
        if (this.kbase == null) {
            this.kbase = kbuilder.newKnowledgeBase();
        }
        return this.kbase;
    }

    @Override
    public Environment getEnvironment() {
        // this environment is like template always make a new copy when this method is called
        return copyEnvironment();
    }

    @Override
    public KieSessionConfiguration getConfiguration() {
        if (this.sessionConfigProperties != null) {
            return KnowledgeBaseFactory.newKnowledgeSessionConfiguration(this.sessionConfigProperties);
        }
        return null;
    }
    @Override
    public boolean usePersistence() {
        
        return environment.get(EnvironmentName.ENTITY_MANAGER_FACTORY) != null;
    }
    
    @Override
    public RegisterableItemsFactory getRegisterableItemsFactory() {
        return this.registerableItemsFactory;
    }
    
    @Override
    public void close() {

    }

    protected void addIfPresent(String name, Environment copy) {
        Object value = this.environment.get(name);
        if (value != null) {
            copy.set(name, value);
        }
    }
    
    protected Environment copyEnvironment() {
        Environment copy = EnvironmentFactory.newEnvironment();
        
        addIfPresent(EnvironmentName.ENTITY_MANAGER_FACTORY,copy);
        addIfPresent(EnvironmentName.CALENDARS, copy);
        addIfPresent(EnvironmentName.DATE_FORMATS, copy);
        addIfPresent(EnvironmentName.GLOBALS, copy);
        addIfPresent(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, copy);
        addIfPresent(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, copy);
        addIfPresent(EnvironmentName.TRANSACTION_MANAGER, copy);
        addIfPresent(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, copy);
        
        return copy;
    }
    @Override
    public Mapper getMapper() {
        return this.mapper;
    }
    
    @Override
    public UserGroupCallback getUserGroupCallback() {
        return this.userGroupCallback;
    }
    
    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }
    public Properties getSessionConfigProperties() {
        return sessionConfigProperties;
    }
    public void setSessionConfigProperties(Properties sessionConfigProperties) {
        this.sessionConfigProperties = sessionConfigProperties;
    }
    public void setRegisterableItemsFactory(
            RegisterableItemsFactory registerableItemsFactory) {
        this.registerableItemsFactory = registerableItemsFactory;
    }
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public GlobalSchedulerService getSchedulerService() {
        return this.schedulerService;
    }
    public void setSchedulerService(GlobalSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
