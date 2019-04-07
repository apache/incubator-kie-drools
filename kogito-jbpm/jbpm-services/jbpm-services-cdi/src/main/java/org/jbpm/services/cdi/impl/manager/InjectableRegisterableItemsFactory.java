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
package org.jbpm.services.cdi.impl.manager;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.util.InjectionHelper;
import org.drools.core.util.StringUtils;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.api.qualifiers.Agenda;
import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.jbpm.runtime.manager.api.qualifiers.Task;
import org.jbpm.runtime.manager.api.qualifiers.WorkingMemory;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.EventListenerProducer;
import org.kie.internal.runtime.manager.GlobalProducer;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of <code>RegisterableItemsFactory</code> dedicated to CDI environments that allows us to get 
 * injections of following components:
 * <ul>
 *  <li><code>ExternalTaskEventListener</code> - required bean</li>
 *  <li><code>WorkItemHandlerProducer</code> - optional bean (0 or more)</li>
 *  <li><code>EventListenerProducer<ProcessEventListener>></code> - optional bean (0 or more)</li>
 *  <li><code>EventListenerProducer<AgendaEventListener>></code> - optional bean (0 or more)</li>
 *  <li><code>EventListenerProducer<WorkingMemoryEventListener>></code> - optional bean (0 or more)</li>
 *  <li><code>RuntimeFinder</code> - optional required only when single CDI bean is going to manage many 
 *  <code>RuntimeManager</code> instances</li>
 * </ul>
 * In addition to that, <code>AbstractAuditLogger</code> can be set after the bean has been injected if the default 
 * is not sufficient. Although this factory extends <code>DefaultRegisterableItemsFactory</code>, it will not
 * use any of the listeners and handlers that come from the super class. It relies mainly on CDI injections
 * where the only exception from this rule is <code>AbstractAuditLogger</code>
 * <br/>
 * Even though this is a fully qualified bean for injection, it provides helper methods to build its instances
 * using <code>BeanManager</code> in case more independent instances are required.
 * <ul>
 *  <li>getFactory(BeanManager, AbstractAuditLogger)</li>
 *  <li>getFactory(BeanManager, AbstractAuditLogger, KieContainer, String)</li>
 * </ul>  
 */
