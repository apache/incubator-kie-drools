package org.drools.spi;

import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.instance.NodeInstance;

public class ActionContext {
    
    private NodeInstance nodeInstance;

    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }
    
    public Object getVariable(String variableName) {
    	VariableScopeInstance variableScope = (VariableScopeInstance) nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
    	if (variableScope == null) {
    		return null;
    	}
    	return variableScope.getVariable(variableName);
    }
    
}
