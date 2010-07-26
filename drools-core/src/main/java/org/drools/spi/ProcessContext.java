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

package org.drools.spi;

import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;

public class ProcessContext implements org.drools.runtime.process.ProcessContext {
    
	private ProcessInstance processInstance;
    private NodeInstance nodeInstance;

    public ProcessInstance getProcessInstance() {
    	if (processInstance != null) {
    		return processInstance;
    	}
    	if (nodeInstance != null) {
    		return (ProcessInstance) nodeInstance.getProcessInstance();
    	}
    	return null;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
    
    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }
    
    public Object getVariable(String variableName) {
    	VariableScopeInstance variableScope = null;
    	if (nodeInstance != null) {
	    	variableScope = (VariableScopeInstance) ((org.drools.workflow.instance.NodeInstance)
				nodeInstance).resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
    	}
    	if (variableScope == null) {
    		variableScope = (VariableScopeInstance) ((ProcessInstance) 
    			getProcessInstance()).getContextInstance(VariableScope.VARIABLE_SCOPE);
    	}
    	return variableScope.getVariable(variableName);
    }
    
    public void setVariable(String variableName, Object value) {
    	VariableScopeInstance variableScope = null;
    	if (nodeInstance != null) {
    		variableScope = (VariableScopeInstance) ((org.drools.workflow.instance.NodeInstance) 
    			nodeInstance).resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
    	}
    	if (variableScope == null) {
    		variableScope = (VariableScopeInstance) getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE);
    		if (variableScope.getVariableScope().findVariable(variableName) == null) {
    			variableScope = null;
    		}
    	}
    	if (variableScope == null) {
    		System.err.println("Could not find variable " + variableName);
    		System.err.println("Using process-level scope");
    		variableScope = (VariableScopeInstance) ((ProcessInstance) 
    			getProcessInstance()).getContextInstance(VariableScope.VARIABLE_SCOPE);
    	}
    	variableScope.setVariable(variableName, value);
    }

	public KnowledgeRuntime getKnowledgeRuntime() {
		return new StatefulKnowledgeSessionImpl(
			(ReteooStatefulSession) getProcessInstance().getWorkingMemory() );
	}
    
}
