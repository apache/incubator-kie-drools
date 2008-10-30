package org.drools.workflow.instance;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.knowledge.definitions.process.WorkflowProcess;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.NodeInstance;
import org.drools.process.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class WorkflowProcessInstanceUpgrader {
    
    public static void upgradeProcessInstance(WorkingMemory workingMemory, long processInstanceId, String processId, Map<Long, Long> nodeMapping) {
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
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
        ((WorkflowProcessInstanceImpl) processInstance).disconnect();
        ((InternalProcessInstance) processInstance).setProcess(process);
        for (NodeInstance nodeInstance: processInstance.getNodeInstances()) {
            Long oldNodeId = nodeInstance.getNodeId();
            Long newNodeId = nodeMapping.get(oldNodeId);
            if (newNodeId == null) {
                newNodeId = oldNodeId;
            }
            if (process.getNode(newNodeId) == null) {
                throw new IllegalArgumentException("Could not find node " + newNodeId);
            }
            if (newNodeId != oldNodeId) {
                ((NodeInstanceImpl) nodeInstance).setNodeId(newNodeId);
            }
        }
        ((InternalProcessInstance) processInstance).setWorkingMemory((InternalWorkingMemory) workingMemory);
        ((WorkflowProcessInstanceImpl) processInstance).reconnect();
    }

}
