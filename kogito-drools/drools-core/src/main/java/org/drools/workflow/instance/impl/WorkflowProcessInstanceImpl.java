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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.common.EventSupport;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.WorkflowProcess;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.EventNodeInterface;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.node.EndNodeInstance;
import org.drools.workflow.instance.node.EventBasedNodeInstanceInterface;
import org.drools.workflow.instance.node.EventNodeInstance;
import org.drools.workflow.instance.node.EventNodeInstanceInterface;
import org.drools.workflow.instance.node.StateBasedNodeInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl
		implements WorkflowProcessInstance,
		org.drools.workflow.instance.NodeInstanceContainer {

	private static final long serialVersionUID = 400L;

	private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();;
	private long nodeInstanceCounter = 0;
	private Map<String, List<EventListener>> eventListeners = new HashMap<String, List<EventListener>>();
	private Map<String, List<EventListener>> externalEventListeners = new HashMap<String, List<EventListener>>();

	public NodeContainer getNodeContainer() {
		return getWorkflowProcess();
	}

	public void addNodeInstance(final NodeInstance nodeInstance) {
		((NodeInstanceImpl) nodeInstance).setId(nodeInstanceCounter++);
		this.nodeInstances.add(nodeInstance);
	}

	public void removeNodeInstance(final NodeInstance nodeInstance) {
		if (((NodeInstanceImpl) nodeInstance).isInversionOfControl()) {
			getWorkingMemory().retract(
					getWorkingMemory().getFactHandle(nodeInstance));
		}
		this.nodeInstances.remove(nodeInstance);
	}

	public Collection<org.drools.runtime.process.NodeInstance> getNodeInstances() {
		return new ArrayList<org.drools.runtime.process.NodeInstance>(getNodeInstances(false));
	}

	public Collection<NodeInstance> getNodeInstances(boolean recursive) {
		Collection<NodeInstance> result = nodeInstances;
		if (recursive) {
			result = new ArrayList<NodeInstance>(result);
			for (Iterator<NodeInstance> iterator = nodeInstances.iterator(); iterator
					.hasNext();) {
				NodeInstance nodeInstance = iterator.next();
				if (nodeInstance instanceof NodeInstanceContainer) {
					result
							.addAll(((org.drools.workflow.instance.NodeInstanceContainer) nodeInstance)
									.getNodeInstances(true));
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

	public List<String> getActiveNodeIds() {
		List<String> result = new ArrayList<String>();
		addActiveNodeIds(this, result);
		return result;
	}
	
	private void addActiveNodeIds(NodeInstanceContainer container, List<String> result) {
		for (org.drools.runtime.process.NodeInstance nodeInstance: container.getNodeInstances()) {
			result.add(((NodeImpl) ((NodeInstanceImpl) nodeInstance).getNode()).getUniqueId());
			if (nodeInstance instanceof NodeInstanceContainer) {
				addActiveNodeIds((NodeInstanceContainer) nodeInstance, result);
			}
		}
	}

	public NodeInstance getFirstNodeInstance(final long nodeId) {
		for (final Iterator<NodeInstance> iterator = this.nodeInstances
				.iterator(); iterator.hasNext();) {
			final NodeInstance nodeInstance = iterator.next();
			if (nodeInstance.getNodeId() == nodeId) {
				return nodeInstance;
			}
		}
		return null;
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

	public NodeInstance getNodeInstance(final Node node) {
		NodeInstanceFactoryRegistry nodeRegistry = ((InternalRuleBase) getWorkingMemory()
				.getRuleBase()).getConfiguration()
				.getProcessNodeInstanceFactoryRegistry();
		NodeInstanceFactory conf = nodeRegistry
				.getProcessNodeInstanceFactory(node);
		if (conf == null) {
			throw new IllegalArgumentException("Illegal node type: "
					+ node.getClass());
		}
		NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf
				.getNodeInstance(node, this, this);
		if (nodeInstance == null) {
			throw new IllegalArgumentException("Illegal node type: "
					+ node.getClass());
		}
		if (((NodeInstanceImpl) nodeInstance).isInversionOfControl()) {
			getWorkingMemory().insert(nodeInstance);
		}
		return nodeInstance;
	}


	public long getNodeInstanceCounter() {
		return nodeInstanceCounter;
	}

	public void internalSetNodeInstanceCounter(long nodeInstanceCounter) {
		this.nodeInstanceCounter = nodeInstanceCounter;
	}

	public WorkflowProcess getWorkflowProcess() {
		return (WorkflowProcess) getProcess();
	}
	
	public Object getVariable(String name) {
		// for disconnected process instances, try going through the variable scope instances
		// (as the default variable scope cannot be retrieved as the link to the process could
		// be null and the associated working memory is no longer accessible)
		if (getWorkingMemory() == null) {
			List<ContextInstance> variableScopeInstances = 
				getContextInstances(VariableScope.VARIABLE_SCOPE);
			if (variableScopeInstances != null && variableScopeInstances.size() == 1) {
				for (ContextInstance contextInstance: variableScopeInstances) {
					Object value = ((VariableScopeInstance) contextInstance).getVariable(name);
					if (value != null) {
						return value;
					}
				}
			}
			return null;
		}
		// else retrieve the variable scope
		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
			getContextInstance(VariableScope.VARIABLE_SCOPE);
		if (variableScopeInstance == null) {
			return null;
		}
		return variableScopeInstance.getVariable(name);
	}
	
	public Map<String, Object> getVariables() {
        // for disconnected process instances, try going through the variable scope instances
        // (as the default variable scope cannot be retrieved as the link to the process could
        // be null and the associated working memory is no longer accessible)
        if (getWorkingMemory() == null) {
            List<ContextInstance> variableScopeInstances = 
                getContextInstances(VariableScope.VARIABLE_SCOPE);
            if (variableScopeInstances == null) {
                return null;
            }
            Map<String, Object> result = new HashMap<String, Object>();
            for (ContextInstance contextInstance: variableScopeInstances) {
                Map<String, Object> variables = 
                    ((VariableScopeInstance) contextInstance).getVariables();
                result.putAll(variables);
            }
            return result;
        }
        // else retrieve the variable scope
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScopeInstance == null) {
            return null;
        }
        return variableScopeInstance.getVariables();
	}
	
	public void setVariable(String name, Object value) {
		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
			getContextInstance(VariableScope.VARIABLE_SCOPE);
		if (variableScopeInstance == null) {
			throw new IllegalArgumentException("No variable scope found.");
		}
		variableScopeInstance.setVariable(name, value);
	}

	public void setState(final int state) {
		super.setState(state);
		// TODO move most of this to ProcessInstanceImpl
		if (state == ProcessInstance.STATE_COMPLETED
				|| state == ProcessInstance.STATE_ABORTED) {
			InternalWorkingMemory workingMemory = (InternalWorkingMemory) getWorkingMemory();
			((EventSupport) getWorkingMemory()).getRuleFlowEventSupport()
					.fireBeforeRuleFlowProcessCompleted(this, workingMemory);
			// deactivate all node instances of this process instance
			while (!nodeInstances.isEmpty()) {
				NodeInstance nodeInstance = nodeInstances.get(0);
				((org.drools.workflow.instance.NodeInstance) nodeInstance)
						.cancel();
			}
			removeEventListeners();
			workingMemory.removeProcessInstance(this);
			((EventSupport) workingMemory).getRuleFlowEventSupport()
					.fireAfterRuleFlowProcessCompleted(this, workingMemory);

			String type = "processInstanceCompleted:" + getId();
			workingMemory.getSignalManager().signalEvent(type, this);
		}
	}

	public void disconnect() {
		removeEventListeners();
		for (NodeInstance nodeInstance : nodeInstances) {
			if (nodeInstance instanceof StateBasedNodeInstance) {
				((StateBasedNodeInstance) nodeInstance).removeEventListeners();
			}
		}
		super.disconnect();
	}

	public void reconnect() {
		super.reconnect();
		for (NodeInstance nodeInstance : nodeInstances) {
			if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
				((EventBasedNodeInstanceInterface) nodeInstance)
						.addEventListeners();
			}
		}
		addEventListeners();
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder("WorkflowProcessInstance");
		sb.append(getId());
		sb.append(" [processId=");
		sb.append(getProcessId());
		sb.append(",state=");
		sb.append(getState());
		sb.append("]");
		return sb.toString();
	}

	public void start() {
		synchronized (this) {
			registerExternalEventNodeListeners();
			super.start();
		}
	}

	private void registerExternalEventNodeListeners() {
		for (Node node : getWorkflowProcess().getNodes()) {
			if (node instanceof EventNode) {
				if ("external".equals(((EventNode) node).getScope())) {
					addEventListener(((EventNode) node).getType(),
							new EventListener() {
								public String[] getEventTypes() {
									return null;
								}

								public void signalEvent(String type,
										Object event) {
								}
							}, true);
				}
			}
		}
	}

	public void signalEvent(String type, Object event) {
		synchronized (this) {
			List<EventListener> listeners = eventListeners.get(type);
			if (listeners != null) {
				for (EventListener listener : listeners) {
					listener.signalEvent(type, event);
				}
			}
			listeners = externalEventListeners.get(type);
			if (listeners != null) {
				for (EventListener listener : listeners) {
					listener.signalEvent(type, event);
				}
			}
			for (Node node : getWorkflowProcess().getNodes()) {
				if (node instanceof EventNodeInterface) {
					if (((EventNodeInterface) node).acceptsEvent(type, event)) {
						if (node instanceof EventNode && ((EventNode) node).getFrom() == null) {
							EventNodeInstance eventNodeInstance = (EventNodeInstance) getNodeInstance(node);
							eventNodeInstance.signalEvent(type, event);
						} else {
							List<NodeInstance> nodeInstances = getNodeInstances(node
									.getId());
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
	}

	public void addEventListener(String type, EventListener listener,
			boolean external) {
		Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners
				: this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<EventListener>();
			eventListeners.put(type, listeners);
			if (external) {
				getWorkingMemory().getSignalManager().addEventListener(type,
						this);
			}
		}
		listeners.add(listener);
	}

	public void removeEventListener(String type, EventListener listener,
			boolean external) {
		Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners
				: this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				eventListeners.remove(type);
				if (external) {
					getWorkingMemory().getSignalManager().removeEventListener(
							type, this);
				}
			}
		}
	}

	private void addEventListeners() {
		registerExternalEventNodeListeners();
	}

	private void removeEventListeners() {
		for (String type : externalEventListeners.keySet()) {
			getWorkingMemory().getSignalManager().removeEventListener(type,
					this);
		}
	}

	public String[] getEventTypes() {
		return externalEventListeners.keySet().toArray(
				new String[externalEventListeners.size()]);
	}
	
	public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
        if (nodeInstance instanceof EndNodeInstance) {
            if (((org.drools.workflow.core.WorkflowProcess) getProcess()).isAutoComplete()) {
                if (nodeInstances.isEmpty()) {
                    setState(ProcessInstance.STATE_COMPLETED);
                }
            }
        } else {
    		throw new IllegalArgumentException(
    			"Completing a node instance that has no outgoing connection not suppoerted.");
        }
	}

}
