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

import org.drools.common.InternalRuleBase;
import org.drools.process.core.Process;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.EventListener;
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
public class SubProcessNodeInstance extends EventBasedNodeInstance implements EventListener {

    private static final long serialVersionUID = 400L;
    
    private long processInstanceId;
	
    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
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
        String processId = getSubProcessNode().getProcessId();
        Process process = ((InternalRuleBase) getProcessInstance().getWorkingMemory().getRuleBase()).getProcess(processId);
        if (process == null) {
        	System.err.println("Could not find process " + processId);
        	System.err.println("Aborting process");
        	getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
        } else {
	    	ProcessInstance processInstance = 
	    		getProcessInstance().getWorkingMemory().startProcess(processId, parameters);
	    	if (!getSubProcessNode().isWaitForCompletion()
	    	        || processInstance.getState() == ProcessInstance.STATE_COMPLETED) {
	    		triggerCompleted();
	    	} else {
	    		this.processInstanceId = processInstance.getId();
	    	    addProcessListener();
	    	}
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
        addProcessListener();
    }
    
    private void addProcessListener() {
        getProcessInstance().addEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

	public void signalEvent(String type, Object event) {
		if (("processInstanceCompleted:" + processInstanceId).equals(type)) {
			processInstanceCompleted((ProcessInstance) event);
		} else {
			super.signalEvent(type, event);
		}
	}
    
    public String[] getEventTypes() {
    	return new String[] { "processInstanceCompleted:" + processInstanceId };
    }
    
    public void processInstanceCompleted(ProcessInstance processInstance) {
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