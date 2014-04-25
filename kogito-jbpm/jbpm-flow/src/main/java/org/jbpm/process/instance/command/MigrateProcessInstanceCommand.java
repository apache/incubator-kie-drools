package org.jbpm.process.instance.command;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.Context;

public class MigrateProcessInstanceCommand implements GenericCommand<Void> {
	
    private static final long serialVersionUID = 6L;
	
	private Long processInstanceId;
    private String processId;
    private Map<String, Long> nodeMapping;

    public MigrateProcessInstanceCommand(Long processInstanceId, String processId) {
    	this.processInstanceId = processInstanceId;
    	this.processId = processId;
    }
    
    public MigrateProcessInstanceCommand(Long processInstanceId, String processId, Map<String, Long> nodeMapping) {
    	this.processInstanceId = processInstanceId;
    	this.processId = processId;
    	this.nodeMapping = nodeMapping;
    }
    
    public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Map<String, Long> getNodeMapping() {
		return nodeMapping;
	}

	public void setNodeMapping(Map<String, Long> nodeMapping) {
		this.nodeMapping = nodeMapping;
	}

	public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
    		ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            throw new IllegalArgumentException("Null process id");
        }
        WorkflowProcess process = (WorkflowProcess)
            ksession.getKieBase().getProcess(processId);
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            return null;
        }
        synchronized (processInstance) {
        	org.kie.api.definition.process.Process oldProcess = processInstance.getProcess();
	        processInstance.disconnect();
	        processInstance.setProcess(oldProcess);
	        if (nodeMapping == null) {
	    		nodeMapping = new HashMap<String, Long>();
	    	}
	        updateNodeInstances(processInstance, nodeMapping);
	        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) ksession);
	        processInstance.setProcess(process);
	        processInstance.reconnect();
		}
        return null;
    }

    private void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            String oldNodeId = ((NodeImpl)
        		((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getNode()).getUniqueId();
            Long newNodeId = nodeMapping.get(oldNodeId);
            if (newNodeId == null) {
                newNodeId = nodeInstance.getNodeId();
            }
            ((NodeInstanceImpl) nodeInstance).setNodeId(newNodeId);
            if (nodeInstance instanceof NodeInstanceContainer) {
            	updateNodeInstances((NodeInstanceContainer) nodeInstance, nodeMapping);
            }
        }
    }
    
    public String toString() {
    	return "migrateProcessInstance(" + processInstanceId +", \"" + processId + "\");";
    }
    
}
