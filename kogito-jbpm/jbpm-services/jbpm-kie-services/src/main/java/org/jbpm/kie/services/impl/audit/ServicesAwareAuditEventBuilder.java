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

package org.jbpm.kie.services.impl.audit;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.event.AuditEvent;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.internal.identity.IdentityProvider;


public class ServicesAwareAuditEventBuilder extends DefaultAuditEventBuilderImpl {

    private IdentityProvider identityProvider;    
    
    private String deploymentUnitId;

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }
    
    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        
        ProcessInstanceLog log = (ProcessInstanceLog) super.buildEvent(pse);
        log.setIdentity(identityProvider.getName());
        log.setExternalId(deploymentUnitId);
        return log;
    }

    @Override
    public AuditEvent buildEvent(ProcessCompletedEvent pce, Object log) {
        ProcessInstanceLog instanceLog = (ProcessInstanceLog) super.buildEvent(pce, log); 
        instanceLog.setExternalId(deploymentUnitId);
        return instanceLog;
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog)super.buildEvent(pnte); 
        nodeInstanceLog.setExternalId(deploymentUnitId);
        return nodeInstanceLog;
        
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog) super.buildEvent(pnle, log); 
        nodeInstanceLog.setExternalId(deploymentUnitId);
        return nodeInstanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessVariableChangedEvent pvce) {
        VariableInstanceLog variableLog = (VariableInstanceLog)super.buildEvent(pvce); 
        variableLog.setExternalId(deploymentUnitId);
        return variableLog;
    }

    public String getDeploymentUnitId() {
        return deploymentUnitId;
    }

    public void setDeploymentUnitId(String deploymentUnitId) {
        this.deploymentUnitId = deploymentUnitId;
    }
    
}
