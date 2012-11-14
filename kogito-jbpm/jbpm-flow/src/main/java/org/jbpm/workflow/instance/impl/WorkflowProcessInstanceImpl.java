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

package org.jbpm.workflow.instance.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.common.InternalKnowledgeRuntime;
import org.kie.definition.process.Node;
import org.kie.definition.process.NodeContainer;
import org.kie.definition.process.WorkflowProcess;
import org.kie.runtime.process.EventListener;
import org.kie.runtime.process.NodeInstanceContainer;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.EndNodeInstance;
import org.jbpm.workflow.instance.node.EventBasedNodeInstanceInterface;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstanceInterface;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;

/**
 * Default implementation of a RuleFlow process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl
		implements WorkflowProcessInstance,
		org.jbpm.workflow.instance.NodeInstanceContainer {

	private static final long serialVersionUID = 510l;

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
			getKnowledgeRuntime().retract(
					getKnowledgeRuntime().getFactHandle(nodeInstance));
		}
		this.nodeInstances.remove(nodeInstance);
	}

	public Collection<org.kie.runtime.process.NodeInstance> getNodeInstances() {
		return new ArrayList<org.kie.runtime.process.NodeInstance>(getNodeInstances(false));
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
							.addAll(((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstance)
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
		for (org.kie.runtime.process.NodeInstance nodeInstance: container.getNodeInstances()) {
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
		NodeInstanceFactory conf = NodeInstanceFactoryRegistry.INSTANCE.getProcessNodeInstanceFactory(node);
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
			getKnowledgeRuntime().insert(nodeInstance);
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
		if (getKnowledgeRuntime() == null) {
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
        if (getKnowledgeRuntime() == null) {
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
	
	public void setState(final int state, String outcome) {
	    super.setState(state, outcome);
        // TODO move most of this to ProcessInstanceImpl
        if (state == ProcessInstance.STATE_COMPLETED
                || state == ProcessInstance.STATE_ABORTED) {
            InternalKnowledgeRuntime kruntime = getKnowledgeRuntime();
            InternalProcessRuntime processRuntime = (InternalProcessRuntime) kruntime.getProcessRuntime();
            processRuntime.getProcessEventSupport().fireBeforeProcessCompleted(this, kruntime);
            // deactivate all node instances of this process instance
            while (!nodeInstances.isEmpty()) {
                NodeInstance nodeInstance = nodeInstances.get(0);
                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
                        .cancel();
            }
            removeEventListeners();
            processRuntime.getProcessInstanceManager().removeProcessInstance(this);
            processRuntime.getProcessEventSupport().fireAfterProcessCompleted(this, kruntime);

            processRuntime.getSignalManager().signalEvent("processInstanceCompleted:" + getId(), this);
        }
	}

	public void setState(final int state) {
		setState(state, null);
	}

	public void disconnect() {
		removeEventListeners();
		unregisterExternalEventNodeListeners();
		
		for (NodeInstance nodeInstance : nodeInstances) {
			if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
				((EventBasedNodeInstanceInterface) nodeInstance).removeEventListeners();
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
						new ExternalEventListener(), true);
				}
			}
		}
	}
	
	private void unregisterExternalEventNodeListeners() {
		for (Node node : getWorkflowProcess().getNodes()) {
			if (node instanceof EventNode) {
				if ("external".equals(((EventNode) node).getScope())) {
					externalEventListeners.remove(((EventNode) node).getType());
				}
			}
		}
	}

	public void signalEvent(String type, Object event) {
		synchronized (this) {
			if (getState() != ProcessInstance.STATE_ACTIVE) {
				return;
			}
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
			if (((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()) {
				for (Node node : getWorkflowProcess().getNodes()) {
					if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
		    			NodeInstance nodeInstance = getNodeInstance(node);
		                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
		                	.trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
		    		}
				}
			}
		}
	}

	public void addEventListener(String type, EventListener listener,
			boolean external) {
		Map<String, List<EventListener>> eventListeners = 
			external ? this.externalEventListeners : this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<EventListener>();
			eventListeners.put(type, listeners);
			if (external) {
				((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
					.getSignalManager().addEventListener(type, this);
			}
		}
		listeners.add(listener);
	}

	public void removeEventListener(String type, EventListener listener, boolean external) {
		Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners
				: this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				eventListeners.remove(type);
				if (external) {
					((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
						.getSignalManager().removeEventListener(type, this);
				}
			}
		}
	}

	private void addEventListeners() {
		registerExternalEventNodeListeners();
	}

	private void removeEventListeners() {
		for (String type : externalEventListeners.keySet()) {
			((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
				.getSignalManager().removeEventListener(type, this);
		}
	}

	public String[] getEventTypes() {
		return externalEventListeners.keySet().toArray(
				new String[externalEventListeners.size()]);
	}
	
	public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
        if (nodeInstance instanceof EndNodeInstance || 
        		((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()) {
            if (((org.jbpm.workflow.core.WorkflowProcess) getProcess()).isAutoComplete()) {
                if (nodeInstances.isEmpty()) {
                    setState(ProcessInstance.STATE_COMPLETED);
                }
            }
        } else {
    		throw new IllegalArgumentException(
    			"Completing a node instance that has no outgoing connection not suppoerted.");
        }
	}

	private class ExternalEventListener implements EventListener, Serializable {
		private static final long serialVersionUID = 5L;
		public String[] getEventTypes() {
			return null;
		}
		public void signalEvent(String type,
				Object event) {
		}		
	}
}
