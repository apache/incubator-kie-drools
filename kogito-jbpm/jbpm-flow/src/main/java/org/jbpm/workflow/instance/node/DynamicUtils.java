/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.workflow.instance.node;

import java.util.Map;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.definition.process.Process;
import org.drools.event.ProcessEventSupport;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;

public class DynamicUtils {
	
	public static void addDynamicWorkItem(
			DynamicNodeInstance dynamicContext, KnowledgeRuntime ksession,
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
		ProcessEventSupport eventSupport = ((InternalProcessRuntime)
			((InternalKnowledgeRuntime) ksession).getProcessRuntime()).getProcessEventSupport();
		eventSupport.fireBeforeNodeTriggered(workItemNodeInstance, ksession);
		((WorkItemManager) ksession.getWorkItemManager()).internalExecuteWorkItem(workItem);
		eventSupport.fireAfterNodeTriggered(workItemNodeInstance, ksession);
	}

	public static void addDynamicSubProcess(
			DynamicNodeInstance dynamicContext, KnowledgeRuntime ksession,
			String processId, Map<String, Object> parameters) {
		WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
		SubProcessNodeInstance subProcessNodeInstance = new SubProcessNodeInstance();
    	subProcessNodeInstance.setNodeInstanceContainer(dynamicContext);
		subProcessNodeInstance.setProcessInstance(processInstance);
		Process process = ksession.getKnowledgeBase().getProcess(processId);
        if (process == null) {
        	System.err.println("Could not find process " + processId);
        	System.err.println("Aborting process");
        	processInstance.setState(ProcessInstance.STATE_ABORTED);
        } else {
        	ProcessEventSupport eventSupport = ((InternalProcessRuntime)
    			((InternalKnowledgeRuntime) ksession).getProcessRuntime()).getProcessEventSupport();
    		eventSupport.fireBeforeNodeTriggered(subProcessNodeInstance, ksession);
    		ProcessInstance subProcessInstance = (ProcessInstance)
	    		ksession.startProcess(processId, parameters);
    		eventSupport.fireAfterNodeTriggered(subProcessNodeInstance, ksession);
    		if (subProcessInstance.getState() == ProcessInstance.STATE_COMPLETED) {
	    		subProcessNodeInstance.triggerCompleted();
	    	} else {
	    		subProcessNodeInstance.internalSetProcessInstanceId(subProcessInstance.getId());
	    	    subProcessNodeInstance.addEventListeners();
	    	}
        }
	}

}
