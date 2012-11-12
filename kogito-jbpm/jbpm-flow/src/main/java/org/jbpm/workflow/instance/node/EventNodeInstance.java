/**
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

package org.jbpm.workflow.instance.node;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.kie.runtime.process.NodeInstance;

/**
 * Runtime counterpart of an event node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class EventNodeInstance extends ExtendedNodeInstanceImpl implements EventNodeInstanceInterface {

    private static final long serialVersionUID = 510l;

    public void signalEvent(String type, Object event) {
    	String variableName = getEventNode().getVariableName();
    	if (variableName != null) {
    		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
    			resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
    		if (variableScopeInstance == null) {
    			throw new IllegalArgumentException(
					"Could not find variable for event node: " + variableName);
    		}
    		EventTransformer transformer = getEventNode().getEventTransformer();
    		if (transformer != null) {
    			event = transformer.transformEvent(event);
    		}
    		variableScopeInstance.setVariable(variableName, event);
    	}
    	triggerCompleted();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
    	if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An EventNode only accepts default incoming connections!");
        }
        // Do nothing, event activated
    }
    
    public EventNode getEventNode() {
        return (EventNode) getNode();
    }

    public void triggerCompleted() {
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
}
