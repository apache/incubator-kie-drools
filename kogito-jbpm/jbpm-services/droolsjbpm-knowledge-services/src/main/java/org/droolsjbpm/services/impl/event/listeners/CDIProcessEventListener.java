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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.droolsjbpm.services.impl.helpers.NodeInstanceDescFactory;
import org.droolsjbpm.services.impl.helpers.ProcessInstanceDescFactory;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author salaboy
 */
@ApplicationScoped // This should be something like DomainScoped
@Transactional
public class CDIProcessEventListener implements ProcessEventListener {
    @Inject
    private EntityManager em; 

    private String domainName;
    
    public CDIProcessEventListener() {
    }
    
    
    
    public void beforeProcessStarted(ProcessStartedEvent pse) {
        //do nothing
        ProcessInstance processInstance = pse.getProcessInstance();
        int sessionId = ((StatefulKnowledgeSession)pse.getKnowledgeRuntime()).getId();
        em.persist(ProcessInstanceDescFactory.newProcessInstanceDesc(domainName, sessionId, processInstance));
    }

    public void afterProcessStarted(ProcessStartedEvent pse) {
        
    }

    public void beforeProcessCompleted(ProcessCompletedEvent pce) {
        //do nothing
        ProcessInstance processInstance = pce.getProcessInstance();
        int sessionId = ((StatefulKnowledgeSession)pce.getKnowledgeRuntime()).getId();
        em.persist(ProcessInstanceDescFactory.newProcessInstanceDesc(domainName, sessionId, processInstance));
    }

    public void afterProcessCompleted(ProcessCompletedEvent pce) {
        
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        //do nothing
        int sessionId = ((StatefulKnowledgeSession)pnte.getKnowledgeRuntime()).getId();
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
        int sessionId = ((StatefulKnowledgeSession)pnle.getKnowledgeRuntime()).getId();
        long processInstanceId = pnle.getProcessInstance().getId();
        NodeInstance nodeInstance = pnle.getNodeInstance();
        em.persist(NodeInstanceDescFactory.newNodeInstanceDesc(domainName, sessionId, processInstanceId, nodeInstance, true));
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent pvce) {
        //do nothing
        int sessionId = ((StatefulKnowledgeSession)pvce.getKnowledgeRuntime()).getId();
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
    
    
    
}
