package org.drools.workflow.instance.node;

import java.util.Map;

import org.drools.common.EventSupport;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Process;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.workflow.instance.WorkflowProcessInstance;


public class DynamicUtils {
	
	public static void addDynamicWorkItem(
			DynamicNodeInstance dynamicContext, ProcessRuntime ksession,
			String workItemName, Map<String, Object> parameters) {
		WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setState(WorkItem.ACTIVE);
		workItem.setProcessInstanceId(processInstance.getId());
		workItem.setName(workItemName);
		workItem.setParameters(parameters);
		WorkItemNodeInstance workItemNodeInstance = new WorkItemNodeInstance();
    	workItemNodeInstance.setNodeInstanceContainer(dynamicContext);
		workItemNodeInstance.setProcessInstance(processInstance);
		workItemNodeInstance.internalSetWorkItem(workItem);
    	workItemNodeInstance.addEventListeners();
		InternalWorkingMemory workingMemory = ((StatefulKnowledgeSessionImpl) ksession).session;
		((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(workItemNodeInstance, workingMemory);
		((WorkItemManager) ksession.getWorkItemManager()).internalExecuteWorkItem(workItem);
        ((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(workItemNodeInstance, (InternalWorkingMemory) workingMemory);
	}

	public static void addDynamicSubProcess(
			DynamicNodeInstance dynamicContext, ProcessRuntime ksession,
			String processId, Map<String, Object> parameters) {
		WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
		SubProcessNodeInstance subProcessNodeInstance = new SubProcessNodeInstance();
    	subProcessNodeInstance.setNodeInstanceContainer(dynamicContext);
		subProcessNodeInstance.setProcessInstance(processInstance);
		InternalWorkingMemory workingMemory = ((StatefulKnowledgeSessionImpl) ksession).session;
		Process process = ((InternalRuleBase) workingMemory.getRuleBase()).getProcess(processId);
        if (process == null) {
        	System.err.println("Could not find process " + processId);
        	System.err.println("Aborting process");
        	processInstance.setState(ProcessInstance.STATE_ABORTED);
        } else {
        	((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(subProcessNodeInstance, workingMemory);
    		ProcessInstance subProcessInstance = (ProcessInstance)
	    		workingMemory.startProcess(processId, parameters);
    		((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(subProcessNodeInstance, (InternalWorkingMemory) workingMemory);
    		if (subProcessInstance.getState() == ProcessInstance.STATE_COMPLETED) {
	    		subProcessNodeInstance.triggerCompleted();
	    	} else {
	    		subProcessNodeInstance.internalSetProcessInstanceId(subProcessInstance.getId());
	    	    subProcessNodeInstance.addEventListeners();
	    	}
        }
	}

}