public class InjectableRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

    private static final String DEFAULT_KIE_SESSION = "defaultKieSession";
    private static final Logger logger = LoggerFactory.getLogger(InjectableRegisterableItemsFactory.class);
    
    // optional injections
    @Inject
    @Any
    private Instance<GlobalProducer> globalProducer; 
    @Inject
    @Any
    private Instance<WorkItemHandlerProducer> workItemHandlerProducer;  
    @Inject
    @Process
    private Instance<EventListenerProducer<ProcessEventListener>> processListenerProducer;
    @Inject
    @Agenda
    private Instance<EventListenerProducer<AgendaEventListener>> agendaListenerProducer;
    @Inject
    @WorkingMemory
    private Instance<EventListenerProducer<RuleRuntimeEventListener>> wmListenerProducer;
    @Inject
    @Task
    private Instance<EventListenerProducer<TaskLifeCycleEventListener>> taskListenerProducer;
    @Inject
    private Instance<ExecutorService> executorService;
    
    private AbstractAuditLogger auditlogger;
    
    // to handle kmodule approach
    private KieContainer kieContainer;
    private String ksessionName;
    

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> handler = new HashMap<String, WorkItemHandler>();
        handler.put("Human Task", getHTWorkItemHandler(runtime));
        
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        parameters.put("taskService", runtime.getTaskService());
        parameters.put("runtimeManager", manager);
        parameters.put("kieContainer", getRuntimeManager().getKieContainer());
        try {
            parameters.put("executorService", executorService.get());
        } catch (Exception e) {
            logger.debug("Executor service not available due to {}", e.getMessage());
        }
        
        if (kieContainer != null) {
        	// add classloader as one of the parameters so it can be easily referenced
        	parameters.put("classLoader", kieContainer.getClassLoader());
            KieSessionModel ksessionModel = null;
            if(StringUtils.isEmpty(ksessionName)) {
                ksessionModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieSession();
                if (ksessionModel == null) {
                    ksessionModel = ((KieContainerImpl)kieContainer).getKieSessionModel(DEFAULT_KIE_SESSION);
                }
            } else {            
                ksessionModel = ((KieContainerImpl)kieContainer).getKieSessionModel(ksessionName);
            }
            
            if (ksessionModel == null) {
                throw new IllegalStateException("Cannot find ksession, either it does not exist or there are multiple default ksession in kmodule.xml");
            }
            try {

                InjectionHelper.wireSessionComponents(ksessionModel, runtime.getKieSession(), parameters);
            } catch (Throwable e) {
                // use fallback mechanism
                InjectionHelper.wireSessionComponents(ksessionModel, runtime.getKieSession());
            }
        }
        try {
            for (WorkItemHandlerProducer producer : workItemHandlerProducer) {
                handler.putAll(producer.getWorkItemHandlers(manager.getIdentifier(), parameters));
            }
        } catch (Exception e) {
            // do nothing as work item handler is considered optional
            logger.warn("Exception while evalutating work item handler prodcuers {}", e.getMessage());
        }
        // add handlers from descriptor
        handler.putAll(getWorkItemHandlersFromDescriptor(runtime));
        return handler;
    }
    

    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        if(auditlogger != null) {
            defaultListeners.add(auditlogger);
        } else if (getAuditBuilder() != null) {
        	AbstractAuditLogger aLogger = getAuditLoggerInstance(runtime);
        	if (aLogger != null) {
        		defaultListeners.add(aLogger);
        	}
        }
        try {
            for (EventListenerProducer<ProcessEventListener> producer : processListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warn("Exception while evaluating ProcessEventListener producers {}", e.getMessage());
        }
        
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, ProcessEventListener.class)); 
        return defaultListeners;
    }
    
    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime) {
        List<RuleRuntimeEventListener> defaultListeners = new ArrayList<RuleRuntimeEventListener>();
        try {
            for (EventListenerProducer<RuleRuntimeEventListener> producer : wmListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warn("Exception while evaluating WorkingMemoryEventListener producers {}", e.getMessage());
        }
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, RuleRuntimeEventListener.class));
        return defaultListeners;
    }      

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        List<AgendaEventListener> defaultListeners = new ArrayList<AgendaEventListener>();
        try {
            for (EventListenerProducer<AgendaEventListener> producer : agendaListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warn("Exception while evaluating WorkingMemoryEventListener producers {}", e.getMessage());
        }
        // add listeners from descriptor
        defaultListeners.addAll(getEventListenerFromDescriptor(runtime, AgendaEventListener.class)); 
        return defaultListeners;
    }   
    
    
    @Override
    public List<TaskLifeCycleEventListener> getTaskListeners() {
        List<TaskLifeCycleEventListener> defaultListeners = new ArrayList<TaskLifeCycleEventListener>();
        try {
            for ( EventListenerProducer<TaskLifeCycleEventListener> producer : taskListenerProducer ) {
            	defaultListeners.addAll( producer.getEventListeners(null, null) );
            }
        } catch ( Exception e ) {
            logger.warn( "Cannot add listeners to task service due to {}", e.getMessage() );
        }
        // add listeners from descriptor
        defaultListeners.addAll(getTaskListenersFromDescriptor());
        return defaultListeners;
    }  
    
    @Override
	public Map<String, Object> getGlobals(RuntimeEngine runtime) {    	
    	Map<String, Object> globals = new HashMap<String, Object>();
        
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        parameters.put("taskService", runtime.getTaskService());
        parameters.put("runtimeManager", manager);
        parameters.put("kieContainer", getRuntimeManager().getKieContainer());
        try {
            parameters.put("executorService", executorService.get());
        } catch (Exception e) {
            logger.debug("Executor service not available due to {}", e.getMessage());
        }
        
        try {
            for (GlobalProducer producer : globalProducer) {
                globals.putAll(producer.getGlobals(manager.getIdentifier(), parameters));
            }
        } catch (Exception e) {
            // do nothing as work item handler is considered optional
            logger.warn("Exception while evalutating globals prodcuers {}", e.getMessage());
        }
        
	    // add globals from descriptor
	    globals.putAll(getGlobalsFromDescriptor(runtime));
        
        return globals;
	}


	/**
     * Allows us to create an instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
     * independent instances are required on runtime and that need cannot be satisfied with regular CDI practices.
     * @param beanManager - bean manager instance of the container
     * @param auditlogger - <code>AbstractAuditLogger</code> logger instance to be used, might be null
     * @return new instance of the factory
     */
    public static RegisterableItemsFactory getFactory(BeanManager beanManager, AbstractAuditLogger auditlogger) {
        InjectableRegisterableItemsFactory instance = getInstanceByType(beanManager, InjectableRegisterableItemsFactory.class, new Annotation[]{});
        instance.setAuditlogger(auditlogger);
        return instance;
    }
    
    /**
     * Allows us to create instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
     * independent instances are required on runtime and that need cannot be satisfied with regular CDI practices.
     * @param beanManager - bean manager instance of the container
     * @param auditlogger - <code>AbstractAuditLogger</code> logger instance to be used, might be null
     * @param kieContainer - <code>KieContainer</code> that the factory is built for
     * @param ksessionName - name of the ksession defined in kmodule to be used, 
     * if not given default ksession from kmodule will be used.
     * @return
     */
    public static RegisterableItemsFactory getFactory(BeanManager beanManager, AbstractAuditLogger auditlogger, KieContainer kieContainer, String ksessionName) {
        InjectableRegisterableItemsFactory instance = getInstanceByType(beanManager, InjectableRegisterableItemsFactory.class, new Annotation[]{});
        instance.setAuditlogger(auditlogger);
        instance.setKieContainer(kieContainer);
        instance.setKsessionName(ksessionName);
        return instance;
    }
    
	/**
     * Allows to create instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
     * independent instances are required on runtime and that need cannot be satisfied with regular CDI practices.
     * @param beanManager - bean manager instance of the container
     * @param eventBuilder - <code>AuditEventBuilder</code> logger builder instance to be used, might be null
     * @return new instance of the factory
     */
    public static RegisterableItemsFactory getFactory(BeanManager beanManager, AuditEventBuilder eventBuilder) {
        InjectableRegisterableItemsFactory instance = getInstanceByType(beanManager, InjectableRegisterableItemsFactory.class, new Annotation[]{});
        instance.setAuditBuilder(eventBuilder);
        return instance;
    }
    
    /**
     * Allows to create instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
     * independent instances are required on runtime and that need cannot be satisfied with regular CDI practices.
     * @param beanManager - bean manager instance of the container
     * @param eventBuilder - <code>AbstractAuditLogger</code> logger builder instance to be used, might be null
     * @param kieContainer - <code>KieContainer</code> that the factory is built for
     * @param ksessionName - name of the ksession defined in kmodule to be used, 
     * if not given default ksession from kmodule will be used.
     * @return
     */
    public static RegisterableItemsFactory getFactory(BeanManager beanManager, AuditEventBuilder eventBuilder, KieContainer kieContainer, String ksessionName) {
        InjectableRegisterableItemsFactory instance = getInstanceByType(beanManager, InjectableRegisterableItemsFactory.class, new Annotation[]{});
        instance.setAuditBuilder(eventBuilder);
        instance.setKieContainer(kieContainer);
        instance.setKsessionName(ksessionName);
        return instance;
    }
    
    
    protected static <T> T getInstanceByType(BeanManager manager, Class<T> type, Annotation... bindings) {
        final Bean<?> bean = manager.resolve(manager.getBeans(type, bindings));
        if (bean == null) {
            throw new UnsatisfiedResolutionException("Unable to resolve a bean for " + type + " with bindings " + Arrays.asList(bindings));
        }
        CreationalContext<?> cc = manager.createCreationalContext(null);
        return type.cast(manager.getReference(bean, type, cc));
    }

    public AbstractAuditLogger getAuditlogger() {
        return auditlogger;
    }

    public void setAuditlogger(AbstractAuditLogger auditlogger) {
        this.auditlogger = auditlogger;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }

    /**
     * Provides  AuditLogger implementation, JPA or JMS.
     * JPA is the default one and JMS requires to have configuration file (.properties)
     * to be available on classpath under 'jbpm.audit.jms.properties' name.
     * This file must have following properties defined:
     * <ul>
     *  <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     *  <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul> 
     * @return instance of the audit logger
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected AbstractAuditLogger getAuditLoggerInstance(RuntimeEngine engine) {
    	DeploymentDescriptor descriptor = getRuntimeManager().getDeploymentDescriptor();
    	AbstractAuditLogger auditLogger = null;
        if ("true".equals(System.getProperty("jbpm.audit.jms.enabled")) || descriptor.getAuditMode() == AuditMode.JMS) {
            try {
                Properties properties = new Properties();
                properties.load(getRuntimeManager().getEnvironment().getClassLoader().getResourceAsStream("/jbpm.audit.jms.properties"));
                
                auditLogger =  AuditLoggerFactory.newJMSInstance((Map)properties);
            } catch (IOException e) {
                logger.error("Unable to load jms audit properties from {}", "/jbpm.audit.jms.properties", e);
            }
            auditLogger.setBuilder(getAuditBuilder(engine));
        } else if (descriptor.getAuditMode() == AuditMode.JPA){        
        	if (descriptor.getPersistenceUnit().equals(descriptor.getAuditPersistenceUnit())) {
        		auditLogger = AuditLoggerFactory.newJPAInstance(engine.getKieSession().getEnvironment());
        	} else {
        		auditLogger = new JPAWorkingMemoryDbLogger(EntityManagerFactoryManager.get().getOrCreate(descriptor.getAuditPersistenceUnit()));
        	}
        	auditLogger.setBuilder(getAuditBuilder(engine));
        }        
        
        return auditLogger;
    }
    
}
