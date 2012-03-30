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

import java.util.List;

import org.drools.runtime.process.NodeInstance;
import org.drools.spi.ProcessContext;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of an event node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class EventNodeInstance extends NodeInstanceImpl implements EventNodeInstanceInterface {

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
        List<DroolsAction> actions = getEventNode().getActions(EndNode.EVENT_NODE_EXIT);
        if (actions != null) {
            for (DroolsAction droolsAction: actions) {
                executeAction(droolsAction);
            }
        }
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }
    
    protected void executeAction(DroolsAction droolsAction) {
        Action action = (Action) droolsAction.getMetaData("Action");
        ProcessContext context = new ProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        try {
            action.execute(context);
        } catch (Exception exception) {
            exception.printStackTrace();
            String exceptionName = exception.getClass().getName();
            ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
                resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
            if (exceptionScopeInstance == null) {
                exception.printStackTrace();
                throw new IllegalArgumentException(
                    "Could not find exception handler for " + exceptionName + " while executing node " + getNodeId());
            }
            exceptionScopeInstance.handleException(exceptionName, exception);
        }
    }
}
