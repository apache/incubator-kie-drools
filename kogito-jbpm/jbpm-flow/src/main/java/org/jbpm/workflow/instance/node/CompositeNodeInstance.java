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

import static org.jbpm.workflow.instance.impl.DummyEventListener.EMPTY_EVENT_LISTENER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.AsyncEventNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceFactory;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;

/**
 * Runtime counterpart of a composite node.
 * 
 */
public class CompositeNodeInstance extends StateBasedNodeInstance implements NodeInstanceContainer, EventNodeInstanceInterface, EventBasedNodeInstanceInterface {

    private static final long serialVersionUID = 510l;

    private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();

    private AtomicLong singleNodeInstanceCounter = null; // set during NodeInstance creation (*NodeFactory)
    private int state = ProcessInstance.STATE_ACTIVE;
    private Map<String, Integer> iterationLevels = new HashMap<String, Integer>();
    private int currentLevel;

    @Override
    public int getLevelForNode(String uniqueID) {
        if ("true".equalsIgnoreCase(System.getProperty("jbpm.loop.level.disabled"))) {
            return 1;
        }
        Integer value = iterationLevels.get(uniqueID);
        if (value == null && currentLevel == 0) {
           value = new Integer(1);
        } else if ((value == null && currentLevel > 0) || (value != null && currentLevel > 0 && value > currentLevel)) {
            value = new Integer(currentLevel);
        } else {
            value++;
        }

        iterationLevels.put(uniqueID, value);
        return value;
    }

    public void setProcessInstance(WorkflowProcessInstance processInstance) {
    	super.setProcessInstance(processInstance);
    	this.singleNodeInstanceCounter = ((WorkflowProcessInstanceImpl) processInstance).internalGetNodeInstanceCounter();
    	registerExternalEventNodeListeners();
    }

    private void registerExternalEventNodeListeners() {
    	for (Node node: getCompositeNode().getNodes()) {
			if (node instanceof EventNode) {
				if ("external".equals(((EventNode) node).getScope())) {
					getProcessInstance().addEventListener(
						((EventNode) node).getType(), EMPTY_EVENT_LISTENER,  true);
				}
			} else if (node instanceof EventSubProcessNode) {
                List<String> events = ((EventSubProcessNode) node).getEvents();
                for (String type : events) {
                    getProcessInstance().addEventListener(type, EMPTY_EVENT_LISTENER, true);
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

    public void internalTrigger(final org.kie.api.runtime.process.NodeInstance from, String type) {
    	super.internalTrigger(from, type);
    	// if node instance was cancelled, abort
		if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
			return;
		}
        CompositeNode.NodeAndType nodeAndType = getCompositeNode().internalGetLinkedIncomingNode(type);
        if (nodeAndType != null) {
	        List<Connection> connections = nodeAndType.getNode().getIncomingConnections(nodeAndType.getType());
	        for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext(); ) {
	            Connection connection = iterator.next();
	            if ((connection.getFrom() instanceof CompositeNode.CompositeNodeStart) &&
	            		(from == null ||
	    				((CompositeNode.CompositeNodeStart) connection.getFrom()).getInNode().getId() == from.getNodeId())) {
	                NodeInstance nodeInstance = getNodeInstance(connection.getFrom());
	                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).trigger(null, nodeAndType.getType());
	                return;
	            }
	        }
        } else {
        	// try to search for start nodes
        	boolean found = false;
        	for (Node node: getCompositeNode().getNodes()) {
        		if (node instanceof StartNode) {
        			StartNode startNode = (StartNode) node;
        			if (startNode.getTriggers() == null || startNode.getTriggers().isEmpty()) {
    	                NodeInstance nodeInstance = getNodeInstance(startNode);
    	                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
    	                	.trigger(null, null);
    	                found = true;
        			}
        		}
        	}
        	if (found) {
        		return;
        	}
        }
        if (isLinkedIncomingNodeRequired()) {
	        throw new IllegalArgumentException(
	            "Could not find start for composite node: " + type);
        }
    }

    protected void internalTriggerOnlyParent(final org.kie.api.runtime.process.NodeInstance from, String type) {
        super.internalTrigger(from, type);
    }

    protected boolean isLinkedIncomingNodeRequired() {
    	return true;
    }

    public void triggerCompleted(String outType) {
    	boolean cancelRemainingInstances = getCompositeNode().isCancelRemainingInstances();
    	((org.jbpm.workflow.instance.NodeInstanceContainer)getNodeInstanceContainer()).setCurrentLevel(getLevel());
        triggerCompleted(outType, cancelRemainingInstances);
        if (cancelRemainingInstances) {
	        while (!nodeInstances.isEmpty()) {
	            NodeInstance nodeInstance = (NodeInstance) nodeInstances.get(0);
	            ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).cancel();
	        }
        }
    }

