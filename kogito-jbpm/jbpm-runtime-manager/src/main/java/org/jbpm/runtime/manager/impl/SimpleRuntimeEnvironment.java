/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.runtime.manager.api.SchedulerProvider;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.UserInfo;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceTypeImpl;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

/**
 * The most basic implementation of the <code>RuntimeEnvironment</code> that, at the same time, serves as base 
 * implementation for all extensions. Encapsulates all important configuration that <code>RuntimeManager</code>
 * requires for execution.
 * <ul>
 *  <li>EntityManagerFactory - shared for all runtime engine build based on same <code>RuntimeEnvironment</code></li>
 *  <li>Environment - Drools/jBPM environment object - will be cloned for every <code>RuntimeEngine</code></li>
 *  <li>KieSessionConfiguration - will be build passed on defined properties - cloned for every <code>RuntimeEngine</code></li>
 *  <li>KieBase - resulting knowledge base build on given assets or returned if it was preset</li>
 *  <li>RegisterableItemsFactory - factory used to provide listeners and work item handlers</li>
 *  <li>Mapper - mapper used to keep context information</li>
 *  <li>UserGroupCallback - user group callback, if not given null will be returned</li>
 *  <li>GlobalSchedulerService - since this environment implements <code>SchedulerProvider</code>
 *  it allows to get <code>GlobalTimerService</code> if available</li>
 * </ul>
 *
 */
public class SimpleRuntimeEnvironment implements RuntimeEnvironment, SchedulerProvider {
    
    protected boolean usePersistence;
    protected EntityManagerFactory emf;
    
    protected Map<String, Object> environmentEntries;
    protected Environment environment;
    protected KieSessionConfiguration configuration;
    protected KieBase kbase;
    protected KnowledgeBuilder kbuilder;
    protected RegisterableItemsFactory registerableItemsFactory;
    protected Mapper mapper;
    protected UserGroupCallback userGroupCallback;
    protected UserInfo userInfo;
    protected GlobalSchedulerService schedulerService;
    protected ClassLoader classLoader;
    
    protected Properties sessionConfigProperties;      
    
    public SimpleRuntimeEnvironment() {
        this(new SimpleRegisterableItemsFactory());        
    }
    
    public SimpleRuntimeEnvironment(RegisterableItemsFactory registerableItemsFactory) {
        this.environment = EnvironmentFactory.newEnvironment();
        this.environmentEntries = new HashMap<String, Object>();
        this.kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        this.registerableItemsFactory = registerableItemsFactory;

    }
    
    public void init() {
        if (this.mapper == null) {
            this.mapper = new InMemoryMapper();
        }
    }
    
