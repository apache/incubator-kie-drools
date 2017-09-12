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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.util.InjectionHelper;
import org.drools.core.util.StringUtils;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;

/**
 * This implementation extends the DefaultRegisterableItemsFactory
 * and relies on definitions of work item handlers and
 * listeners that come from kmodule.xml from kjar. 
 * It will directly register all listeners and work item handlers on the ksession
 * and will also return listeners and handlers provided by the default implementation.
 *
 */
public class KModuleRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

	private static final String DEFAULT_KIE_SESSION = "defaultKieSession";
	
    private KieContainer kieContainer;
    private String ksessionName;
    
    
    public KModuleRegisterableItemsFactory(KieContainer kieContainer,
            String ksessionName) {
        super();
        this.kieContainer = kieContainer;
        this.ksessionName = ksessionName;
    }
    
    public KModuleRegisterableItemsFactory(KieContainer kieContainer,
            String ksessionName, AuditEventBuilder auditBuilder) {
        super();
        this.kieContainer = kieContainer;
        this.ksessionName = ksessionName;
        setAuditBuilder(auditBuilder);
    }

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        KieSessionModel ksessionModel = null;
        if(StringUtils.isEmpty(ksessionName)) {
            ksessionModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieSession();
            if (ksessionModel == null) {
            	ksessionName = DEFAULT_KIE_SESSION;
            	ksessionModel = ((KieContainerImpl)kieContainer).getKieSessionModel(ksessionName);
            }
        } else {            
            ksessionModel = ((KieContainerImpl)kieContainer).getKieSessionModel(ksessionName);
        }
        
        if (ksessionModel == null) {
            throw new IllegalStateException("Cannot find ksession, either it does not exist or there are multiple default ksession in kmodule.xml");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        parameters.put("taskService", runtime.getTaskService());
        parameters.put("runtimeManager", ((RuntimeEngineImpl)runtime).getManager());
        if (getRuntimeManager().getKieContainer() != null) {
        	parameters.put("kieContainer", getRuntimeManager().getKieContainer());
        }
        parameters.put("classLoader", getRuntimeManager().getEnvironment().getClassLoader());
        try {

            InjectionHelper.wireSessionComponents(ksessionModel, runtime.getKieSession(), parameters);
        } catch (Exception e) {
        	e.printStackTrace();
            // use fallback mechanism
            InjectionHelper.wireSessionComponents(ksessionModel, runtime.getKieSession());
        }
        
        return super.getWorkItemHandlers(runtime);
    }

    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        return super.getProcessEventListeners(runtime);
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        return super.getAgendaEventListeners(runtime);
    }

    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(
            RuntimeEngine runtime) {
        return super.getRuleRuntimeEventListeners(runtime);
    }

}
