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

package org.drools.workflow.instance.impl;

/*
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.process.core.Context;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.exclusive.ExclusiveGroup;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.node.CompositeNodeInstance;

/**
 * Default implementation of a RuleFlow node instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class NodeInstanceImpl implements org.drools.workflow.instance.NodeInstance, Serializable {

	private static final long serialVersionUID = 4L;
	
	private long id;
    private long nodeId;
    private WorkflowProcessInstance processInstance;
    private org.drools.workflow.instance.NodeInstanceContainer nodeInstanceContainer;

    public void setId(final long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setNodeId(final long nodeId) {
        this.nodeId = nodeId;
    }

    public long getNodeId() {
        return this.nodeId;
    }
    
    public String getNodeName() {
    	Node node = getNode();
    	return node == null ? "" : node.getName();
    }

    public void setProcessInstance(final WorkflowProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public WorkflowProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    public NodeInstanceContainer getNodeInstanceContainer() {
        return this.nodeInstanceContainer;
    }
    
    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = (org.drools.workflow.instance.NodeInstanceContainer) nodeInstanceContainer;
        if (nodeInstanceContainer != null) {
            this.nodeInstanceContainer.addNodeInstance(this);
        }
    }

    public Node getNode() {
        return ((org.drools.workflow.core.NodeContainer)
    		this.nodeInstanceContainer.getNodeContainer()).internalGetNode( this.nodeId );
    }
    
    public boolean isInversionOfControl() {
        return false;
    }
    
    public void cancel() {
        nodeInstanceContainer.removeNodeInstance(this);
    }
    
    public final void trigger(NodeInstance from, String type) {
    	boolean hidden = false;
    	if (getNode().getMetaData("hidden") != null) {
    		hidden = true;
    	}
    	WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
    	if (!hidden) {
    		((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(this, (InternalWorkingMemory) workingMemory);
    	}
        internalTrigger(from, type);
        if (!hidden) {
            ((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(this, (InternalWorkingMemory) workingMemory);
        }
    }
    
    public abstract void internalTrigger(NodeInstance from, String type);
    
    protected void triggerCompleted(String type, boolean remove) {
        if (remove) {
            ((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
            	.removeNodeInstance(this);
        }
        Node node = getNode();
        List<Connection> connections = null;
        if (node != null) {
        	connections = node.getOutgoingConnections(type);
        }
        if (connections == null || connections.isEmpty()) {
        	((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
        		.nodeInstanceCompleted(this, type);
        } else {
	        for (Connection connection: connections) {
	        	// stop if this process instance has been aborted / completed
	        	if (getProcessInstance().getState() != ProcessInstance.STATE_ACTIVE) {
	        		return;
	        	}
	    		triggerConnection(connection);
	        }
        }
    }
    
    protected void triggerConnection(Connection connection) {
    	boolean hidden = false;
    	if (getNode().getMetaData("hidden") != null) {
    		hidden = true;
    	}
    	WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
    	if (!hidden) {
    		((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
    	}
    	// check for exclusive group first
    	NodeInstanceContainer parent = getNodeInstanceContainer();
    	if (parent instanceof ContextInstanceContainer) {
    		List<ContextInstance> contextInstances = ((ContextInstanceContainer) parent).getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
    		if (contextInstances != null) {
    			for (ContextInstance contextInstance: new ArrayList<ContextInstance>(contextInstances)) {
    				ExclusiveGroupInstance groupInstance = (ExclusiveGroupInstance) contextInstance;
    				if (groupInstance.containsNodeInstance(this)) {
    					for (NodeInstance nodeInstance: groupInstance.getNodeInstances()) {
    						if (nodeInstance != this) {
    							((org.drools.workflow.instance.NodeInstance) nodeInstance).cancel();
    						}
    					}
    					((ContextInstanceContainer) parent).removeContextInstance(ExclusiveGroup.EXCLUSIVE_GROUP, contextInstance);
    				}
    				
    			}
    		}
    	}
    	// trigger next node
        ((org.drools.workflow.instance.NodeInstance) ((org.drools.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
        	.getNodeInstance(connection.getTo())).trigger(this, connection.getToType());
        if (!hidden) {
            ((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeLeft(this, (InternalWorkingMemory) workingMemory);
        }
    }
    
    public Context resolveContext(String contextId, Object param) {
        return ((NodeImpl) getNode()).resolveContext(contextId, param);
    }
    
    public ContextInstance resolveContextInstance(String contextId, Object param) {
        Context context = resolveContext(contextId, param);
        if (context == null) {
            return null;
        }
        ContextInstanceContainer contextInstanceContainer
        	= getContextInstanceContainer(context.getContextContainer());
        if (contextInstanceContainer == null) {
        	throw new IllegalArgumentException(
    			"Could not find context instance container for context");
        }
        return contextInstanceContainer.getContextInstance(context);
    }
    
    private ContextInstanceContainer getContextInstanceContainer(ContextContainer contextContainer) {
    	ContextInstanceContainer contextInstanceContainer = null; 
		if (this instanceof ContextInstanceContainer) {
        	contextInstanceContainer = (ContextInstanceContainer) this;
        } else {
        	contextInstanceContainer = getEnclosingContextInstanceContainer(this);
        }
        while (contextInstanceContainer != null) {
    		if (contextInstanceContainer.getContextContainer() == contextContainer) {
    			return contextInstanceContainer;
    		}
    		contextInstanceContainer = getEnclosingContextInstanceContainer(
				(NodeInstance) contextInstanceContainer);
    	}
        return null;
    }
    
    private ContextInstanceContainer getEnclosingContextInstanceContainer(NodeInstance nodeInstance) {
    	NodeInstanceContainer nodeInstanceContainer = nodeInstance.getNodeInstanceContainer();
    	while (true) {
    		if (nodeInstanceContainer instanceof ContextInstanceContainer) {
    			return (ContextInstanceContainer) nodeInstanceContainer;
    		}
    		if (nodeInstanceContainer instanceof NodeInstance) {
    			nodeInstanceContainer = ((NodeInstance) nodeInstanceContainer).getNodeInstanceContainer();
    		} else {
    			return null;
    		}
    	}
    }
    
    public String getUniqueId() {
    	String result = "" + getId();
    	NodeInstanceContainer parent = getNodeInstanceContainer();
    	while (parent instanceof CompositeNodeInstance) {
    		CompositeNodeInstance nodeInstance = (CompositeNodeInstance) parent;
    		result = nodeInstance.getId() + ":" + result;
    		parent = nodeInstance.getNodeInstanceContainer();
    	}
    	return result;
    }
    
}
