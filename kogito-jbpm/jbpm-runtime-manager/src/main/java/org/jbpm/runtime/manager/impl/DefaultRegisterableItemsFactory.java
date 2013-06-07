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
package org.jbpm.runtime.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.task.api.EventService;

/**
 * Default implementation of <code>RegisterableItemsFactory</code> responsible for providing 
 * common set of WorkItemHandlers and EventListeners. This factory should not be used in CDI container.
 * <br/>
 * It will deliver fully configured instances of:
 * <ul>
 *  <li>WorkItemHandler for "Human Task" that is configured with local task service</li>
 *  <li>JPA audit logger - for history log</li>
 *  <li>event listener to trigger rules automatically without a need of invoking fireAllRules</li>
 * </ul>
 * Moreover it will invoke its super methods to get rest of registerable items defined, that might override defaults
 * as they are added to resulting map at the end.
 * 
 * @see InjectableRegisterableItemsFactory
 */
public class DefaultRegisterableItemsFactory extends SimpleRegisterableItemsFactory {

    private AuditEventBuilder auditBuilder = new DefaultAuditEventBuilderImpl();
    
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> defaultHandlers = new HashMap<String, WorkItemHandler>();
        //HT handler 
        WorkItemHandler handler = getHTWorkItemHandler(runtime);
        defaultHandlers.put("Human Task", handler);
        // add any custom registered
        defaultHandlers.putAll(super.getWorkItemHandlers(runtime));
        
        return defaultHandlers;
    }


    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        // register JPAWorkingMemoryDBLogger
        AbstractAuditLogger logger = AuditLoggerFactory.newJPAInstance((EntityManagerFactory) 
                runtime.getKieSession().getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        logger.setBuilder(getAuditBuilder());
        defaultListeners.add(logger);
        // add any custom listeners
        defaultListeners.addAll(super.getProcessEventListeners(runtime));
        return defaultListeners;
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        List<AgendaEventListener> defaultListeners = new ArrayList<AgendaEventListener>();
        defaultListeners.add(new TriggerRulesEventListener(runtime.getKieSession()));
        // add any custom listeners
        defaultListeners.addAll(super.getAgendaEventListeners(runtime));
        return defaultListeners;
    }

    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(RuntimeEngine runtime) {
        List<WorkingMemoryEventListener> defaultListeners = new ArrayList<WorkingMemoryEventListener>();
        
        // add any custom listeners
        defaultListeners.addAll(super.getWorkingMemoryEventListeners(runtime));
        return defaultListeners;
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected WorkItemHandler getHTWorkItemHandler(RuntimeEngine runtime) {
        
        ExternalTaskEventListener listener = new ExternalTaskEventListener();
        listener.setRuntimeManager(((RuntimeEngineImpl)runtime).getManager());
        
        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.setRuntimeManager(((RuntimeEngineImpl)runtime).getManager());
        if (runtime.getTaskService() instanceof EventService) {
            ((EventService)runtime.getTaskService()).registerTaskLifecycleEventListener(listener);
        }
        
        if (runtime instanceof Disposable) {
            ((Disposable)runtime).addDisposeListener(new DisposeListener() {
                
                @Override
                public void onDispose(RuntimeEngine runtime) {
                    if (runtime.getTaskService() instanceof EventService) {
                        ((EventService)runtime.getTaskService()).clearTaskLifecycleEventListeners();
                        ((EventService)runtime.getTaskService()).clearTasknotificationEventListeners();
                    }
                }
            });
        }
        return humanTaskHandler;
    }


    public AuditEventBuilder getAuditBuilder() {
        return auditBuilder;
    }


    public void setAuditBuilder(AuditEventBuilder auditBuilder) {
        this.auditBuilder = auditBuilder;
    }    
}
