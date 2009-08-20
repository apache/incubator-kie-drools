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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.common.InternalRuleBase;
import org.drools.definition.process.Process;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.instance.impl.NodeInstanceResolverFactory;
import org.drools.workflow.instance.impl.VariableScopeResolverFactory;
import org.mvel2.MVEL;

/**
 * Runtime counterpart of a SubFlow node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SubProcessNodeInstance extends StateBasedNodeInstance implements EventListener {

    private static final long serialVersionUID = 400L;
    private static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{(\\S+)\\}", Pattern.DOTALL);
    
    private long processInstanceId;
	
    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Map.Entry<String, String> mapping: getSubProcessNode().getInMappings().entrySet()) {
        	Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(mapping.getValue());
            } else {
            	try {
            		parameterValue = MVEL.eval(mapping.getValue(), new NodeInstanceResolverFactory(this));
            	} catch (Throwable t) {
            		System.err.println("Could not find variable scope for variable " + mapping.getValue());
                    System.err.println("when trying to execute SubProcess node " + getSubProcessNode().getName());
                    System.err.println("Continuing without setting parameter.");
            	}
            }
            if (parameterValue != null) {
            	parameters.put(mapping.getKey(),parameterValue); 
            }
        }
        String processId = getSubProcessNode().getProcessId();
        // resolve processId if necessary
        Map<String, String> replacements = new HashMap<String, String>();
		Matcher matcher = PARAMETER_MATCHER.matcher(processId);
        while (matcher.find()) {
        	String paramName = matcher.group(1);
        	if (replacements.get(paramName) == null) {
            	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                	resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                	String variableValueString = variableValue == null ? "" : variableValue.toString(); 
	                replacements.put(paramName, variableValueString);
                } else {
                	try {
                		Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(this));
	                	String variableValueString = variableValue == null ? "" : variableValue.toString();
	                	replacements.put(paramName, variableValueString);
                	} catch (Throwable t) {
	                    System.err.println("Could not find variable scope for variable " + paramName);
	                    System.err.println("when trying to replace variable in processId for sub process " + getNodeName());
	                    System.err.println("Continuing without setting process id.");
                	}
                }
        	}
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
        	processId = processId.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        // start process instance
        Process process = ((InternalRuleBase) ((ProcessInstance) getProcessInstance())
    		.getWorkingMemory().getRuleBase()).getProcess(processId);
        if (process == null) {
        	System.err.println("Could not find process " + processId);
        	System.err.println("Aborting process");
        	((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED);
        } else {
	    	ProcessInstance processInstance = ( ProcessInstance )
	    		((ProcessInstance) getProcessInstance()).getWorkingMemory()
	    			.startProcess(processId, parameters);
	    	if (!getSubProcessNode().isWaitForCompletion()) {
	    		triggerCompleted();
	    	} else if (processInstance.getState() == ProcessInstance.STATE_COMPLETED) {
	    		handleOutMappings(processInstance);
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
            ProcessInstance processInstance = (ProcessInstance)
                ((ProcessInstance) getProcessInstance()).getWorkingMemory()
                    .getProcessInstance(processInstanceId);
            if (processInstance != null) {
            	processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
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
        handleOutMappings(processInstance);
        triggerCompleted();
    }
    
    private void handleOutMappings(ProcessInstance processInstance) {
        VariableScopeInstance subProcessVariableScopeInstance = (VariableScopeInstance)
	        processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
	    for (Map.Entry<String, String> mapping: getSubProcessNode().getOutMappings().entrySet()) {
	        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	            resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
	        if (variableScopeInstance != null) {
	        	Object value = subProcessVariableScopeInstance.getVariable(mapping.getKey());
	        	if (value == null) {
	        		try {
	            		value = MVEL.eval(mapping.getKey(), new VariableScopeResolverFactory(subProcessVariableScopeInstance));
	            	} catch (Throwable t) {
	            		// do nothing
	            	}
	        	}
	            variableScopeInstance.setVariable(mapping.getValue(), value);
	        } else {
	            System.err.println("Could not find variable scope for variable " + mapping.getValue());
	            System.err.println("when trying to complete SubProcess node " + getSubProcessNode().getName());
	            System.err.println("Continuing without setting variable.");
	        }
	    }
    }

}