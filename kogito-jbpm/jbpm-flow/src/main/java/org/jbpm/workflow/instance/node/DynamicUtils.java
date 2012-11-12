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

import org.drools.command.CommandService;
import org.drools.command.Context;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.event.ProcessEventSupport;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.definition.process.Process;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.WorkItem;

public class DynamicUtils {
	
	public static void addDynamicWorkItem(
			final DynamicNodeInstance dynamicContext, KnowledgeRuntime ksession,
			String workItemName, Map<String, Object> parameters) {
		final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
		internalAddDynamicWorkItem(processInstance, dynamicContext, ksession, workItemName, parameters);
	}
	
	public static void addDynamicWorkItem(
			final org.kie.runtime.process.ProcessInstance dynamicProcessInstance, KnowledgeRuntime ksession,
			String workItemName, Map<String, Object> parameters) {
		internalAddDynamicWorkItem((WorkflowProcessInstance) dynamicProcessInstance, null, ksession, workItemName, parameters);
	}
	
	private static void internalAddDynamicWorkItem(
			final WorkflowProcessInstance processInstance, final DynamicNodeInstance dynamicContext, 
			KnowledgeRuntime ksession, String workItemName, Map<String, Object> parameters) {
		final WorkItemImpl workItem = new WorkItemImpl();
		workItem.setState(WorkItem.ACTIVE);
		workItem.setProcessInstanceId(processInstance.getId());
		workItem.setName(workItemName);
		workItem.setParameters(parameters);
		final WorkItemNodeInstance workItemNodeInstance = new WorkItemNodeInstance();
    	workItemNodeInstance.setNodeInstanceContainer(dynamicContext == null ? processInstance : dynamicContext);
		workItemNodeInstance.setProcessInstance(processInstance);
		workItemNodeInstance.internalSetWorkItem(workItem);
    	workItemNodeInstance.addEventListeners();
    	if (ksession instanceof StatefulKnowledgeSessionImpl) {
    		executeWorkItem((StatefulKnowledgeSessionImpl) ksession, workItem, workItemNodeInstance);
		} else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
    		CommandService commandService = ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
    		commandService.execute(new GenericCommand<Void>() {
				private static final long serialVersionUID = 5L;
				public Void execute(Context context) {
                    StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
                    WorkflowProcessInstance realProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstance.getId());
                    workItemNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                    	workItemNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
                    	DynamicNodeInstance realDynamicContext = findDynamicContext(realProcessInstance, dynamicContext.getUniqueId());
                    	workItemNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    executeWorkItem((StatefulKnowledgeSessionImpl) ksession, workItem, workItemNodeInstance);
                    return null;
                }
            });
    	} else {
    		throw new IllegalArgumentException("Unsupported ksession: " + ksession == null ? "null" : ksession.getClass().getName());
    	}
	}
	
	private static void executeWorkItem(StatefulKnowledgeSessionImpl ksession, WorkItemImpl workItem, WorkItemNodeInstance workItemNodeInstance) {
		ProcessEventSupport eventSupport = ((InternalProcessRuntime)
			ksession.getProcessRuntime()).getProcessEventSupport();
		eventSupport.fireBeforeNodeTriggered(workItemNodeInstance, ksession);
		((WorkItemManager) ksession.getWorkItemManager()).internalExecuteWorkItem(workItem);
		workItemNodeInstance.internalSetWorkItemId(workItem.getId());
		eventSupport.fireAfterNodeTriggered(workItemNodeInstance, ksession);
	}
	
	private static DynamicNodeInstance findDynamicContext(WorkflowProcessInstance processInstance, String uniqueId) {
		for (NodeInstance nodeInstance: ((WorkflowProcessInstanceImpl) processInstance).getNodeInstances(true)) {
			if (uniqueId.equals(((NodeInstanceImpl) nodeInstance).getUniqueId())) {
				return (DynamicNodeInstance) nodeInstance;
			}
		}
		throw new IllegalArgumentException("Could not find node instance " + uniqueId);
	}

	public static void addDynamicSubProcess(
			final DynamicNodeInstance dynamicContext, KnowledgeRuntime ksession,
			final String processId, final Map<String, Object> parameters) {
		final WorkflowProcessInstance processInstance = dynamicContext.getProcessInstance();
		internalAddDynamicSubProcess(processInstance, dynamicContext, ksession, processId, parameters);
	}
	
	public static void addDynamicSubProcess(
			final org.kie.runtime.process.ProcessInstance processInstance, KnowledgeRuntime ksession,
			final String processId, final Map<String, Object> parameters) {
		internalAddDynamicSubProcess((WorkflowProcessInstance) processInstance, null, ksession, processId, parameters);
	}
	
	public static void internalAddDynamicSubProcess(
			final WorkflowProcessInstance processInstance, final DynamicNodeInstance dynamicContext,
			KnowledgeRuntime ksession, final String processId, final Map<String, Object> parameters) {
		final SubProcessNodeInstance subProcessNodeInstance = new SubProcessNodeInstance();
    	subProcessNodeInstance.setNodeInstanceContainer(dynamicContext == null ? processInstance : dynamicContext);
		subProcessNodeInstance.setProcessInstance(processInstance);
    	if (ksession instanceof StatefulKnowledgeSessionImpl) {
    		executeSubProcess((StatefulKnowledgeSessionImpl) ksession, processId, parameters, processInstance, subProcessNodeInstance);
    	} else if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
    		CommandService commandService = ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
    		commandService.execute(new GenericCommand<Void>() {
				private static final long serialVersionUID = 5L;
				public Void execute(Context context) {
                    StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
                    WorkflowProcessInstance realProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstance.getId());
                    subProcessNodeInstance.setProcessInstance(realProcessInstance);
                    if (dynamicContext == null) {
                    	subProcessNodeInstance.setNodeInstanceContainer(realProcessInstance);
                    } else {
	                    DynamicNodeInstance realDynamicContext = findDynamicContext(realProcessInstance, dynamicContext.getUniqueId());
	                    subProcessNodeInstance.setNodeInstanceContainer(realDynamicContext);
                    }
                    executeSubProcess((StatefulKnowledgeSessionImpl) ksession, processId, parameters, processInstance, subProcessNodeInstance);
                    return null;
                }
            });
    	} else {
    		throw new IllegalArgumentException("Unsupported ksession: " + ksession == null ? "null" : ksession.getClass().getName());
    	}
	}
	
	private static void executeSubProcess(StatefulKnowledgeSessionImpl ksession, String processId, Map<String, Object> parameters, ProcessInstance processInstance, SubProcessNodeInstance subProcessNodeInstance) {
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
