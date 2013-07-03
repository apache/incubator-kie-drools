/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.util.CDIHelper;
import org.drools.core.util.StringUtils;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.runtime.manager.api.EventListenerProducer;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.runtime.manager.api.qualifiers.Agenda;
import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.jbpm.runtime.manager.api.qualifiers.WorkingMemory;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.services.task.annotations.External;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;

/**
 * Implementation of <code>RegisterableItemsFactory</code> dedicated to CDI environments that allows to get 
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
 * In addition to that, <code>AbstractAuditLogger</code> can be set after bean has been injected if the default 
 * is not sufficient. Although this factory extends <code>DefaultRegisterableItemsFactory</code> it will not
 * use any of the listeners and handlers that comes from the super class. It mainly relies on CDI injections
 * where the only exception from this rule is <code>AbstractAuditLogger</code>
 * <br/>
 * Even though this is fully qualified bean for injection it provides helper methods to build its instances
 * using <code>BeanManager</code> in case more independent instances are required.
 * <ul>
 *  <li>getFactory(BeanManager, AbstractAuditLogger)</li>
 *  <li>getFactory(BeanManager, AbstractAuditLogger, KieContainer, String)</li>
 * </ul>  
 */
public class InjectableRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

    private static final String DEFAULT_KIE_SESSION = "defaultKieSession";
    private static Logger logger = Logger.getLogger(InjectableRegisterableItemsFactory.class.getName());
    
    @Inject
    @External
    private ExternalTaskEventListener taskListener; 
    // optional injections
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
    private Instance<EventListenerProducer<WorkingMemoryEventListener>> wmListenerProducer;
    
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
        
        if (kieContainer != null) {
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
                throw new IllegalStateException("Cannot find ksession with name " + ksessionName);
            }
            try {

                CDIHelper.wireListnersAndWIHs(ksessionModel, runtime.getKieSession(), parameters);
            } catch (Exception e) {
                // use fallback mechanism
                CDIHelper.wireListnersAndWIHs(ksessionModel, runtime.getKieSession());
            }
        }
        try {
            for (WorkItemHandlerProducer producer : workItemHandlerProducer) {
                handler.putAll(producer.getWorkItemHandlers(manager.getIdentifier(), parameters));
            }
        } catch (Exception e) {
            // do nothing as work item handler is considered optional
            logger.warning("Exception while evalutating work item handler prodcuers " + e.getMessage());
        }
        
        return handler;
    }
    
    protected WorkItemHandler getHTWorkItemHandler(RuntimeEngine runtime) {
        
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        taskListener.addMappedManger(manager.getIdentifier(), manager);
        
        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.setRuntimeManager(manager);

        return humanTaskHandler;
    }  
    

    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        if(auditlogger != null) {
            defaultListeners.add(auditlogger);
        }
        try {
            for (EventListenerProducer<ProcessEventListener> producer : processListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warning("Exception while evaluating ProcessEventListener producers" + e.getMessage());
        }
        return defaultListeners;
    }
    
    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(RuntimeEngine runtime) {
        List<WorkingMemoryEventListener> defaultListeners = new ArrayList<WorkingMemoryEventListener>();
        try {
            for (EventListenerProducer<WorkingMemoryEventListener> producer : wmListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warning("Exception while evaluating WorkingMemoryEventListener producers" + e.getMessage());
        }
        
        return defaultListeners;
    }      

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(
            RuntimeEngine runtime) {
        List<AgendaEventListener> defaultListeners = new ArrayList<AgendaEventListener>();
        try {
            for (EventListenerProducer<AgendaEventListener> producer : agendaListenerProducer) {
                defaultListeners.addAll(producer.getEventListeners(((RuntimeEngineImpl)runtime).getManager().getIdentifier(), getParametersMap(runtime)));
            }
        } catch (Exception e) {
            logger.warning("Exception while evaluating WorkingMemoryEventListener producers" + e.getMessage());
        }
        
        return defaultListeners;
    }
    
    /**
     * Allows to create instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
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
     * Allows to create instance of this class dynamically via <code>BeanManager</code>. This is useful in case multiple 
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

    protected Map<String, Object> getParametersMap(RuntimeEngine runtime) {
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        parameters.put("taskService", runtime.getKieSession());
        parameters.put("runtimeManager", manager);
        
        return parameters;
    }

    
}
