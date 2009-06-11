package org.drools.workflow.instance.node;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.common.InternalRuleBase;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.EventListener;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.EventNodeInterface;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceFactory;
import org.drools.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a composite node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class CompositeNodeInstance extends NodeInstanceImpl implements NodeInstanceContainer, EventNodeInstanceInterface, EventBasedNodeInstanceInterface {

    private static final long serialVersionUID = 400L;
    
    private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();;
    private long nodeInstanceCounter = 0;
    
    public void setProcessInstance(WorkflowProcessInstance processInstance) {
    	super.setProcessInstance(processInstance);
    	registerExternalEventNodeListeners();
    }
    
    private void registerExternalEventNodeListeners() {
    	for (Node node: getCompositeNode().getNodes()) {
			if (node instanceof EventNode) {
				if ("external".equals(((EventNode) node).getScope())) {
					getProcessInstance().addEventListener(
							((EventNode) node).getType(), new EventListener() {
						public String[] getEventTypes() {
							return null;
						}
						public void signalEvent(String type, Object event) {
						}
					}, true);
				}
			}
    	}
    }
    
    protected CompositeNode getCompositeNode() {
        return (CompositeNode) getNode();
    }
    
    public NodeContainer getNodeContainer() {
        return getCompositeNode();
    }
    
    public void internalTrigger(final org.drools.runtime.process.NodeInstance from, String type) {
        CompositeNode.NodeAndType nodeAndType = getCompositeNode().internalGetLinkedIncomingNode(type);
        List<Connection> connections = nodeAndType.getNode().getIncomingConnections(nodeAndType.getType());
        for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext(); ) {
            Connection connection = iterator.next();
            if ((connection.getFrom() instanceof CompositeNode.CompositeNodeStart) &&
            		(from == null || 
    				((CompositeNode.CompositeNodeStart) connection.getFrom()).getInNode().getId() == from.getNodeId())) {
                NodeInstance nodeInstance = getNodeInstance(connection.getFrom());
                ((org.drools.workflow.instance.NodeInstance) nodeInstance).trigger(null, nodeAndType.getType());
                return;
            }
        }
        throw new IllegalArgumentException(
            "Could not find start for composite node: " + type);
    }

    public void triggerCompleted(String outType) {
        triggerCompleted(outType, true);
        while (!nodeInstances.isEmpty()) {
            NodeInstance nodeInstance = (NodeInstance) nodeInstances.get(0);
            ((org.drools.workflow.instance.NodeInstance) nodeInstance).cancel();
        }
    }

    public void cancel() {
        while (!nodeInstances.isEmpty()) {
            NodeInstance nodeInstance = (NodeInstance) nodeInstances.get(0);
            ((org.drools.workflow.instance.NodeInstance) nodeInstance).cancel();
        }
        super.cancel();
    }
    
    public void addNodeInstance(final NodeInstance nodeInstance) {
        ((NodeInstanceImpl) nodeInstance).setId(nodeInstanceCounter++);
        this.nodeInstances.add(nodeInstance);
    }

    public void removeNodeInstance(final NodeInstance nodeInstance) {
        this.nodeInstances.remove(nodeInstance);
    }

    public Collection<org.drools.runtime.process.NodeInstance> getNodeInstances() {
        return new ArrayList(getNodeInstances(false));
    }
    
    public Collection<NodeInstance> getNodeInstances(boolean recursive) {
        Collection<NodeInstance> result = nodeInstances;
        if (recursive) {
            result = new ArrayList<NodeInstance>(result);
            for (Iterator<NodeInstance> iterator = nodeInstances.iterator(); iterator.hasNext(); ) {
                NodeInstance nodeInstance = iterator.next();
                if (nodeInstance instanceof NodeInstanceContainer) {
                    result.addAll(((NodeInstanceContainer)
                		nodeInstance).getNodeInstances(true));
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public NodeInstance getFirstNodeInstance(final long nodeId) {
        for ( final Iterator<NodeInstance> iterator = this.nodeInstances.iterator(); iterator.hasNext(); ) {
            final NodeInstance nodeInstance = iterator.next();
            if ( nodeInstance.getNodeId() == nodeId ) {
                return nodeInstance;
            }
        }
        return null;
    }
    
    public NodeInstance getNodeInstance(final Node node) {
        // TODO do this cleaner for start / end of composite?
        if (node instanceof CompositeNode.CompositeNodeStart) {
            CompositeNodeStartInstance nodeInstance = new CompositeNodeStartInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(this);
            nodeInstance.setProcessInstance(getProcessInstance());
            return nodeInstance;
        } else if (node instanceof CompositeNode.CompositeNodeEnd) {
            CompositeNodeEndInstance nodeInstance = new CompositeNodeEndInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(this);
            nodeInstance.setProcessInstance(getProcessInstance());
            return nodeInstance;
        }
        
        NodeInstanceFactoryRegistry nodeRegistry =
            ((InternalRuleBase) ((ProcessInstance) getProcessInstance()).getWorkingMemory().getRuleBase())
                .getConfiguration().getProcessNodeInstanceFactoryRegistry();
        NodeInstanceFactory conf = nodeRegistry.getProcessNodeInstanceFactory(node);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(node, getProcessInstance(), this);
        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        return nodeInstance;
    }

	public void signalEvent(String type, Object event) {
		for (Node node: getCompositeNode().getNodes()) {
			if (node instanceof EventNodeInterface) {
				if (((EventNodeInterface) node).acceptsEvent(type, event)) {
					EventNodeInstanceInterface eventNodeInstance = (EventNodeInstanceInterface) getNodeInstance(node);
					eventNodeInstance.signalEvent(type, event);
				}
			}
		}
	}

    public class CompositeNodeStartInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 400L;

        public CompositeNode.CompositeNodeStart getCompositeNodeStart() {
            return (CompositeNode.CompositeNodeStart) getNode();
        }
        
        public void internalTrigger(org.drools.runtime.process.NodeInstance from, String type) {
            triggerCompleted();
        }
        
        public void triggerCompleted() {
            triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
        }
        
    }

    public class CompositeNodeEndInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 400L;

        public CompositeNode.CompositeNodeEnd getCompositeNodeEnd() {
            return (CompositeNode.CompositeNodeEnd) getNode();
        }
        
        public void internalTrigger(org.drools.runtime.process.NodeInstance from, String type) {
            triggerCompleted();
        }
        
        public void triggerCompleted() {
            CompositeNodeInstance.this.triggerCompleted(
                getCompositeNodeEnd().getOutType());
        }
        
    }

	public void addEventListeners() {
		for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
                ((EventBasedNodeInstanceInterface) nodeInstance).addEventListeners();
            }
        }
	}

	public void removeEventListeners() {
		for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
                ((EventBasedNodeInstanceInterface) nodeInstance).removeEventListeners();
            }
        }
	}

	public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
		throw new IllegalArgumentException(
			"Completing a node instance that has no outgoing connection not supported.");
	}

}