    /**
     * Adds given asset to knowledge builder to produce KieBase
     * @param resource asset to be added 
     * @param type type of the asset
     */
    public void addAsset(Resource resource, ResourceType type) {
        /**
         * The code below (CSV/XLS) was added because of timelines related to switchyard/fuse.
         *  
         * However, it is an ugly hack: As soon as is possible, the code below should be removed or refactored. 
         * - an "addAsset(Resource, ResourceType, ResourceConfiguration)" method should be added to this implementation
         * - or the kbuilder code should be refactored so that there are two ResourceTypes: CSV and XLS
         * 
         * (refactoring the kbuilder code is probably a better idea.)
         */
        boolean replaced = false;
        if (resource.getSourcePath() != null ) { 
            String path = resource.getSourcePath();
          
            String typeStr = null;
            if( path.toLowerCase().endsWith(".csv") ) { 
                typeStr = DecisionTableInputType.CSV.toString();
            } else if( path.toLowerCase().endsWith(".xls") ) { 
                typeStr = DecisionTableInputType.XLS.toString();
            } 
           
            if( typeStr != null ) { 
                String worksheetName = null;
                boolean replaceConfig = true;
                ResourceConfiguration config = resource.getConfiguration();
                if( config != null && config instanceof DecisionTableConfiguration ) { 
                    DecisionTableInputType realType = DecisionTableInputType.valueOf(typeStr);
                    if( ((DecisionTableConfiguration) config).getInputType().equals(realType) ) { 
                       replaceConfig = false;
                    } else { 
                        worksheetName = ((DecisionTableConfiguration) config).getWorksheetName();
                    }
                }

                if( replaceConfig ) { 
                    Properties prop = new Properties();
                    prop.setProperty(ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName());
                    prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_TYPE, typeStr);
                    if( worksheetName != null ) { 
                        prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_WORKSHEET, worksheetName);
                    }
                    ResourceConfiguration conf = ResourceTypeImpl.fromProperties(prop);
                    this.kbuilder.add(resource, type, conf);
                    replaced = true;
                }
            } 
        } 
        
        if( ! replaced ) { 
            this.kbuilder.add(resource, type);
        }

        if (this.kbuilder.hasErrors()) {
            StringBuffer errorMessage = new StringBuffer();
            for( KnowledgeBuilderError error : kbuilder.getErrors()) {
                errorMessage.append(error.getMessage() + ",");
            }
            this.kbuilder.undo();
            throw new IllegalArgumentException("Cannot add asset: " + errorMessage.toString());
        }
    }
    
    /**
     * Adds element to the drools/jbpm environment - the value must be thread save as it will be shared between all 
     * <code>RuntimeEngine</code> instances
     * @param name name of the environment entry
     * @param value value of the environment entry
     */
    public void addToEnvironment(String name, Object value) {
        this.environment.set(name, value);
        this.environmentEntries.put(name, value);
    }
    
    /**
     * Adds configuration property that will be part of <code>KieSessionConfiguration</code>
     * @param name name of the property
     * @param value value of the property
     */
    public void addToConfiguration(String name, String value) {
        if (this.sessionConfigProperties == null) {
            this.sessionConfigProperties = new Properties();
        }
        this.sessionConfigProperties.setProperty(name, value);
    }

    @Override
    public KieBase getKieBase() {
        if (this.kbase == null) {
            this.kbase = kbuilder.newKieBase();
        }
        return this.kbase;
    }
    
    public Environment getEnvironmentTemplate() {
    	return this.environment;
    }

    @Override
    public Environment getEnvironment() {
        // this environment is like template always make a new copy when this method is called
        return copyEnvironment();
    }

    @Override
    public KieSessionConfiguration getConfiguration() {
    	KieSessionConfiguration config = null;
    	if (this.sessionConfigProperties != null) {
    		config = KieServices.Factory.get().newKieSessionConfiguration(this.sessionConfigProperties, classLoader);
        } else {
        	config = KieServices.Factory.get().newKieSessionConfiguration(null, classLoader);
        }
    	// add special option to fire activations marked as eager directly
    	config.setOption(ForceEagerActivationOption.YES);
    	
    	return config;
    }
    @Override
    public boolean usePersistence() {
        
        return this.usePersistence;
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
        addIfPresent(EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER, copy);
        addIfPresent(EnvironmentName.TRANSACTION_MANAGER, copy);
        addIfPresent(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, copy);
        addIfPresent(EnvironmentName.TRANSACTION, copy);
        addIfPresent(EnvironmentName.USE_LOCAL_TRANSACTIONS, copy);
        addIfPresent(EnvironmentName.USE_PESSIMISTIC_LOCKING, copy);        
        addIfPresent(EnvironmentName.EXEC_ERROR_MANAGER, copy);
        addIfPresent(EnvironmentName.DEPLOYMENT_ID, copy);
        
        if (usePersistence()) {
            ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) copy.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);        
            
            List<ObjectMarshallingStrategy> listStrategies = new ArrayList<ObjectMarshallingStrategy>(Arrays.asList(strategies));
            listStrategies.add(0, new ProcessInstanceResolverStrategy());
            strategies = new ObjectMarshallingStrategy[listStrategies.size()];  
            copy.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, listStrategies.toArray(strategies));
        }
        // copy if present in environment template which in general should not be used 
        // unless with some framework support to make EM thread safe - like spring 
        addIfPresent(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, copy);
        addIfPresent(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, copy);
        
        
        addIfPresent("IS_JTA_TRANSACTION", copy);
        addIfPresent("IS_TIMER_CMT", copy);
		addIfPresent("IS_SHARED_ENTITY_MANAGER", copy);
		addIfPresent("TRANSACTION_LOCK_ENABLED", copy);
		addIfPresent("IdentityProvider", copy);
		addIfPresent("jbpm.business.calendar", copy);
		
		// handle for custom environment entries that might be required by non engine use cases
		if (!environmentEntries.isEmpty()) {
			for (Entry<String, Object> entry : environmentEntries.entrySet()) {
				// don't override
				if (copy.get(entry.getKey()) != null) {
					continue;
				}
				copy.set(entry.getKey(), entry.getValue());
			}
		}

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
    @Override
    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Properties getSessionConfigProperties() {
        return sessionConfigProperties;
    }
    public void setSessionConfigProperties(Properties sessionConfigProperties) {
        this.sessionConfigProperties = sessionConfigProperties;
    }

    public void setUsePersistence(boolean usePersistence) {
        this.usePersistence = usePersistence;
    }

    public void setKieBase(KieBase kbase) {
        this.kbase = kbase;
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

    public void setRegisterableItemsFactory(RegisterableItemsFactory registerableItemsFactory) {
        this.registerableItemsFactory = registerableItemsFactory;
    }
    
    public EntityManagerFactory getEmf() {
        return emf;
    }
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
