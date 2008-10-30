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

import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.NodeInstance;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a fault node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class FaultNodeInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;
    
    protected FaultNode getFaultNode() {
        return (FaultNode) getNode();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A FaultNode only accepts default incoming connections!");
        }
        String faultName = getFaultName();
        ExceptionScopeInstance exceptionScopeInstance = getExceptionScopeInstance(faultName);
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
        if (exceptionScopeInstance != null) {
        	handleException(faultName, exceptionScopeInstance);
        } else {
        	((InternalProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED);
        }
    }
    
    protected ExceptionScopeInstance getExceptionScopeInstance(String faultName) {
    	return (ExceptionScopeInstance)
    		resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultName);
    }
    
    protected String getFaultName() {
    	return getFaultNode().getFaultName();
    }
    
    protected Object getFaultData() {
    	Object value = null;
    	String faultVariable = getFaultNode().getFaultVariable();
    	if (faultVariable != null) {
    		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            	resolveContextInstance(VariableScope.VARIABLE_SCOPE, faultVariable);
            if (variableScopeInstance != null) {
                value = variableScopeInstance.getVariable(faultVariable);
            } else {
                System.err.println("Could not find variable scope for variable " + faultVariable);
                System.err.println("when trying to execute fault node " + getFaultNode().getName());
                System.err.println("Continuing without setting value.");
            }
    	}
    	return value;
    }
    
    protected void handleException(String faultName, ExceptionScopeInstance exceptionScopeInstance) {
        exceptionScopeInstance.handleException(faultName, getFaultData());
    }

}