/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.event.listeners;

import org.droolsjbpm.services.api.IdentityProvider;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.impl.helpers.NodeInstanceDescFactory;
import org.droolsjbpm.services.impl.helpers.ProcessInstanceDescFactory;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.jboss.seam.transaction.Transactional;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.ProcessInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author salaboy
 */
@ApplicationScoped // This should be something like DomainScoped
@Transactional
public class CDIProcessEventListener implements ProcessEventListener {
    @Inject
    private EntityManager em; 
    
    @Inject
    private IdentityProvider identity;

    private String domainName;
    
    private SessionManager sessionManager;
    
    public CDIProcessEventListener() {
    }
    
    
    
    
    public void beforeProcessStarted(ProcessStartedEvent pse) {
        //do nothing
        ProcessInstance processInstance = pse.getProcessInstance();
        int sessionId = ((StatefulKnowledgeSession)pse.getKieRuntime()).getId();
        em.persist(ProcessInstanceDescFactory.newProcessInstanceDesc(domainName, sessionId, processInstance, identity.getName()));
    }

    public void afterProcessStarted(ProcessStartedEvent pse) {
        int currentState = pse.getProcessInstance().getState();

        if (currentState == ProcessInstance.STATE_ACTIVE) {
            ProcessInstance processInstance = pse.getProcessInstance();
            int sessionId = ((StatefulKnowledgeSession)pse.getKieRuntime()).getId();
            em.persist(ProcessInstanceDescFactory.newProcessInstanceDesc(domainName, sessionId, processInstance, identity.getName()));
        }
    }

    public void beforeProcessCompleted(ProcessCompletedEvent pce) {
        ProcessInstance processInstance = pce.getProcessInstance();
        int sessionId = ((StatefulKnowledgeSession)pce.getKieRuntime()).getId();
        em.persist(ProcessInstanceDescFactory.newProcessInstanceDesc(domainName, sessionId, processInstance, identity.getName()));
        if(sessionManager != null){
            sessionManager.getProcessInstanceIdKsession().remove(processInstance.getId());
        }
    }

    public void afterProcessCompleted(ProcessCompletedEvent pce) {
        
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        //do nothing
        int sessionId = ((StatefulKnowledgeSession)pnte.getKieRuntime()).getId();
        long processInstanceId = pnte.getProcessInstance().getId();
        NodeInstance nodeInstance = pnte.getNodeInstance();
        em.persist(NodeInstanceDescFactory.newNodeInstanceDesc(domainName, sessionId, processInstanceId, nodeInstance, false));
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent pnle) {
        // do nothing
    }

    public void afterNodeLeft(ProcessNodeLeftEvent pnle) {
        int sessionId = ((StatefulKnowledgeSession)pnle.getKieRuntime()).getId();
        long processInstanceId = pnle.getProcessInstance().getId();
        NodeInstance nodeInstance = pnle.getNodeInstance();
        em.persist(NodeInstanceDescFactory.newNodeInstanceDesc(domainName, sessionId, processInstanceId, nodeInstance, true));
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent pvce) {
        //do nothing
        int sessionId = ((StatefulKnowledgeSession)pvce.getKieRuntime()).getId();
        long processInstanceId = pvce.getProcessInstance().getId();
        String variableId = pvce.getVariableId();
        String variableInstanceId = pvce.getVariableInstanceId();
        String oldValue = (pvce.getOldValue() != null)?pvce.getOldValue().toString():"";
        String newValue = (pvce.getNewValue() != null)?pvce.getNewValue().toString():"";
        em.persist(new VariableStateDesc(variableId, variableInstanceId, oldValue, 
                                        newValue, domainName, sessionId, processInstanceId));
    }

    public void afterVariableChanged(ProcessVariableChangedEvent pvce) {
        
        
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    
    
    
}