    public void cancel() {
        while (!nodeInstances.isEmpty()) {
            NodeInstance nodeInstance = (NodeInstance) nodeInstances.get(0);
            ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).cancel();
        }
        super.cancel();
    }

    public void addNodeInstance(final NodeInstance nodeInstance) {
        if (nodeInstance.getId() == -1) {
            // assign new id only if it does not exist as it might already be set by marshalling 
            // it's important to keep same ids of node instances as they might be references e.g. exclusive group
            long id = singleNodeInstanceCounter.incrementAndGet();  
            ((NodeInstanceImpl) nodeInstance).setId(id);
        }
        this.nodeInstances.add(nodeInstance);
    }

    public void removeNodeInstance(final NodeInstance nodeInstance) {
        this.nodeInstances.remove(nodeInstance);
    }

    public Collection<org.kie.api.runtime.process.NodeInstance> getNodeInstances() {
        return new ArrayList<org.kie.api.runtime.process.NodeInstance>(getNodeInstances(false));
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

	public NodeInstance getNodeInstance(long nodeInstanceId) {
		for (NodeInstance nodeInstance: nodeInstances) {
			if (nodeInstance.getId() == nodeInstanceId) {
				return nodeInstance;
			}
		}
		return null;
	}

	public NodeInstance getNodeInstance(long nodeInstanceId, boolean recursive) {
		for (NodeInstance nodeInstance: getNodeInstances(recursive)) {
			if (nodeInstance.getId() == nodeInstanceId) {
				return nodeInstance;
			}
		}
		return null;
	}

    public NodeInstance getFirstNodeInstance(final long nodeId) {
        for ( final Iterator<NodeInstance> iterator = this.nodeInstances.iterator(); iterator.hasNext(); ) {
            final NodeInstance nodeInstance = iterator.next();
            if ( nodeInstance.getNodeId() == nodeId && nodeInstance.getLevel() == getCurrentLevel()) {
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
        Node actualNode = node;
        // async continuation handling
        if (node instanceof AsyncEventNode) {
            actualNode = ((AsyncEventNode) node).getActualNode();
        } else if (useAsync(node)) {
            actualNode = new AsyncEventNode(node);
        }

        NodeInstanceFactory conf = NodeInstanceFactoryRegistry.getInstance(getProcessInstance().getKnowledgeRuntime().getEnvironment()).getProcessNodeInstanceFactory(actualNode);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(actualNode, getProcessInstance(), this);
        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: " + node.getClass());
        }
        return nodeInstance;
    }

    @Override
	public void signalEvent(String type, Object event) {
		List<NodeInstance> currentView = new ArrayList<NodeInstance>(this.nodeInstances);
		super.signalEvent(type, event);
		for (Node node: getCompositeNode().internalGetNodes()) {
			if (node instanceof EventNodeInterface) {
				if (((EventNodeInterface) node).acceptsEvent(type, event)) {
					if (node instanceof EventNode && ((EventNode) node).getFrom() == null) {
						EventNodeInstanceInterface eventNodeInstance = (EventNodeInstanceInterface) getNodeInstance(node);
						eventNodeInstance.signalEvent(type, event);
					} else if( node instanceof EventSubProcessNode ) {
					    EventNodeInstanceInterface eventNodeInstance = (EventNodeInstanceInterface) getNodeInstance(node);
					    eventNodeInstance.signalEvent(type, event);
					} else {
						List<NodeInstance> nodeInstances = getNodeInstances(node.getId(), currentView);
						if (nodeInstances != null && !nodeInstances.isEmpty()) {
							for (NodeInstance nodeInstance : nodeInstances) {
								((EventNodeInstanceInterface) nodeInstance)
										.signalEvent(type, event);
							}
						}
					}
				}
			}
		}
	}

	public List<NodeInstance> getNodeInstances(final long nodeId) {
		List<NodeInstance> result = new ArrayList<NodeInstance>();
		for (final Iterator<NodeInstance> iterator = this.nodeInstances
				.iterator(); iterator.hasNext();) {
			final NodeInstance nodeInstance = iterator.next();
			if (nodeInstance.getNodeId() == nodeId) {
				result.add(nodeInstance);
			}
		}
		return result;
	}

	public List<NodeInstance> getNodeInstances(final long nodeId, List<NodeInstance> currentView) {
		List<NodeInstance> result = new ArrayList<NodeInstance>();
		for (final Iterator<NodeInstance> iterator = currentView
				.iterator(); iterator.hasNext();) {
			final NodeInstance nodeInstance = iterator.next();
			if (nodeInstance.getNodeId() == nodeId) {
				result.add(nodeInstance);
			}
		}
		return result;
	}

    public class CompositeNodeStartInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;

        public CompositeNode.CompositeNodeStart getCompositeNodeStart() {
            return (CompositeNode.CompositeNodeStart) getNode();
        }

        public void internalTrigger(org.kie.api.runtime.process.NodeInstance from, String type) {
            triggerCompleted();
        }

        public void triggerCompleted() {
            triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
        }

    }

    public class CompositeNodeEndInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;

        public CompositeNode.CompositeNodeEnd getCompositeNodeEnd() {
            return (CompositeNode.CompositeNodeEnd) getNode();
        }

        public void internalTrigger(org.kie.api.runtime.process.NodeInstance from, String type) {
            triggerCompleted();
        }

        public void triggerCompleted() {
            CompositeNodeInstance.this.triggerCompleted(
                getCompositeNodeEnd().getOutType());
        }

    }

	public void addEventListeners() {
		super.addEventListeners();
		for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
                ((EventBasedNodeInstanceInterface) nodeInstance).addEventListeners();
            }
        }
	}

	public void removeEventListeners() {
		super.removeEventListeners();
		for (NodeInstance nodeInstance: nodeInstances) {
            if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
                ((EventBasedNodeInstanceInterface) nodeInstance).removeEventListeners();
            }
        }
	}

	public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
	    Node nodeInstanceNode = nodeInstance.getNode();
	    if( nodeInstanceNode != null ) {
	        Object compensationBoolObj =  nodeInstanceNode.getMetaData().get("isForCompensation");
	        boolean isForCompensation = compensationBoolObj == null ? false : ((Boolean) compensationBoolObj);
	        if( isForCompensation ) {
	            return;
	        }
	    }
	    if (nodeInstance instanceof FaultNodeInstance || nodeInstance instanceof EndNodeInstance || nodeInstance instanceof EventSubProcessNodeInstance ) {
            if (getCompositeNode().isAutoComplete()) {
                if (nodeInstances.isEmpty()) {
                    triggerCompleted(
                        org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                }
            }
	    } else {
    		throw new IllegalArgumentException(
    			"Completing a node instance that has no outgoing connection not supported.");
	    }
	}

    public void setState(final int state) {
        this.state = state;
        if (state == ProcessInstance.STATE_ABORTED) {
            cancel();
        }
    }

    public int getState() {
        return this.state;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Map<String, Integer> getIterationLevels() {
        return iterationLevels;
    }

    protected boolean useAsync(final Node node) {
        if (!(node instanceof EventSubProcessNode) && (node instanceof ActionNode || node instanceof StateBasedNode || node instanceof EndNode)) {  
            boolean asyncMode = Boolean.parseBoolean((String)node.getMetaData().get("customAsync"));
            if (asyncMode) {
                return asyncMode;
            }
            
            return Boolean.parseBoolean((String)getProcessInstance().getKnowledgeRuntime().getEnvironment().get("AsyncMode"));
        }
        
        return false;
    }

}
