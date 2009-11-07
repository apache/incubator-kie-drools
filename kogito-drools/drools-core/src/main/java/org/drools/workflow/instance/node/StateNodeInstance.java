package org.drools.workflow.instance.node;

import java.util.Iterator;
import java.util.Map;

import org.drools.common.AbstractWorkingMemory;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Connection;
import org.drools.event.ActivationCreatedEvent;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Declaration;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstance;
import org.drools.spi.Activation;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ExtendedNodeImpl;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.instance.NodeInstanceContainer;

public class StateNodeInstance extends CompositeContextNodeInstance implements EventListener {

	private static final long serialVersionUID = 4L;

    protected StateNode getStateNode() {
        return (StateNode) getNode();
    }
    
	public void internalTrigger(NodeInstance from, String type) {
		super.internalTrigger(from, type);
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
		        boolean isActive = ((InternalAgenda) getProcessInstance().getAgenda())
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
						((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
		            		.removeNodeInstance(this);
						triggerConnection(connection);
						return;
					}
				}
			}
		} else if (getActivationEventType().equals(type)) {
			if (event instanceof ActivationCreatedEvent) {
				activationCreated((ActivationCreatedEvent) event);
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
    
    private boolean checkProcessInstance(Activation activation) {
    	final Map<?, ?> declarations = activation.getSubRule().getOuterDeclarations();
        for ( Iterator<?> it = declarations.values().iterator(); it.hasNext(); ) {
            Declaration declaration = (Declaration) it.next();
            if ("processInstance".equals(declaration.getIdentifier())) {
            	Object value = declaration.getValue(
        			(InternalWorkingMemory) getProcessInstance().getWorkingMemory(),
        			((InternalFactHandle) activation.getTuple().get(declaration)).getObject());
            	if (value instanceof ProcessInstance) {
            		return ((ProcessInstance) value).getId() == getProcessInstance().getId();
            	}
        	}
        }
        return true;
    }
    
    public void activationCreated(ActivationCreatedEvent event) {
        Connection selected = null;
        for (Connection connection: getNode().getOutgoingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE)) {
            Constraint constraint = getStateNode().getConstraint(connection);
            if (constraint != null) {
	            String constraintName =  getActivationEventType() + "-"
	            	+ connection.getTo().getId() + "-" + connection.getToType();
	            if (constraintName.equals(event.getActivation().getRule().getName()) && checkProcessInstance(event.getActivation())) {
	            	selected = connection;
	            }
            }
        }
        if (selected != null) {
    		if ( !((AbstractWorkingMemory) getProcessInstance().getWorkingMemory()).getActionQueue().isEmpty() ) {
    			((AbstractWorkingMemory) getProcessInstance().getWorkingMemory()).executeQueuedActions();
            }
        	removeEventListeners();
        	((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            triggerConnection(selected);
        }
    }

}
