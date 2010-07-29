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

package org.drools.workflow.instance.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.process.core.context.exclusive.ExclusiveGroup;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.impl.ConstraintEvaluator;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SplitInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    protected Split getSplit() {
        return (Split) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A Split only accepts default incoming connections!");
        }
        final Split split = getSplit();
        // TODO make different strategies for each type
        switch ( split.getType() ) {
            case Split.TYPE_AND :
                triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
                break;
            case Split.TYPE_XOR :
                List<Connection> outgoing = split.getDefaultOutgoingConnections();
                int priority = Integer.MAX_VALUE;
                Connection selected = null;
                for ( final Iterator<Connection> iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final Connection connection = (Connection) iterator.next();
                    ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
                    if ( constraint != null && constraint.getPriority() < priority && !constraint.isDefault()) {
                        if ( constraint.evaluate( this,
                                                  connection,
                                                  constraint ) ) {
                            selected = connection;
                            priority = constraint.getPriority();
                        }
                    }
                }
                ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
                if ( selected == null ) {
                	for ( final Iterator<Connection> iterator = outgoing.iterator(); iterator.hasNext(); ) {
                        final Connection connection = (Connection) iterator.next();
                        ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
                        if ( constraint.isDefault() ) {
                            selected = connection;
                            break;
                        }
                    }
                }
                if ( selected == null ) {
                	throw new IllegalArgumentException( "XOR split could not find at least one valid outgoing connection for split " + getSplit().getName() );
                }
                triggerConnection(selected);
                break;
            case Split.TYPE_OR :
            	((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
                outgoing = split.getDefaultOutgoingConnections();
                boolean found = false;
                List<Connection> outgoingCopy = new ArrayList<Connection>(outgoing);
                while (!outgoingCopy.isEmpty()) {
                    priority = Integer.MAX_VALUE;
                    Connection selectedConnection = null;
                    ConstraintEvaluator selectedConstraint = null;
                    for ( final Iterator<Connection> iterator = outgoingCopy.iterator(); iterator.hasNext(); ) {
                        final Connection connection = (Connection) iterator.next();
                        ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
    
                        if ( constraint != null  
                                && constraint.getPriority() < priority
                                && !constraint.isDefault() ) {
                            priority = constraint.getPriority();
                            selectedConnection = connection;
                            selectedConstraint = constraint;
                        }
                    }
                    if (selectedConstraint == null) {
                    	break;
                    }
                    if (selectedConstraint.evaluate( this,
                                                     selectedConnection,
                                                     selectedConstraint ) ) {
                        triggerConnection(selectedConnection);
                        found = true;
                    }
                    outgoingCopy.remove(selectedConnection);
                }
                if ( !found ) {
                	for ( final Iterator<Connection> iterator = outgoing.iterator(); iterator.hasNext(); ) {
                        final Connection connection = (Connection) iterator.next();
                        ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
                        if ( constraint.isDefault() ) {
                        	triggerConnection(connection);
                        	found = true;
                            break;
                        }
                    }
                }
                if ( !found ) {
                    throw new IllegalArgumentException( "OR split could not find at least one valid outgoing connection for split " + getSplit().getName() );
                }                
                break;
            case Split.TYPE_XAND :
            	((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
                Node node = getNode();
                List<Connection> connections = null;
                if (node != null) {
                	connections = node.getOutgoingConnections(type);
                }
                if (connections == null || connections.isEmpty()) {
                	((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
                		.nodeInstanceCompleted(this, type);
                } else {
                	ExclusiveGroupInstance groupInstance = new ExclusiveGroupInstance();
            		org.drools.runtime.process.NodeInstanceContainer parent = getNodeInstanceContainer();
                	if (parent instanceof ContextInstanceContainer) {
                		((ContextInstanceContainer) parent).addContextInstance(ExclusiveGroup.EXCLUSIVE_GROUP, groupInstance);
                	} else {
                		throw new IllegalArgumentException(
            				"An Exclusive AND is only possible if the parent is a context instance container");
                	}
                	Map<NodeInstance, String> nodeInstances = new HashMap<NodeInstance, String>();
        	        for (Connection connection: connections) {
        	        	nodeInstances.put(
    	            		((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
        	            		.getNodeInstance(connection.getTo()),
    	            		connection.getToType());
        	        }
        	        for (NodeInstance nodeInstance: nodeInstances.keySet()) {
        	        	groupInstance.addNodeInstance(nodeInstance);
        	        }
        	        for (Map.Entry<NodeInstance, String> entry: nodeInstances.entrySet()) {
        	        	// stop if this process instance has been aborted / completed
        	        	if (getProcessInstance().getState() != ProcessInstance.STATE_ACTIVE) {
        	        		return;
        	        	}
        	        	boolean hidden = false;
        	        	if (getNode().getMetaData("hidden") != null) {
        	        		hidden = true;
        	        	}
        	        	WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
        	        	if (!hidden) {
        	        		((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
        	        	}
        	            ((org.drools.workflow.instance.NodeInstance) entry.getKey())
        	        		.trigger(this, entry.getValue());
        	            if (!hidden) {
        	                ((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
        	            }
        	        }
                }
                break;
            default :
                throw new IllegalArgumentException( "Illegal split type " + split.getType() );
        }
    }
}
