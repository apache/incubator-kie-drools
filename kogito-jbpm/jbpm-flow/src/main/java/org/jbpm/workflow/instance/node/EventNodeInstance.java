/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import static org.jbpm.workflow.instance.impl.DummyEventListener.EMPTY_EVENT_LISTENER;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;

/**
 * Runtime counterpart of an event node.
 * 
 */
public class EventNodeInstance extends ExtendedNodeInstanceImpl implements EventNodeInstanceInterface, EventBasedNodeInstanceInterface {

    protected static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{([\\S&&[^\\}]]+)\\}", Pattern.DOTALL);

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
    	addEventListeners();
        // Do nothing, event activated
    }

    public EventNode getEventNode() {
        return (EventNode) getNode();
    }

    public void triggerCompleted() {
    	getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
        ((org.jbpm.workflow.instance.NodeInstanceContainer)getNodeInstanceContainer()).setCurrentLevel(getLevel());
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }

    @Override
	public void cancel() {
    	getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
		super.cancel();
	}

   private class VariableExternalEventListener implements EventListener, Serializable {
        private static final long serialVersionUID = 5L;

        private String eventType;

        VariableExternalEventListener(String eventType) {
            this.eventType = eventType;
        }

        public String[] getEventTypes() {
            return new String[] {eventType};
        }
        public void signalEvent(String type, Object event) {
            callSignal(type, event);
        }
    }

	@Override
	public void addEventListeners() {
	    String eventType = getEventType();
	    if (isVariableExpression(getEventNode().getType())) {
	        getProcessInstance().addEventListener(eventType, new VariableExternalEventListener(eventType), true);
	    } else {
	        getProcessInstance().addEventListener(eventType, getEventListener(), true);
	    }
	}

	@Override
	public void removeEventListeners() {


	}

	public String getEventType() {
	    return resolveVariable(getEventNode().getType());
	}

	protected EventListener getEventListener() {
	    return EMPTY_EVENT_LISTENER;
	}

	private boolean isVariableExpression(String eventType) {
	    if (eventType == null ){
	        return false;
	    }
	    Matcher matcher = PARAMETER_MATCHER.matcher(eventType);
	    if (matcher.find()) {
	        return true;
	    }

	    return false;
	}

	private String resolveVariable(String s) {
        if (s == null) {
            return null;
        }

        Map<String, String> replacements = new HashMap<String, String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                    resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                    String variableValueString = variableValue == null ? "" : variableValue.toString();
                    replacements.put(paramName, variableValueString);
                }
            }
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }

        return s;
    }

	private void callSignal(String type, Object event) {
	    signalEvent(type, event);
	}
}
