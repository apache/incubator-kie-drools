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

import org.drools.core.common.InternalAgenda;
import org.drools.core.spi.Activation;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.kie.api.definition.process.Connection;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;

public class StateNodeInstance extends CompositeContextNodeInstance implements EventListener {

	private static final long serialVersionUID = 510l;

    protected StateNode getStateNode() {
        return (StateNode) getNode();
    }
    
	public void internalTrigger(NodeInstance from, String type) {
		super.internalTrigger(from, type);
		// if node instance was cancelled, abort
		if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
			return;
		}        
		// TODO: composite states trigger
        StateNode stateNode = getStateNode();
        Connection selected = null;
        int priority = Integer.MAX_VALUE;
        for (Connection connection: stateNode.getOutgoingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE)) {
            Constraint constraint = stateNode.getConstraint(connection);
            if (constraint != null && constraint.getPriority() < priority) {
	            String rule = "RuleFlowStateNode-" + getProcessInstance().getProcessId() + "-" + 
	            	getStateNode().getUniqueId() + "-" + 
	            	connection.getTo().getId() + "-" + 
	            	connection.getToType();
		        boolean isActive = ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
		            .isRuleActiveInRuleFlowGroup("DROOLS_SYSTEM", rule, getProcessInstance().getId());
		        if (isActive) {
		            selected = connection;
	                priority = constraint.getPriority();
	            }
            }
        }
        if (selected != null) {
            ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            triggerConnection(selected);
        } else {
            addTriggerListener();
            addActivationListener();
        }
	}
	
    protected boolean isLinkedIncomingNodeRequired() {
    	return false;
    }
    
	public void signalEvent(String type, Object event) {
		if ("signal".equals(type)) {
			if (event instanceof String) {
				for (Connection connection: getStateNode().getOutgoingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE)) {
					boolean selected = false;
					Constraint constraint = getStateNode().getConstraint(connection);
					if (constraint == null) {
						if (((String) event).equals(connection.getTo().getName())) {
							selected = true;
						}
					} else if (((String) event).equals(constraint.getName())) {
						selected = true;
					}
					if (selected) {
						triggerEvent(ExtendedNodeImpl.EVENT_NODE_EXIT);
						removeEventListeners();
						((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
		            		.removeNodeInstance(this);
						triggerConnection(connection);
						return;
					}
				}
			}
		} else if (getActivationEventType().equals(type)) {
			if (event instanceof MatchCreatedEvent) {
				activationCreated((MatchCreatedEvent) event);
			}
		} else {
			super.signalEvent(type, event);
		}
	}
	
	private void addTriggerListener() {
		getProcessInstance().addEventListener("signal", this, false);
	}

    private void addActivationListener() {
    	getProcessInstance().addEventListener(getActivationEventType(), this, true);
    }

    public void addEventListeners() {
        super.addEventListeners();
        addTriggerListener();
        addActivationListener();
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("signal", this, false);
        getProcessInstance().removeEventListener(getActivationEventType(), this, true);
    }

    public String[] getEventTypes() {
    	return new String[] { "signal", getActivationEventType() };
    }
    
    private String getActivationEventType() {
    	return "RuleFlowStateNode-" + getProcessInstance().getProcessId()
    		+ "-" + getStateNode().getUniqueId();
    }
    
    public void activationCreated(MatchCreatedEvent event) {
        Connection selected = null;
        for (Connection connection: getNode().getOutgoingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE)) {
            Constraint constraint = getStateNode().getConstraint(connection);
            if (constraint != null) {
	            String constraintName =  getActivationEventType() + "-"
	            	+ connection.getTo().getId() + "-" + connection.getToType();
	            if (constraintName.equals(event.getMatch().getRule().getName())
	            		&& checkProcessInstance((Activation) event.getMatch())) {
	            	selected = connection;
	            }
            }
        }
        if (selected != null) {
        	removeEventListeners();
        	((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            triggerConnection(selected);
        }
    }

}
