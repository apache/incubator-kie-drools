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

package org.jbpm.workflow.instance.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.EndNodeInstance;
import org.jbpm.workflow.instance.node.EventBasedNodeInstanceInterface;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstanceInterface;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.FaultNodeInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationKey;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.process.BaseEventDescription;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.NamedDataType;
import org.kie.services.time.TimerInstance;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.ruleflow.core.Metadata.COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.CONDITION;
import static org.jbpm.ruleflow.core.Metadata.CORRELATION_KEY;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_ASYNC;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_SLA_DUE_DATE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.IS_FOR_COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.workflow.instance.impl.DummyEventListener.EMPTY_EVENT_LISTENER;

/**
 * Default implementation of a RuleFlow process instance.
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl
        implements WorkflowProcessInstance,
                   org.jbpm.workflow.instance.NodeInstanceContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowProcessInstanceImpl.class);

    private final List<NodeInstance> nodeInstances = new ArrayList<>();

    private Map<String, List<EventListener>> eventListeners = new HashMap<>();
    private Map<String, List<EventListener>> externalEventListeners = new HashMap<>();

    private List<String> completedNodeIds = new ArrayList<>();
    private List<String> activatingNodeIds;
    private Map<String, Integer> iterationLevels = new HashMap<>();
    private int currentLevel;

    private Object faultData;

    private boolean signalCompletion = true;

    private String deploymentId;
    private String correlationKey;

    private Date startDate;
    private Date endDate;

    private String nodeIdInError;
    private String errorMessage;

    private int slaCompliance = SLA_NA;
    private Date slaDueDate;
    private String slaTimerId;
    
    private String referenceId;

    @Override
    public NodeContainer getNodeContainer() {
        return getWorkflowProcess();
    }

    @Override
    public void addNodeInstance(final NodeInstance nodeInstance) {
        if (nodeInstance.getId() == null) {
            // assign new id only if it does not exist as it might already be set by marshalling
            // it's important to keep same ids of node instances as they might be references e.g. exclusive group
            ((NodeInstanceImpl) nodeInstance).setId(UUID.randomUUID().toString());
        }
        this.nodeInstances.add(nodeInstance);
    }

    @Override
    public int getLevelForNode(String uniqueID) {
        if (Boolean.parseBoolean(System.getProperty("jbpm.loop.level.disabled"))) {
            return 1;
        }

        Integer value = iterationLevels.get(uniqueID);
        if (value == null && currentLevel == 0) {
            value = 1;
        } else if ((value == null && currentLevel > 0) || (value != null && currentLevel > 0 && value > currentLevel)) {
            value = currentLevel;
        } else {
            value++;
        }

        iterationLevels.put(uniqueID, value);
        return value;
    }

    @Override
    public void removeNodeInstance(final NodeInstance nodeInstance) {
        if (((NodeInstanceImpl) nodeInstance).isInversionOfControl()) {
            getKnowledgeRuntime().delete(
                    getKnowledgeRuntime().getFactHandle(nodeInstance));
        }
        this.nodeInstances.remove(nodeInstance);
    }

    @Override
    public Collection<org.kie.api.runtime.process.NodeInstance> getNodeInstances() {
        return new ArrayList<>(getNodeInstances(false));
    }

    @Override
    public Collection<NodeInstance> getNodeInstances(boolean recursive) {
        Collection<NodeInstance> result = nodeInstances;
        if (recursive) {
            result = new ArrayList<>(result);
            for (NodeInstance nodeInstance : nodeInstances) {
                if (nodeInstance instanceof NodeInstanceContainer) {
                    result
                            .addAll(((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstance)
                                    .getNodeInstances(true));
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public NodeInstance getNodeInstance(String nodeInstanceId) {
        return getNodeInstance(nodeInstanceId, false);
    }

    @Override
    public NodeInstance getNodeInstance(String nodeInstanceId, boolean recursive) {
        return getNodeInstances(recursive).stream()
                .filter(nodeInstance -> Objects.equals(nodeInstance.getId(), nodeInstanceId))
                .findFirst()
                .orElse(null);
    }

    public List<String> getActiveNodeIds() {
        List<String> result = new ArrayList<>();
        addActiveNodeIds(this, result);
        return result;
    }

    private void addActiveNodeIds(NodeInstanceContainer container, List<String> result) {
        for (org.kie.api.runtime.process.NodeInstance nodeInstance : container.getNodeInstances()) {
            result.add(((NodeImpl) nodeInstance.getNode()).getUniqueId());
            if (nodeInstance instanceof NodeInstanceContainer) {
                addActiveNodeIds((NodeInstanceContainer) nodeInstance, result);
            }
        }
    }

    @Override
    public NodeInstance getFirstNodeInstance(final long nodeId) {
        for (final NodeInstance nodeInstance : this.nodeInstances) {
            if (nodeInstance.getNodeId() == nodeId && nodeInstance.getLevel() == getCurrentLevel()) {
                return nodeInstance;
            }
        }
        return null;
    }

    public List<NodeInstance> getNodeInstances(final long nodeId) {
        List<NodeInstance> result = new ArrayList<>();
        for (final NodeInstance nodeInstance : this.nodeInstances) {
            if (nodeInstance.getNodeId() == nodeId) {
                result.add(nodeInstance);
            }
        }
        return result;
    }

    public List<NodeInstance> getNodeInstances(final long nodeId, final List<NodeInstance> currentView) {
        List<NodeInstance> result = new ArrayList<>();
        for (final NodeInstance nodeInstance : currentView) {
            if (nodeInstance.getNodeId() == nodeId) {
                result.add(nodeInstance);
            }
        }
        return result;
    }

    public NodeInstance getNodeInstanceByNodeDefinitionId(final String nodeDefinitionId, NodeContainer nodeContainer) {

        for (Node node : nodeContainer.getNodes()) {

            if (nodeDefinitionId.equals(node.getMetaData().get(UNIQUE_ID))) {
                return getNodeInstance(node);
            }

            if (node instanceof NodeContainer) {
                NodeInstance ni = getNodeInstanceByNodeDefinitionId(nodeDefinitionId, ((NodeContainer) node));

                if (ni != null) {
                    return ni;
                }
            }
        }

        throw new IllegalArgumentException("Node with definition id " + nodeDefinitionId + " was not found");
    }

    @Override
    public NodeInstance getNodeInstance(final Node node) {
        NodeInstanceFactory conf = NodeInstanceFactoryRegistry.getInstance(getKnowledgeRuntime().getEnvironment()).getProcessNodeInstanceFactory(node);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: "
                                                       + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(node, this, this);

        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: "
                                                       + node.getClass());
        }
        if (nodeInstance.isInversionOfControl()) {
            getKnowledgeRuntime().insert(nodeInstance);
        }
        return nodeInstance;
    }

    public WorkflowProcess getWorkflowProcess() {
        return (WorkflowProcess) getProcess();
    }

    @Override
    public Object getVariable(String name) {
        // for disconnected process instances, try going through the variable scope instances
        // (as the default variable scope cannot be retrieved as the link to the process could
        // be null and the associated working memory is no longer accessible)
        if (getKnowledgeRuntime() == null) {
            List<ContextInstance> variableScopeInstances =
                    getContextInstances(VariableScope.VARIABLE_SCOPE);
            if (variableScopeInstances != null && variableScopeInstances.size() == 1) {
                for (ContextInstance contextInstance : variableScopeInstances) {
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

    @Override
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
            Map<String, Object> result = new HashMap<>();
            for (ContextInstance contextInstance : variableScopeInstances) {
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

    @Override
    public void setVariable(String name, Object value) {
        VariableScope variableScope = (VariableScope) ((ContextContainer) getProcess()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScopeInstance == null) {
            throw new IllegalArgumentException("No variable scope found.");
        }
        variableScope.validateVariable(getProcessName(), name, value);
        variableScopeInstance.setVariable(name, value);
    }

    @Override
    public void setState(final int state, String outcome, Object faultData) {
        this.faultData = faultData;
        setState(state, outcome);
    }

    @Override
    public void setState(final int state, String outcome) {
        // TODO move most of this to ProcessInstanceImpl
        if (state == ProcessInstance.STATE_COMPLETED
                || state == ProcessInstance.STATE_ABORTED) {
            this.endDate = new Date();
            if (this.slaCompliance == SLA_PENDING) {
                if (System.currentTimeMillis() > slaDueDate.getTime()) {
                    // completion of the process instance is after expected SLA due date, mark it accordingly
                    this.slaCompliance = SLA_VIOLATED;
                } else {
                    this.slaCompliance = state == ProcessInstance.STATE_COMPLETED ? SLA_MET : SLA_ABORTED;
                }
            }

            InternalKnowledgeRuntime kruntime = getKnowledgeRuntime();
            InternalProcessRuntime processRuntime = (InternalProcessRuntime) kruntime.getProcessRuntime();
            processRuntime.getProcessEventSupport().fireBeforeProcessCompleted(this, kruntime);
            // JBPM-8094 - set state after event
            super.setState(state, outcome);

            // deactivate all node instances of this process instance
            while (!nodeInstances.isEmpty()) {
                NodeInstance nodeInstance = nodeInstances.get(0);
                nodeInstance
                        .cancel();
            }
            if (this.slaTimerId != null && !slaTimerId.trim().isEmpty()) {
                processRuntime.getJobsService().cancelJob(this.slaTimerId);
                logger.debug("SLA Timer {} has been canceled", this.slaTimerId);
            }
            removeEventListeners();
            processRuntime.getProcessInstanceManager().removeProcessInstance(this);
            processRuntime.getProcessEventSupport().fireAfterProcessCompleted(this, kruntime);

            if (isSignalCompletion()) {

                List<EventListener> listeners = eventListeners.get("processInstanceCompleted:" + getId());
                if (listeners != null) {
                    for (EventListener listener : listeners) {
                        listener.signalEvent("processInstanceCompleted:" + getId(), this);
                    }
                }

                processRuntime.getSignalManager().signalEvent("processInstanceCompleted:" + getId(), this);
            }
        } else {
            super.setState(state, outcome);
        }
    }

    @Override
    public void setState(final int state) {
        setState(state, null);
    }

    @Override
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

    @Override
    public void reconnect() {

        super.reconnect();
        for (NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance instanceof EventBasedNodeInstanceInterface) {
                ((EventBasedNodeInstanceInterface) nodeInstance)
                        .addEventListeners();
            }
        }
        registerExternalEventNodeListeners();
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkflowProcessInstance")
                .append(getId())
                .append(" [processId=")
                .append(getProcessId())
                .append(",state=")
                .append(getState())
                .append("]")
                .toString();
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(String trigger) {
        synchronized (this) {
            setStartDate(new Date());
            registerExternalEventNodeListeners();
            // activate timer event sub processes
            Node[] nodes = getNodeContainer().getNodes();
            for (Node node : nodes) {
                if (node instanceof EventSubProcessNode) {
                    Map<Timer, DroolsAction> timers = ((EventSubProcessNode) node).getTimers();
                    if (timers != null && !timers.isEmpty()) {
                        EventSubProcessNodeInstance eventSubprocess = (EventSubProcessNodeInstance) getNodeInstance(node);
                        eventSubprocess.trigger(null, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                    }
                }
            }
            super.start(trigger);
        }
    }

    @Override
    public void configureSLA() {
        String slaDueDateExpression = (String) getProcess().getMetaData().get(CUSTOM_SLA_DUE_DATE);
        if (slaDueDateExpression != null) {
            TimerInstance timer = configureSLATimer(slaDueDateExpression);
            if (timer != null) {
                this.slaTimerId = timer.getId();
                this.slaDueDate = new Date(System.currentTimeMillis() + timer.getDelay());
                this.slaCompliance = SLA_PENDING;
                logger.debug("SLA for process instance {} is PENDING with due date {}", this.getId(), this.slaDueDate);
            }
        }
    }

    public TimerInstance configureSLATimer(String slaDueDateExpression) {
        // setup SLA if provided
        slaDueDateExpression = resolveVariable(slaDueDateExpression);
        if (slaDueDateExpression == null || slaDueDateExpression.trim().isEmpty()) {
            logger.debug("Sla due date expression resolved to no value '{}'", slaDueDateExpression);
            return null;
        }
        logger.debug("SLA due date is set to {}", slaDueDateExpression);
        InternalKnowledgeRuntime kruntime = getKnowledgeRuntime();
        long duration;
        if (kruntime.getEnvironment().get("jbpm.business.calendar") != null) {
            BusinessCalendar businessCalendar = (BusinessCalendar) kruntime.getEnvironment().get("jbpm.business.calendar");
            duration = businessCalendar.calculateBusinessTimeAsDuration(slaDueDateExpression);
        } else {
            duration = DateTimeUtils.parseDuration(slaDueDateExpression);
        }

        TimerInstance timerInstance = new TimerInstance();
        timerInstance.setTimerId(-1);
        timerInstance.setDelay(duration);
        timerInstance.setPeriod(0);
        if (useTimerSLATracking()) {
            ProcessInstanceJobDescription description = ProcessInstanceJobDescription.of(-1L, DurationExpirationTime.after(duration), getId(), getProcessId());
            timerInstance.setId(kruntime.getProcessRuntime().getJobsService().scheduleProcessInstanceJob(description));
        }
        return timerInstance;
    }

    private void registerExternalEventNodeListeners() {
        for (Node node : getWorkflowProcess().getNodes()) {
            if (node instanceof EventNode && "external".equals(((EventNode) node).getScope())) {
                addEventListener(((EventNode) node).getType(), EMPTY_EVENT_LISTENER, true);
            } else if (node instanceof EventSubProcessNode) {
                List<String> events = ((EventSubProcessNode) node).getEvents();
                for (String type : events) {
                    addEventListener(type, EMPTY_EVENT_LISTENER, true);
                    if (isVariableExpression(type)) {
                        addEventListener(resolveVariable(type), EMPTY_EVENT_LISTENER, true);
                    }
                }
            } else if (node instanceof DynamicNode && ((DynamicNode) node).getActivationEventName() != null) {
                addEventListener(((DynamicNode) node).getActivationEventName(), EMPTY_EVENT_LISTENER, true);
            }
        }
        if (getWorkflowProcess().getMetaData().containsKey(COMPENSATION)) {
            addEventListener("Compensation", new CompensationEventListener(this), true);
        }
    }

    private void unregisterExternalEventNodeListeners() {
        for (Node node : getWorkflowProcess().getNodes()) {
            if (node instanceof EventNode && "external".equals(((EventNode) node).getScope())) {
                externalEventListeners.remove(((EventNode) node).getType());
            }
        }
    }

    private void handleSLAViolation() {
        if (slaCompliance == SLA_PENDING) {

            InternalKnowledgeRuntime kruntime = getKnowledgeRuntime();
            InternalProcessRuntime processRuntime = (InternalProcessRuntime) kruntime.getProcessRuntime();
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(this, kruntime);
            logger.debug("SLA violated on process instance {}", getId());
            this.slaCompliance = SLA_VIOLATED;
            this.slaTimerId = null;
            processRuntime.getProcessEventSupport().fireAfterSLAViolated(this, kruntime);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void signalEvent(String type, Object event) {
        logger.debug("Signal {} received with data {} in process instance {}", type, event, getId());
        synchronized (this) {
            if (getState() != ProcessInstance.STATE_ACTIVE) {
                return;
            }

            if ("timerTriggered".equals(type)) {
                TimerInstance timer = (TimerInstance) event;
                if (timer.getId().equals(slaTimerId)) {
                    handleSLAViolation();
                    // no need to pass the event along as it was purely for SLA tracking
                    return;
                }
            }
            if ("slaViolation".equals(type)) {
                handleSLAViolation();
                // no need to pass the event along as it was purely for SLA tracking
                return;
            }

            List<NodeInstance> currentView = new ArrayList<>(this.nodeInstances);

            try {
                this.activatingNodeIds = new ArrayList<>();
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
                    if (node instanceof EventNodeInterface
                            && ((EventNodeInterface) node).acceptsEvent(type, event, getResolver(node, currentView))) {
                        if (node instanceof EventNode && ((EventNode) node).getFrom() == null) {
                            EventNodeInstance eventNodeInstance = (EventNodeInstance) getNodeInstance(node);
                            eventNodeInstance.signalEvent(type, event);
                        } else {
                            if (node instanceof EventSubProcessNode && (resolveVariables(((EventSubProcessNode) node).getEvents()).contains(type))) {
                                EventSubProcessNodeInstance eventNodeInstance = (EventSubProcessNodeInstance) getNodeInstance(node);
                                eventNodeInstance.signalEvent(type, event);
                            }
                            if (node instanceof DynamicNode && type.equals(((DynamicNode) node).getActivationEventName())) {
                                DynamicNodeInstance dynamicNodeInstance = (DynamicNodeInstance) getNodeInstance(node);
                                dynamicNodeInstance.signalEvent(type, event);
                            } else {
                                List<NodeInstance> nodeInstances = getNodeInstances(node.getId(), currentView);
                                if (nodeInstances != null && !nodeInstances.isEmpty()) {
                                    for (NodeInstance nodeInstance : nodeInstances) {
                                        ((EventNodeInstanceInterface) nodeInstance).signalEvent(type, event);
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
                            if (event != null) {
                                Map<String, Object> dynamicParams = new HashMap<>();
                                if (event instanceof Map) {
                                    dynamicParams.putAll((Map<String, Object>) event);
                                } else {
                                    dynamicParams.put("Data", event);
                                }
                                nodeInstance.setDynamicParameters(dynamicParams);
                            }

                            nodeInstance.trigger(null, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                        }
                    }
                }
            } finally {
                if (this.activatingNodeIds != null) {
                    this.activatingNodeIds.clear();
                    this.activatingNodeIds = null;
                }
            }
        }
    }

    private Function<String, String> getResolver(Node node, List<NodeInstance> currentView) {
        if (node instanceof DynamicNode) {
            // special handling for dynamic node to allow to resolve variables from individual node instances of the dynamic node
            // instead of just relying on process instance's variables
            return e -> {
                List<NodeInstance> nodeInstances = getNodeInstances(node.getId(), currentView);
                if (nodeInstances != null && !nodeInstances.isEmpty()) {
                    StringBuilder st = new StringBuilder();
                    for (NodeInstance ni : nodeInstances) {
                        String result = resolveVariable(e, new NodeInstanceResolverFactory(ni));
                        st.append(result).append("###");
                    }
                    return st.toString();
                } else {
                    return resolveVariable(e);
                }
            };
        } else {
            return this::resolveVariable;
        }
    }

    protected List<String> resolveVariables(List<String> events) {
        return events.stream().map(this::resolveVariable).collect(Collectors.toList());
    }

    private String resolveVariable(String s) {
        return resolveVariable(s, new ProcessInstanceResolverFactory(this));
    }

    private String resolveVariable(String s, VariableResolverFactory factory) {
        Map<String, String> replacements = new HashMap<>();
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {

                Object variableValue = getVariable(paramName);
                if (variableValue != null) {
                    replacements.put(paramName, variableValue.toString());
                } else {
                    try {
                        variableValue = MVELSafeHelper.getEvaluator().eval(paramName, factory);
                        String variableValueString = variableValue == null ? "" : variableValue.toString();
                        replacements.put(paramName, variableValueString);
                    } catch (Throwable t) {
                        logger.error("Could not find variable scope for variable {}", paramName);
                    }
                }
            }
        }
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        return s;
    }

    @Override
    public void addEventListener(String type, EventListener listener, boolean external) {
        Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners : this.eventListeners;
        List<EventListener> listeners = eventListeners.computeIfAbsent(type, listenerType -> {
            final List<EventListener> newListenersList = new CopyOnWriteArrayList<>();
            if (external) {
                ((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
                        .getSignalManager().addEventListener(listenerType, this);
            }
            return newListenersList;
        });
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(String type, EventListener listener, boolean external) {
        Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners : this.eventListeners;
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
        } else {
            eventListeners.remove(type);
        }
    }

    private void removeEventListeners() {
        for (String type : externalEventListeners.keySet()) {
            ((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
                    .getSignalManager().removeEventListener(type, this);
        }
    }

    @Override
    public String[] getEventTypes() {
        return externalEventListeners.keySet().stream().map(this::resolveVariable).collect(Collectors.toList()).toArray(new String[externalEventListeners.size()]);
    }
    
    @Override
    public Set<EventDescription<?>> getEventDescriptions() {
        if (getState() == ProcessInstance.STATE_COMPLETED || getState() == ProcessInstance.STATE_ABORTED) {
            return Collections.emptySet();
        }
        VariableScope variableScope = (VariableScope) ((ContextContainer) getProcess()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        Set<EventDescription<?>> eventDesciptions = new LinkedHashSet<>();
        
        List<EventListener> activeListeners = eventListeners.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        
        activeListeners.addAll(externalEventListeners.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
        
        activeListeners.forEach(el -> eventDesciptions.addAll(el.getEventDescriptions()));
        
 
        ((org.jbpm.workflow.core.WorkflowProcess)getProcess()).getNodesRecursively().stream().filter(n -> n instanceof EventNodeInterface).forEach(n -> {
            
            NamedDataType dataType = null;
            if (((EventNodeInterface)n).getVariableName() != null) {
                Variable eventVar = variableScope.findVariable(((EventNodeInterface)n).getVariableName());
                if (eventVar != null) {
                    dataType = new NamedDataType(eventVar.getName(), eventVar.getType());
                }
            }
            if (n instanceof BoundaryEventNode) {
                BoundaryEventNode boundaryEventNode = (BoundaryEventNode) n;
                StateBasedNodeInstance attachedToNodeInstance = (StateBasedNodeInstance) getNodeInstances(true).stream().filter( ni -> ni.getNode().getMetaData().get(UNIQUE_ID).equals(boundaryEventNode.getAttachedToNodeId())).findFirst().orElse(null);
                if (attachedToNodeInstance != null) {
                    Map<String, String> properties = new HashMap<>();
                    properties.put("AttachedToID", attachedToNodeInstance.getNodeDefinitionId());
                    properties.put("AttachedToName", attachedToNodeInstance.getNodeName());
                    String eventType = EVENT_TYPE_SIGNAL;
                    String eventName = boundaryEventNode.getType();
                    Map<String, String> timerProperties = attachedToNodeInstance.extractTimerEventInformation();
                    if (timerProperties != null) {
                        properties.putAll(timerProperties);
                        eventType = "timer";
                        eventName = "timerTriggered";
                    } 
                
                    eventDesciptions.add(new BaseEventDescription(eventName, (String)n.getMetaData().get(UNIQUE_ID), n.getName(), eventType, null, getId(), dataType, properties));
                    
                }
                
            } else if (n instanceof EventSubProcessNode) {
                EventSubProcessNode eventSubProcessNode = (EventSubProcessNode) n;
                Node startNode = eventSubProcessNode.findStartNode();
                Map<Timer, DroolsAction> timers = eventSubProcessNode.getTimers();
                if (timers != null && !timers.isEmpty()) {
                    getNodeInstances(eventSubProcessNode.getId()).forEach(ni -> {
                        
                        Map<String, String> timerProperties = ((StateBasedNodeInstance) ni).extractTimerEventInformation();
                        if (timerProperties != null) {
                         
                            eventDesciptions.add(new BaseEventDescription("timerTriggered", (String)startNode.getMetaData().get("UniqueId"), startNode.getName(), "timer", ni.getId(), getId(), null, timerProperties));
                          
                        }
                    });
                } else {
                
                    for (String eventName : eventSubProcessNode.getEvents()) {
                        
                        eventDesciptions.add(new BaseEventDescription(eventName, (String)startNode.getMetaData().get("UniqueId"), startNode.getName(), "signal", null, getId(), dataType));
                    }
                
                }
            } else if (n instanceof EventNode) {
                NamedDataType finalDataType = dataType;
                getNodeInstances(n.getId()).forEach(ni -> eventDesciptions.add(
                        new BaseEventDescription(
                                ((EventNode) n).getType(),
                                (String) n.getMetaData().get(UNIQUE_ID),
                                n.getName(),
                                (String) n.getMetaData().getOrDefault(EVENT_TYPE, EVENT_TYPE_SIGNAL),
                                ni.getId(),
                                getId(),
                                finalDataType)));
            } else if (n instanceof StateNode) {
                getNodeInstances(n.getId()).forEach(ni -> eventDesciptions.add(
                        new BaseEventDescription(
                                (String) n.getMetaData().get(CONDITION),
                                (String) n.getMetaData().get(UNIQUE_ID),
                                n.getName(),
                                (String) n.getMetaData().getOrDefault(EVENT_TYPE, EVENT_TYPE_SIGNAL),
                                ni.getId(),
                                getId(),
                                null)));
            }
            
        });
        
        
        return eventDesciptions;
    }

    @Override
    public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
        Node nodeInstanceNode = nodeInstance.getNode();
        if (nodeInstanceNode != null) {
            Object compensationBoolObj = nodeInstanceNode.getMetaData().get(IS_FOR_COMPENSATION);
            boolean isForCompensation = compensationBoolObj != null && (Boolean) compensationBoolObj;
            if (isForCompensation) {
                return;
            }
        }
        if (nodeInstance instanceof FaultNodeInstance || nodeInstance instanceof EndNodeInstance ||
                ((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()
                || nodeInstance instanceof CompositeNodeInstance) {
            if (((org.jbpm.workflow.core.WorkflowProcess) getProcess()).isAutoComplete() && canComplete()) {
                setState(ProcessInstance.STATE_COMPLETED);
            }
        } else {
            throw new IllegalArgumentException(
                    "Completing a node instance that has no outgoing connection is not supported.");
        }
    }

    private boolean canComplete() {
        if (nodeInstances.isEmpty()) {
            return true;
        } else {
            int eventSubprocessCounter = 0;
            for (NodeInstance nodeInstance : nodeInstances) {
                Node node = nodeInstance.getNode();
                if (node instanceof EventSubProcessNode) {
                    if (((EventSubProcessNodeInstance) nodeInstance).getNodeInstances().isEmpty()) {
                        eventSubprocessCounter++;
                    }
                } else {
                    return false;
                }
            }
            return eventSubprocessCounter == nodeInstances.size();
        }
    }

    public void addCompletedNodeId(String uniqueId) {
        this.completedNodeIds.add(uniqueId.intern());
    }

    public List<String> getCompletedNodeIds() {
        return new ArrayList<>(this.completedNodeIds);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Map<String, Integer> getIterationLevels() {
        return iterationLevels;
    }

    public void addActivatingNodeId(String uniqueId) {
        if (this.activatingNodeIds == null) {
            return;
        }
        this.activatingNodeIds.add(uniqueId.intern());
    }

    public List<String> getActivatingNodeIds() {
        if (this.activatingNodeIds == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(this.activatingNodeIds);
    }

    @Override
    public Object getFaultData() {
        return faultData;
    }

    @Override
    public boolean isSignalCompletion() {
        return signalCompletion;
    }

    @Override
    public void setSignalCompletion(boolean signalCompletion) {
        this.signalCompletion = signalCompletion;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getCorrelationKey() {
        if (correlationKey == null && getMetaData().get(CORRELATION_KEY) != null) {
            this.correlationKey = ((CorrelationKey) getMetaData().get(CORRELATION_KEY)).toExternalForm();
        }
        return correlationKey;
    }

    public void setCorrelationKey(String correlationKey) {
        this.correlationKey = correlationKey;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        if (this.startDate == null) {
            this.startDate = startDate;
        }
    }

    protected boolean hasDeploymentId() {
        return this.deploymentId != null && !this.deploymentId.isEmpty();
    }

    protected boolean useAsync(final Node node) {
        if (!(node instanceof EventSubProcessNode) && (node instanceof ActionNode || node instanceof StateBasedNode || node instanceof EndNode)) {
            boolean asyncMode = Boolean.parseBoolean((String) node.getMetaData().get(CUSTOM_ASYNC));
            if (asyncMode) {
                return true;
            }

            return Boolean.parseBoolean((String) getKnowledgeRuntime().getEnvironment().get("AsyncMode"));
        }

        return false;
    }

    protected boolean useTimerSLATracking() {

        String mode = (String) getKnowledgeRuntime().getEnvironment().get("SLATimerMode");
        if (mode == null) {
            return true;
        }

        return Boolean.parseBoolean(mode);
    }

    @Override
    public int getSlaCompliance() {
        return slaCompliance;
    }

    public void internalSetSlaCompliance(int slaCompliance) {
        this.slaCompliance = slaCompliance;
    }

    @Override
    public Date getSlaDueDate() {
        return slaDueDate;
    }

    public void internalSetSlaDueDate(Date slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public String getSlaTimerId() {
        return slaTimerId;
    }

    public void internalSetSlaTimerId(String slaTimerId) {
        this.slaTimerId = slaTimerId;
    }

    @Override
    public String getNodeIdInError() {
        return nodeIdInError;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public String getReferenceId() {
        return this.referenceId;
    }

    private boolean isVariableExpression(String eventType) {
        if (eventType == null) {
            return false;
        }
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(eventType);
        return matcher.find();
    }

    @Override
    public void setErrorState(NodeInstance nodeInstanceInError, Exception e) {
        String errorId = UUID.randomUUID().toString();
        this.nodeIdInError = nodeInstanceInError.getNodeDefinitionId();
        Throwable rootException = getRootException(e);
        this.errorMessage = errorId + " - " + rootException.getClass().getCanonicalName() + " - " + rootException.getMessage();
        setState(STATE_ERROR);
        logger.error("Unexpected error (id {}) while executing node {} in process instance {}", errorId, nodeInstanceInError.getNode().getName(), this.getId(), e);
        // remove node instance that caused an error
        ((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstanceInError.getNodeInstanceContainer()).removeNodeInstance(nodeInstanceInError);
    }

    public void internalSetErrorNodeId(String errorNodeId) {
        this.nodeIdInError = errorNodeId;
    }

    public void internalSetErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }
}
