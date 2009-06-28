package org.drools.workflow.instance;

import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.WorkflowProcess;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.instance.impl.NodeInstanceImpl;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class WorkflowProcessInstanceUpgrader {

    public static void upgradeProcessInstance(
    		StatefulKnowledgeSession session,
    		long processInstanceId,
    		String processId,
    		Map<String, Long> nodeMapping) {
    	upgradeProcessInstance(
			((StatefulKnowledgeSessionImpl) session).session,
			processInstanceId,
			processId,
			nodeMapping);
    }
	
    public static void upgradeProcessInstance(
    		WorkingMemory workingMemory,
    		long processInstanceId,
    		String processId,
    		Map<String, Long> nodeMapping) {
    	if (nodeMapping == null) {
    		nodeMapping = new HashMap<String, Long>();
    	}
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
            workingMemory.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        if (processId == null) {
            throw new IllegalArgumentException("Null process id");
        }
        WorkflowProcess process = (WorkflowProcess)
            ((InternalRuleBase) workingMemory.getRuleBase()).getProcess(processId);
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processId);
        }
        if (processInstance.getProcessId().equals(processId)) {
            return;
        }
        synchronized (processInstance) {
        	org.drools.definition.process.Process oldProcess = processInstance.getProcess();
	        processInstance.disconnect();
	        processInstance.setProcess(oldProcess);
	        updateNodeInstances(processInstance, nodeMapping);
	        processInstance.setWorkingMemory((InternalWorkingMemory) workingMemory);
	        processInstance.setProcess(process);
	        processInstance.reconnect();
		}
    }
    
    private static void updateNodeInstances(NodeInstanceContainer nodeInstanceContainer, Map<String, Long> nodeMapping) {
    	NodeContainer nodeContainer = nodeInstanceContainer.getNodeContainer();
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            String oldNodeId = ((NodeImpl)
        		((org.drools.workflow.instance.NodeInstance) nodeInstance).getNode()).getUniqueId();
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

}
