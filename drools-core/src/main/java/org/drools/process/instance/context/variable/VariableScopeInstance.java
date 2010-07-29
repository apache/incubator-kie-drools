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

    private static final long serialVersionUID = 510l;
    
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
