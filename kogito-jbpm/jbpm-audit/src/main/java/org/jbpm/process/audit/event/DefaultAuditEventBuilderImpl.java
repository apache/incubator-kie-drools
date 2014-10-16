package org.jbpm.process.audit.event;

import java.util.Date;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

public class DefaultAuditEventBuilderImpl implements AuditEventBuilder {

    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pse.getProcessInstance();
        ProcessInstanceLog log = new ProcessInstanceLog(pi.getId(), pi.getProcessId());
        log.setExternalId(""+((KieSession) pse.getKieRuntime()).getId());
        log.setProcessName(pi.getProcess().getName());
        log.setProcessVersion(pi.getProcess().getVersion());
        log.setStatus(ProcessInstance.STATE_ACTIVE);
        log.setProcessInstanceDescription( pi.getDescription() );
        try {
            long parentProcessInstanceId = (Long) pi.getMetaData().get("ParentProcessInstanceId");
            log.setParentProcessInstanceId(parentProcessInstanceId);
        } catch (Exception e) {
            //in case of problems with getting hold of parentProcessInstanceId don't break the operation
        }        
        
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
        logEvent.setEnd(new Date());
        logEvent.setDuration(logEvent.getEnd().getTime() - logEvent.getStart().getTime());
        logEvent.setProcessInstanceDescription( pi.getDescription() );
        return logEvent;
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        ProcessInstanceImpl pi = (ProcessInstanceImpl) pnte.getProcessInstance();
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) pnte.getNodeInstance();
        Node node = nodeInstance.getNode();
        String nodeId = null;
        String nodeType = null;
        if (node != null) {
            nodeId = (String)node.getMetaData().get("UniqueId");
            nodeType = node.getClass().getSimpleName();
        } else {
            nodeId = Long.toString(nodeInstance.getNodeId());
        }
        NodeInstanceLog log = new NodeInstanceLog(
                NodeInstanceLog.TYPE_ENTER, pi.getId(), pi.getProcessId(), Long.toString(nodeInstance.getId()), 
                nodeId, nodeInstance.getNodeName());
        if (nodeInstance instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) nodeInstance).getWorkItem() != null) {
            log.setWorkItemId(((WorkItemNodeInstance) nodeInstance).getWorkItem().getId());
        }
        String connection = (String)nodeInstance.getMetaData().get("IncomingConnection");
        log.setConnection(connection);
        log.setExternalId(""+((KieSession) pnte.getKieRuntime()).getId());
        log.setNodeType(nodeType);
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
        if (node != null) {
            nodeId = (String)node.getMetaData().get("UniqueId");
            nodeType = node.getClass().getSimpleName();
        } else {
            nodeId = Long.toString(nodeInstance.getNodeId());
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
        String connection = (String)nodeInstance.getMetaData().get("OutgoingConnection");
        logEvent.setConnection(connection);
        logEvent.setExternalId(""+((KieSession) pnle.getKieRuntime()).getId());
        logEvent.setNodeType(nodeType);
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
        log.setExternalId(""+((KieSession) pvce.getKieRuntime()).getId());
        return log;
    }

}
