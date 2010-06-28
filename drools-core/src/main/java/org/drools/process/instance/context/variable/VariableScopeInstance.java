package org.drools.process.instance.context.variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.EventSupport;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.context.AbstractContextInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.instance.node.CompositeContextNodeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class VariableScopeInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 400L;
    
    private Map<String, Object> variables = new HashMap<String, Object>();
    private transient String variableIdPrefix = null;
    private transient String variableInstanceIdPrefix = null;

    public String getContextType() {
        return VariableScope.VARIABLE_SCOPE;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public void setVariable(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(
                "The name of a variable may not be null!");
        }
        if (getProcessInstance() != null) {
            ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport()
            	.fireBeforeVariableChange(
        			getProcessInstance(),
        			(variableIdPrefix == null ? "" : variableIdPrefix + ":") + name,
        			(variableInstanceIdPrefix == null? "" : variableInstanceIdPrefix + ":") + name,
        			variables.get(name),
        			getProcessInstance().getWorkingMemory());
        }
        variables.put(name, value);
        if (getProcessInstance() != null) {
            ((EventSupport) getProcessInstance().getWorkingMemory()).getRuleFlowEventSupport()
            	.fireAfterVariableChange(
        			getProcessInstance(),
        			(variableIdPrefix == null ? "" : variableIdPrefix + ":") + name,
        			(variableInstanceIdPrefix == null? "" : variableInstanceIdPrefix + ":") + name,
        			variables.get(name),
        			getProcessInstance().getWorkingMemory());
        }
    }
    
    public VariableScope getVariableScope() {
    	return (VariableScope) getContext();
    }
    
    public void setContextInstanceContainer(ContextInstanceContainer contextInstanceContainer) {
    	super.setContextInstanceContainer(contextInstanceContainer);
    	for (Variable variable : getVariableScope().getVariables()) {
            setVariable(variable.getName(), variable.getValue());
        }
    	if (contextInstanceContainer instanceof CompositeContextNodeInstance) {
    		this.variableIdPrefix = ((Node) ((CompositeContextNodeInstance) contextInstanceContainer).getNode()).getUniqueId();
    		this.variableInstanceIdPrefix = ((CompositeContextNodeInstance) contextInstanceContainer).getUniqueId();
    	}
    }

}
