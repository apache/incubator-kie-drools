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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.Closeable;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.kie.internal.runtime.conf.ObjectModelResolverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the <code>RegisterableItemsFactory</code> responsible for providing 
 * a common set of WorkItemHandlers and EventListeners. This factory should not be used in CDI container.
 * <br/>
 * It will deliver fully configured instances of the following:
 * <ul>
 *  <li>a WorkItemHandler for "Human Task" that is configured with local task service</li>
 *  <li>a JPA audit logger - for history logging</li>
 *  <li>a event listener to trigger rules automatically without a need of invoking fireAllRules</li>
 * </ul>
 * Moreover, it will invoke its super methods to define the rest of the registerable items, that might override defaults
 * when they are added to the resulting map at the end.
 * 
 * @see InjectableRegisterableItemsFactory
 */
public class DefaultRegisterableItemsFactory extends SimpleRegisterableItemsFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRegisterableItemsFactory.class);

    private AuditEventBuilder auditBuilder = new ManagedAuditEventBuilderImpl();
    private AbstractAuditLogger jmsLogger = null;
    
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> defaultHandlers = new HashMap<String, WorkItemHandler>();
        //HT handler 
        WorkItemHandler handler = getHTWorkItemHandler(runtime);
        if (handler != null) {
            defaultHandlers.put("Human Task", handler);
        }
        // add any custom registered
        defaultHandlers.putAll(super.getWorkItemHandlers(runtime));
        // add handlers from descriptor
        defaultHandlers.putAll(getWorkItemHandlersFromDescriptor(runtime));
        
        return defaultHandlers;
    }


    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {    	
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
        if (descriptor == null) {
        	// register JPAWorkingMemoryDBLogger
	        AbstractAuditLogger logger = AuditLoggerFactory.newJPAInstance(runtime.getKieSession().getEnvironment());
	        logger.setBuilder(getAuditBuilder(runtime));
	        defaultListeners.add(logger);
        } else if (descriptor.getAuditMode() == AuditMode.JPA) {
        	// register JPAWorkingMemoryDBLogger
        	AbstractAuditLogger logger = null;
        	if (descriptor.getPersistenceUnit().equals(descriptor.getAuditPersistenceUnit())) {
        		logger = AuditLoggerFactory.newJPAInstance(runtime.getKieSession().getEnvironment());
        	} else {
        		Environment env = EnvironmentFactory.newEnvironment();
        		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit()));
        		logger = AuditLoggerFactory.newJPAInstance(env);
        	}
	        
	        logger.setBuilder(getAuditBuilder(runtime));
	        defaultListeners.add(logger);
        } else if (descriptor.getAuditMode() == AuditMode.JMS) {
            try {
                if (jmsLogger == null) {
                    Properties properties = new Properties();
                    InputStream input = getRuntimeManager().getEnvironment().getClassLoader().getResourceAsStream("/jbpm.audit.jms.properties");
                    // required for junit test
                    if (input == null) {
                        input = getRuntimeManager().getEnvironment().getClassLoader().getResourceAsStream("jbpm.audit.jms.properties");
                    }
                    properties.load(input);
                    logger.debug("Creating AsyncAuditLogProducer {}", properties);

                    jmsLogger = AuditLoggerFactory.newJMSInstance((Map) properties);
                    jmsLogger.setBuilder(getAuditBuilder(runtime));
                }
                defaultListeners.add(jmsLogger);
            } catch (IOException e) {
                logger.error("Unable to load jms audit properties from {}", "/jbpm.audit.jms.properties", e);
            }
        }
        // add any custom listeners
        defaultListeners.addAll(super.getProcessEventListeners(runtime));
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, ProcessEventListener.class));        
        return defaultListeners;
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        List<AgendaEventListener> defaultListeners = new ArrayList<AgendaEventListener>();
        // add any custom listeners
        defaultListeners.addAll(super.getAgendaEventListeners(runtime));
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, AgendaEventListener.class));        
        return defaultListeners;
    }

    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime) {
        List<RuleRuntimeEventListener> defaultListeners = new ArrayList<RuleRuntimeEventListener>();
        
        // add any custom listeners
        defaultListeners.addAll(super.getRuleRuntimeEventListeners(runtime));
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, RuleRuntimeEventListener.class));
        return defaultListeners;
    }


    @Override
	public List<TaskLifeCycleEventListener> getTaskListeners() {
    	List<TaskLifeCycleEventListener> defaultListeners = new ArrayList<TaskLifeCycleEventListener>();
        defaultListeners.add(new JPATaskLifeCycleEventListener(true));
        // add any custom listeners
        defaultListeners.addAll(super.getTaskListeners());
        // add listeners from deployment descriptor
        defaultListeners.addAll(getTaskListenersFromDescriptor());
        
        return defaultListeners;
	}


	@Override
	public Map<String, Object> getGlobals(RuntimeEngine runtime) {
		Map<String, Object> defaultGlobals = new HashMap<String, Object>();
				
		defaultGlobals.putAll(super.getGlobals(runtime));
		// add globals from descriptor
		defaultGlobals.putAll(getGlobalsFromDescriptor(runtime));
		return defaultGlobals;
	}


	
    protected WorkItemHandler getHTWorkItemHandler(RuntimeEngine runtime) {

        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.setRuntimeManager(((RuntimeEngineImpl)runtime).getManager());
                
        return humanTaskHandler;
    }


    public AuditEventBuilder getAuditBuilder() {
        return auditBuilder;
    }

    public AuditEventBuilder getAuditBuilder(RuntimeEngine engine) {
    	if (this.auditBuilder != null && this.auditBuilder instanceof ManagedAuditEventBuilderImpl) {
    		String identifier = ((RuntimeEngineImpl)engine).getManager().getIdentifier();
    		((ManagedAuditEventBuilderImpl) this.auditBuilder).setOwnerId(identifier);
    	}
    	
    	return this.auditBuilder;
    }

    public void setAuditBuilder(AuditEventBuilder auditBuilder) {
        this.auditBuilder = auditBuilder;
    }    
    
    protected Object getInstanceFromModel(ObjectModel model, ClassLoader classloader, Map<String, Object> contaxtParams) {
    	ObjectModelResolver resolver = ObjectModelResolverProvider.get(model.getResolver());
		if (resolver == null) {
		    throw new IllegalStateException("Unable to find ObjectModelResolver for " + model.getResolver());
		}
		
		return resolver.getInstance(model, classloader, contaxtParams);
    }    
    
    protected Map<String, Object> getParametersMap(RuntimeEngine runtime) {
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        
        try {
            parameters.put("taskService", runtime.getTaskService());
        } catch (UnsupportedOperationException e) {
            // in case task service was not configured
        }
        parameters.put("runtimeManager", manager);
        parameters.put("classLoader", getRuntimeManager().getEnvironment().getClassLoader());
        parameters.put("entityManagerFactory", 
        		runtime.getKieSession().getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        parameters.put("kieContainer", getRuntimeManager().getKieContainer());
        
        return parameters;
    }
    
    protected List<TaskLifeCycleEventListener> getTaskListenersFromDescriptor() {
    	List<TaskLifeCycleEventListener> defaultListeners = new ArrayList<TaskLifeCycleEventListener>();
        DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
        if (descriptor != null) {
        	Map<String, Object> params = new HashMap<String, Object>();
        	params.put("runtimeManager", getRuntimeManager());
        	params.put("classLoader", getRuntimeManager().getEnvironment().getClassLoader());
        	params.put("kieContainer", getRuntimeManager().getKieContainer());
        	for (ObjectModel model : descriptor.getTaskEventListeners()) {
        		Object taskListener = getInstanceFromModel(model, getRuntimeManager().getEnvironment().getClassLoader(), params);
        		if (taskListener != null) {
        			defaultListeners.add((TaskLifeCycleEventListener) taskListener);
        		}
        	}
        }
        
        return defaultListeners;
    }
    
    protected Map<String, WorkItemHandler> getWorkItemHandlersFromDescriptor(RuntimeEngine runtime) {
    	Map<String, WorkItemHandler> defaultHandlers = new HashMap<String, WorkItemHandler>();
        DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
        if (descriptor != null) {
        	Map<String, Object> params = getParametersMap(runtime);
        	for (NamedObjectModel model : descriptor.getWorkItemHandlers()) {
        		Object hInstance = getInstanceFromModel(model, getRuntimeManager().getEnvironment().getClassLoader(), params);
        		if (hInstance != null) {
        			defaultHandlers.put(model.getName(), (WorkItemHandler) hInstance);
        		}
        	}
        }
        
        return defaultHandlers;
    }
    
    @SuppressWarnings("unchecked")
	protected <T> List<T>  getEventListenerFromDescriptor(RuntimeEngine runtime, Class<T> type) {
    	List<T> listeners = new ArrayList<T>();
        DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
        if (descriptor != null) {
        	Map<String, Object> params = getParametersMap(runtime);
        	for (ObjectModel model : descriptor.getEventListeners()) {
        		Object listenerInstance = getInstanceFromModel(model, getRuntimeManager().getEnvironment().getClassLoader(), params);
        		if (listenerInstance != null && type.isAssignableFrom(listenerInstance.getClass())) {
        			listeners.add((T) listenerInstance);
        		} else {
        		    // close/cleanup instance as it is not going to be used at the moment, except these that are cacheable
        		    if (listenerInstance instanceof Closeable && !(listenerInstance instanceof Cacheable)) {
        		        ((Closeable) listenerInstance).close();
        		    }
        		}
        	}
        }
        
        return listeners;
    }
    
    protected Map<String, Object> getGlobalsFromDescriptor(RuntimeEngine runtime) {
    	Map<String, Object> globals = new HashMap<String, Object>();
        DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
        if (descriptor != null) {
        	Map<String, Object> params = getParametersMap(runtime);
        	for (NamedObjectModel model : descriptor.getGlobals()) {
        		Object gInstance = getInstanceFromModel(model, getRuntimeManager().getEnvironment().getClassLoader(), params);
        		if (gInstance != null) {
        			globals.put(model.getName(), gInstance);
        		}
        	}
        }
        
        return globals;
    }
}
