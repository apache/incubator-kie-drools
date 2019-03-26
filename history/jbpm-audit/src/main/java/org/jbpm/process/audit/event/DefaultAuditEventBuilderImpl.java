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

package org.jbpm.process.audit.event;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationKey;

public class DefaultAuditEventBuilderImpl implements AuditEventBuilder {

    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pse.getProcessInstance();
        ProcessInstanceLog log = new ProcessInstanceLog(pi.getId(), pi.getProcessId());
        log.setExternalId(""+((KieSession) pse.getKieRuntime()).getIdentifier());
        log.setProcessName(pi.getProcess().getName());
        log.setProcessVersion(pi.getProcess().getVersion());
        log.setStatus(ProcessInstance.STATE_ACTIVE);
        log.setProcessInstanceDescription( pi.getDescription() );
        log.setProcessType(((WorkflowProcess)pi.getProcess()).getProcessType());
        log.setSlaCompliance(pi.getSlaCompliance());
        log.setSlaDueDate(pi.getSlaDueDate());
        // store correlation key in its external form
        CorrelationKey correlationKey = (CorrelationKey) pi.getMetaData().get("CorrelationKey");
        if (correlationKey != null) {
        	log.setCorrelationKey(correlationKey.toExternalForm());
        }
        
        long parentProcessInstanceId = (Long) pi.getMetaData().getOrDefault("ParentProcessInstanceId", -1L);
	log.setParentProcessInstanceId( parentProcessInstanceId );	     
        

        return log;
    }

    @Override
    public AuditEvent buildEvent(ProcessCompletedEvent pce, Object log) {
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pce.getProcessInstance();
        ProcessInstanceLog logEvent = null;
        if (log != null) {
            logEvent = (ProcessInstanceLog) log;
        } else {
            logEvent = new ProcessInstanceLog(pi.getId(), pi.getProcessId());
        }
        logEvent.setOutcome(pi.getOutcome());
        logEvent.setStatus(pi.getState());
        logEvent.setEnd(pce.getEventDate());
        logEvent.setDuration(logEvent.getEnd().getTime() - logEvent.getStart().getTime());
        logEvent.setProcessInstanceDescription( pi.getDescription() );
        logEvent.setSlaCompliance(pi.getSlaCompliance());
        return logEvent;
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pnte.getProcessInstance();
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) pnte.getNodeInstance();
        Node node = nodeInstance.getNode();
        String nodeId = null;
        String nodeType = null;
        String nodeContainerId = null;
        if (node != null) {
            nodeId = (String)node.getMetaData().get("UniqueId");
            nodeType = node.getClass().getSimpleName();
            nodeContainerId = getNodeContainerId(node.getNodeContainer());
        } else {
            nodeId = Long.toString(nodeInstance.getNodeId());
            nodeType = (String)nodeInstance.getMetaData("NodeType");
        }
        NodeInstanceLog log = new NodeInstanceLog(
                NodeInstanceLog.TYPE_ENTER, pi.getId(), pi.getProcessId(), Long.toString(nodeInstance.getId()), 
                nodeId, nodeInstance.getNodeName());
        if (nodeInstance instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) nodeInstance).getWorkItem() != null) {
            log.setWorkItemId(((WorkItemNodeInstance) nodeInstance).getWorkItem().getId());
        }
        if (nodeInstance instanceof SubProcessNodeInstance) {
            log.setReferenceId(((SubProcessNodeInstance) nodeInstance).getProcessInstanceId());
        }
        String connection = (String)nodeInstance.getMetaData().get("IncomingConnection");
        log.setConnection(connection);
        log.setExternalId(""+((KieSession) pnte.getKieRuntime()).getIdentifier());
        log.setNodeType(nodeType);
        log.setNodeContainerId(nodeContainerId);
        log.setDate(pnte.getEventDate());
        log.setSlaCompliance(nodeInstance.getSlaCompliance());
        log.setSlaDueDate(nodeInstance.getSlaDueDate());
        return log;
    }
        
    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte, Object log) {
    	NodeInstanceImpl nodeInstance = (NodeInstanceImpl) pnte.getNodeInstance();
    
    	NodeInstanceLog logEvent =null;
        if (log != null) {
            logEvent = (NodeInstanceLog) log;
        
	        if (nodeInstance instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) nodeInstance).getWorkItem() != null) {
	        	logEvent.setWorkItemId(((WorkItemNodeInstance) nodeInstance).getWorkItem().getId());
	        }
	        if (nodeInstance instanceof SubProcessNodeInstance) {
	            logEvent.setReferenceId(((SubProcessNodeInstance) nodeInstance).getProcessInstanceId());
	        }
	
	        return logEvent;
        }
        
        return null;
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log) {
        
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pnle.getProcessInstance();
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) pnle.getNodeInstance();
        Node node = nodeInstance.getNode();
        String nodeId = null;
        String nodeType = null;
        String nodeContainerId = null;
        if (node != null) {
            nodeId = (String)node.getMetaData().get("UniqueId");
            nodeType = node.getClass().getSimpleName();
            nodeContainerId = getNodeContainerId(node.getNodeContainer());
        } else {
            nodeId = Long.toString(nodeInstance.getNodeId());
            nodeType = (String)nodeInstance.getMetaData("NodeType");
        }
        NodeInstanceLog logEvent =null;
        if (log != null) {
            logEvent = (NodeInstanceLog) log;
        } else {
            logEvent = new NodeInstanceLog(
                NodeInstanceLog.TYPE_EXIT, pi.getId(), pi.getProcessId(), Long.toString(nodeInstance.getId()), 
                nodeId, nodeInstance.getNodeName());
        }
        if (nodeInstance instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) nodeInstance).getWorkItem() != null) {
            logEvent.setWorkItemId(((WorkItemNodeInstance) nodeInstance).getWorkItem().getId());
        }
        if (nodeInstance instanceof SubProcessNodeInstance) {
            logEvent.setReferenceId(((SubProcessNodeInstance) nodeInstance).getProcessInstanceId());
        }
        String connection = (String)nodeInstance.getMetaData().get("OutgoingConnection");
        logEvent.setConnection(connection);
        logEvent.setExternalId(""+((KieSession) pnle.getKieRuntime()).getIdentifier());
        logEvent.setNodeType(nodeType);
        logEvent.setNodeContainerId(nodeContainerId);
        logEvent.setDate(pnle.getEventDate());
        logEvent.setSlaCompliance(nodeInstance.getSlaCompliance());
        logEvent.setSlaDueDate(nodeInstance.getSlaDueDate());
        return logEvent;
    }

    @Override
    public AuditEvent buildEvent(ProcessVariableChangedEvent pvce) {
        long processInstanceId = pvce.getProcessInstance().getId();
        String processId = pvce.getProcessInstance().getProcessId();
        String variableId = pvce.getVariableId();
        String variableInstanceId = pvce.getVariableInstanceId();
        String oldValue = (pvce.getOldValue() != null)?pvce.getOldValue().toString():"";
        String newValue = (pvce.getNewValue() != null)?pvce.getNewValue().toString():"";
        VariableInstanceLog log = new VariableInstanceLog(
                processInstanceId, processId, variableInstanceId, variableId, newValue, oldValue);
        log.setExternalId(""+((KieSession) pvce.getKieRuntime()).getIdentifier());
        return log;
    }

    protected String getNodeContainerId(NodeContainer nodeContainer) {
        if (nodeContainer instanceof Node) {
            return (String) ((Node) nodeContainer).getMetaData().get("UniqueId");
        }
        return null;
    }

}
