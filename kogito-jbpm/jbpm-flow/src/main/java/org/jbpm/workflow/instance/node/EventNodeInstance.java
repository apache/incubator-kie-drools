/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.event.EventTransformer;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;

import static org.jbpm.workflow.instance.impl.DummyEventListener.EMPTY_EVENT_LISTENER;

/**
 * Runtime counterpart of an event node.
 * 
 */
public class EventNodeInstance extends ExtendedNodeInstanceImpl implements EventNodeInstanceInterface, EventBasedNodeInstanceInterface {

    private static final long serialVersionUID = 510l;

    public void signalEvent(String type, Object event) {
        if ("timerTriggered".equals(type)) {
            TimerInstance timerInstance = (TimerInstance) event;
            if (timerInstance.getId() == slaTimerId) {                
                handleSLAViolation();        
            }
        } else if (("slaViolation:" + getId()).equals(type)) {
                           
            handleSLAViolation();        
           
        } else {
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
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An EventNode only accepts default incoming connections!");
        }
    	addEventListeners();
        // Do nothing, event activated
    }
    
    protected void configureSla() {
        String slaDueDateExpression = (String) getNode().getMetaData().get("customSLADueDate");
        if (slaDueDateExpression != null) {
            TimerInstance timer = ((WorkflowProcessInstanceImpl)getProcessInstance()).configureSLATimer(slaDueDateExpression);
            if (timer != null) {
                this.slaTimerId = timer.getId();
                this.slaDueDate = new Date(System.currentTimeMillis() + timer.getDelay());
                this.slaCompliance = ProcessInstance.SLA_PENDING;
                logger.debug("SLA for node instance {} is PENDING with due date {}", this.getId(), this.slaDueDate);
            }
        }
    }
    
    protected void handleSLAViolation() {
        if (slaCompliance == ProcessInstance.SLA_PENDING) {
            InternalProcessRuntime processRuntime = ((InternalProcessRuntime) getProcessInstance().getKnowledgeRuntime().getProcessRuntime());
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
            logger.debug("SLA violated on node instance {}", getId());                   
            this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            this.slaTimerId = -1;
            processRuntime.getProcessEventSupport().fireAfterSLAViolated(getProcessInstance(), this, getProcessInstance().getKnowledgeRuntime());
        }
    }
    
    private void cancelSlaTimer() {
        if (this.slaTimerId > -1) {
            TimerManager timerManager = ((InternalProcessRuntime)
                    getProcessInstance().getKnowledgeRuntime().getProcessRuntime()).getTimerManager();
            timerManager.cancelTimer(this.slaTimerId);
            logger.debug("SLA Timer {} has been canceled", this.slaTimerId);
        }
    }
    
    protected void addTimerListener() {
        
        ((WorkflowProcessInstance) getProcessInstance()).addEventListener("timerTriggered", new VariableExternalEventListener("timerTriggered"), false);
        ((WorkflowProcessInstance) getProcessInstance()).addEventListener("timer", new VariableExternalEventListener("timer"), true);
        ((WorkflowProcessInstance) getProcessInstance()).addEventListener("slaViolation:" + getId(), new VariableExternalEventListener("slaViolation"), true);
    }
    
    public void removeTimerListeners() {
        ((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timerTriggered", new VariableExternalEventListener("timerTriggered"), false);
        ((WorkflowProcessInstance) getProcessInstance()).removeEventListener("timer", new VariableExternalEventListener("timer"), true);
        ((WorkflowProcessInstance) getProcessInstance()).removeEventListener("slaViolation:" + getId(), new VariableExternalEventListener("slaViolation"), true);
    }

    public EventNode getEventNode() {
        return (EventNode) getNode();
    }

    public void triggerCompleted() {
    	getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
    	removeTimerListeners();
    	if (this.slaCompliance == ProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the node instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = ProcessInstance.STATE_COMPLETED;
            }
        }
        cancelSlaTimer();
        ((org.jbpm.workflow.instance.NodeInstanceContainer)getNodeInstanceContainer()).setCurrentLevel(getLevel());
        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
    }

    @Override
	public void cancel() {
    	getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
    	removeTimerListeners();
        if (this.slaCompliance == ProcessInstance.SLA_PENDING) {
            if (System.currentTimeMillis() > slaDueDate.getTime()) {
                // completion of the process instance is after expected SLA due date, mark it accordingly
                this.slaCompliance = ProcessInstance.SLA_VIOLATED;
            } else {
                this.slaCompliance = ProcessInstance.SLA_ABORTED;
            }
        }
    	removeTimerListeners();
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VariableExternalEventListener other = (VariableExternalEventListener) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (eventType == null) {
                if (other.eventType != null)
                    return false;
            } else if (!eventType.equals(other.eventType))
                return false;
            return true;
        }

        private EventNodeInstance getOuterType() {
            return EventNodeInstance.this;
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
	    if (slaTimerId > -1) {
	        addTimerListener();
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
	    Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(eventType);
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
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
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
