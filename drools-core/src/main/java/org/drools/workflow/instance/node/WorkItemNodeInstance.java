package org.drools.workflow.instance.node;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.process.core.Work;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemListener;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.instance.NodeInstance;

/**
 * Runtime counterpart of a task node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemNodeInstance extends EventNodeInstance implements WorkItemListener {

    private static final long serialVersionUID = 400L;
    
    private long workItemId = -1;
    private transient WorkItemImpl workItem;
    
    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }
    
    public WorkItem getWorkItem() {
    	if (workItem == null && workItemId >= 0) {
    		workItem = (WorkItemImpl) getProcessInstance().getWorkingMemory()
    			.getWorkItemManager().getWorkItem(workItemId);
    	}
        return workItem;
    }
    
    public long getWorkItemId() {
        return workItemId;
    }
    
    public void internalSetWorkItemId(long workItemId) {
    	this.workItemId = workItemId;
    }
    
    public boolean isInversionOfControl() {
        return ((InternalRuleBase) getProcessInstance().getWorkingMemory().getRuleBase()).getConfiguration().isAdvancedProcessRuleIntegration();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        // TODO this should be included for ruleflow only, not for BPEL
//        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
//            throw new IllegalArgumentException(
//                "A WorkItemNode only accepts default incoming connections!");
//        }
        WorkItemNode workItemNode = getWorkItemNode();
        createWorkItem(workItemNode);
		if (workItemNode.isWaitForCompletion()) {
		    addEventListeners();
        }
		if (isInversionOfControl()) {
		    getProcessInstance().getWorkingMemory().update(getProcessInstance().getWorkingMemory().getFactHandle(this), this);
		} else {
		    getProcessInstance().getWorkingMemory().getWorkItemManager().internalExecuteWorkItem(workItem);
		}
        if (!workItemNode.isWaitForCompletion()) {
            triggerCompleted();
        } else {
        	this.workItemId = workItem.getId();
        }
    }
    
    protected WorkItem createWorkItem(WorkItemNode workItemNode) {
        Work work = workItemNode.getWork();
        workItem = new WorkItemImpl();
        workItem.setName(work.getName());
        workItem.setProcessInstanceId(getProcessInstance().getId());
        workItem.setParameters(new HashMap<String, Object>(work.getParameters()));
        for (Iterator<Map.Entry<String, String>> iterator = workItemNode.getInMappings().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> mapping = iterator.next();
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
            if (variableScopeInstance != null) {
                workItem.setParameter(mapping.getKey(), variableScopeInstance.getVariable(mapping.getValue()));
            } else {
                System.err.println("Could not find variable scope for variable " + mapping.getValue());
                System.err.println("when trying to execute Work Item " + work.getName());
                System.err.println("Continuing without setting parameter.");
            }
        }
        return workItem;
    }

    public void triggerCompleted(WorkItem workItem) {
        for (Iterator<Map.Entry<String, String>> iterator = getWorkItemNode().getOutMappings().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> mapping = iterator.next();
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
            if (variableScopeInstance != null) {
                variableScopeInstance.setVariable(mapping.getValue(), workItem.getResult(mapping.getKey()));
            } else {
                System.err.println("Could not find variable scope for variable " + mapping.getValue());
                System.err.println("when trying to complete Work Item " + workItem.getName());
                System.err.println("Continuing without setting variable.");
            }
        }
        if (isInversionOfControl()) {
            WorkingMemory workingMemory = getProcessInstance().getWorkingMemory();
            workingMemory.update(workingMemory.getFactHandle(this), this);
        } else {
            triggerCompleted();
        }
    }
    
    public void cancel() {
        super.cancel();
        getProcessInstance().getWorkingMemory().getWorkItemManager().internalAbortWorkItem(workItem.getId());
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        getProcessInstance().addWorkItemListener(this);
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeWorkItemListener(this);
    }

    public void workItemAborted(WorkItem workItem) {
        if ( getWorkItem().getId() == workItem.getId() ) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }

    public void workItemCompleted(WorkItem workItem) {
        if ( getWorkItem().getId() == workItem.getId() ) {
            removeEventListeners();
            triggerCompleted(workItem);
        }
    }

}