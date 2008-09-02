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
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowCompletedEvent;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.RuleFlowStartedEvent;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.instance.NodeInstance;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SubProcessNodeInstance extends EventBasedNodeInstance implements RuleFlowEventListener {

    private static final long serialVersionUID = 400L;
    
    private long processInstanceId;
	
    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Map.Entry<String, String> mapping: getSubProcessNode().getInMappings().entrySet()) {
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
            if (variableScopeInstance != null) {
                parameters.put(mapping.getKey(), variableScopeInstance.getVariable(mapping.getValue()));
            } else {
                System.err.println("Could not find variable scope for variable " + mapping.getValue());
                System.err.println("when trying to execute SubProcess node " + getSubProcessNode().getName());
                System.err.println("Continuing without setting parameter.");
            }
        }
    	ProcessInstance processInstance = 
    		getProcessInstance().getWorkingMemory().startProcess(getSubProcessNode().getProcessId(), parameters);
    	if (!getSubProcessNode().isWaitForCompletion()
    	        || processInstance.getState() == ProcessInstance.STATE_COMPLETED) {
    		triggerCompleted();
    	} else {
    	    addEventListeners();
    		this.processInstanceId = processInstance.getId();
    	}
    }
    
    public void cancel() {
        super.cancel();
        if (!getSubProcessNode().isIndependent()) {
            ProcessInstance processInstance =
                getProcessInstance().getWorkingMemory()
                    .getProcessInstance(processInstanceId);
            processInstance.setState(ProcessInstance.STATE_ABORTED);
        }
    }
    
    public long getProcessInstanceId() {
    	return processInstanceId;
    }
    
    public void internalSetProcessInstanceId(long processInstanceId) {
    	this.processInstanceId = processInstanceId;
    }

    public void addEventListeners() {
        super.addEventListeners();
        getProcessInstance().getWorkingMemory().addEventListener(this);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().getWorkingMemory().removeEventListener(this);
    }

    public void afterRuleFlowCompleted(RuleFlowCompletedEvent event,
            WorkingMemory workingMemory) {
        ProcessInstance processInstance = event.getProcessInstance();
        if ( processInstance.getId() == processInstanceId ) {
            removeEventListeners();
            VariableScopeInstance subProcessVariableScopeInstance = (VariableScopeInstance)
                processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
            for (Map.Entry<String, String> mapping: getSubProcessNode().getOutMappings().entrySet()) {
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
                if (variableScopeInstance != null) {
                    variableScopeInstance.setVariable(mapping.getValue(), subProcessVariableScopeInstance.getVariable(mapping.getKey()));
                } else {
                    System.err.println("Could not find variable scope for variable " + mapping.getValue());
                    System.err.println("when trying to complete SubProcess node " + getSubProcessNode().getName());
                    System.err.println("Continuing without setting variable.");
                }
            }
            triggerCompleted();
        }
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowNodeTriggered(RuleFlowNodeTriggeredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void afterRuleFlowStarted(RuleFlowStartedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowCompleted(RuleFlowCompletedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowNodeTriggered(RuleFlowNodeTriggeredEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

    public void beforeRuleFlowStarted(RuleFlowStartedEvent event,
            WorkingMemory workingMemory) {
        // Do nothing
    }

	public void afterRuleFlowNodeLeft(RuleFlowNodeTriggeredEvent event,
			WorkingMemory workingMemory) {
        // Do nothing
	}

	public void beforeRuleFlowNodeLeft(RuleFlowNodeTriggeredEvent event,
			WorkingMemory workingMemory) {
        // Do nothing
	}

}