/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.mvel.util.MVELEvaluator;
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
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.EndNodeInstance;
import org.jbpm.workflow.instance.node.EventBasedNodeInstanceInterface;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstanceInterface;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.FaultNodeInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.process.CorrelationKey;
import org.kie.kogito.internal.process.event.KogitoEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.TimerJobId;
import org.kie.kogito.process.BaseEventDescription;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.NamedDataType;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.flexible.AdHocFragment;
import org.kie.kogito.process.flexible.ItemDescription;
import org.kie.kogito.process.flexible.Milestone;
import org.kie.kogito.timer.TimerInstance;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.ruleflow.core.Metadata.COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.CONDITION;
import static org.jbpm.ruleflow.core.Metadata.CORRELATION_KEY;
import static org.jbpm.ruleflow.core.Metadata.CUSTOM_SLA_DUE_DATE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.IS_FOR_COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.workflow.instance.impl.DummyEventListener.EMPTY_EVENT_LISTENER;
import static org.kie.kogito.process.flexible.ItemDescription.Status.ACTIVE;
import static org.kie.kogito.process.flexible.ItemDescription.Status.AVAILABLE;
import static org.kie.kogito.process.flexible.ItemDescription.Status.COMPLETED;

/**
 * Default implementation of a RuleFlow process instance.
 */
public abstract class WorkflowProcessInstanceImpl extends ProcessInstanceImpl implements WorkflowProcessInstance {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowProcessInstanceImpl.class);

    private final List<NodeInstance> nodeInstances = new ArrayList<>();

    private Map<String, List<KogitoEventListener>> eventListeners = new HashMap<>();
    private Map<String, List<KogitoEventListener>> externalEventListeners = new HashMap<>();

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
    private transient Optional<Throwable> errorCause = Optional.empty();

    private int slaCompliance = KogitoProcessInstance.SLA_NA;
    private Date slaDueDate;
    private String slaTimerId;

    private String referenceId;

    private AgendaFilter agendaFilter;

    private ProcessInstance<?> kogitoProcessInstance;

    @Override
    public NodeContainer getNodeContainer() {
        return getWorkflowProcess();
    }

    @Override
    public void addNodeInstance(final NodeInstance nodeInstance) {
        if (nodeInstance.getStringId() == null) {
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
                if (nodeInstance instanceof KogitoNodeInstanceContainer) {
                    result.addAll(((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstance).getNodeInstances(true));
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public Collection<KogitoNodeInstance> getKogitoNodeInstances(Predicate<KogitoNodeInstance> filter,
            boolean recursive) {
        Collection<KogitoNodeInstance> result = new ArrayList<>();
        for (NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance instanceof KogitoNodeInstance && filter.test(nodeInstance)) {
                result.add(nodeInstance);
            }
            if (nodeInstance instanceof KogitoNodeInstanceContainer && recursive) {
                result.addAll(((KogitoNodeInstanceContainer) nodeInstance).getKogitoNodeInstances(
                        filter, true));
            }
        }
        return result;
    }

    @Override
    public NodeInstance getNodeInstance(long nodeInstanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KogitoNodeInstance getNodeInstance(String nodeInstanceId) {
        return getNodeInstance(nodeInstanceId, false);
    }

    @Override
    public NodeInstance getNodeInstance(String nodeInstanceId, boolean recursive) {
        return getNodeInstances(recursive).stream()
                .filter(nodeInstance -> Objects.equals(nodeInstance.getStringId(), nodeInstanceId))
                .findFirst()
                .orElse(null);
    }

    public List<String> getActiveNodeIds() {
        List<String> result = new ArrayList<>();
        addActiveNodeIds(this, result);
        return result;
    }

    private void addActiveNodeIds(KogitoNodeInstanceContainer container, List<String> result) {
        for (org.kie.api.runtime.process.NodeInstance nodeInstance : container.getNodeInstances()) {
            result.add(((NodeImpl) nodeInstance.getNode()).getUniqueId());
            if (nodeInstance instanceof KogitoNodeInstanceContainer) {
                addActiveNodeIds((KogitoNodeInstanceContainer) nodeInstance, result);
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

    @Override
    public String getBusinessKey() {
        return correlationKey;
    }

    @Override
    public NodeInstance getNodeInstance(final org.kie.api.definition.process.Node node) {
        // async continuation handling
        org.kie.api.definition.process.Node actualNode = resolveAsync(node);

        NodeInstanceFactory conf = NodeInstanceFactoryRegistry.getInstance(getKnowledgeRuntime().getEnvironment()).getProcessNodeInstanceFactory(actualNode);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal node type: "
                    + node.getClass());
        }
        NodeInstanceImpl nodeInstance = (NodeInstanceImpl) conf.getNodeInstance(actualNode, this, this);

        if (nodeInstance == null) {
            throw new IllegalArgumentException("Illegal node type: "
                    + node.getClass());
        }
        if (nodeInstance.isInversionOfControl()) {
            getKnowledgeRuntime().insert(nodeInstance);
        }
        return nodeInstance;
    }

    public KogitoWorkflowProcess getWorkflowProcess() {
        return (KogitoWorkflowProcess) getProcess();
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
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) getContextInstance(VariableScope.VARIABLE_SCOPE);
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
            List<ContextInstance> variableScopeInstances = getContextInstances(VariableScope.VARIABLE_SCOPE);
            if (variableScopeInstances == null) {
                return Collections.emptyMap();
            }
            Map<String, Object> result = new HashMap<>();
            for (ContextInstance contextInstance : variableScopeInstances) {
                Map<String, Object> variables = ((VariableScopeInstance) contextInstance).getVariables();
                result.putAll(variables);
            }
            return result;
        }
        // else retrieve the variable scope
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScopeInstance == null) {
            return Collections.emptyMap();
        }
        return variableScopeInstance.getVariables();
    }

    @Override
    public void setVariable(String name, Object value) {
        VariableScope variableScope = (VariableScope) ((ContextContainer) getProcess()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) getContextInstance(VariableScope.VARIABLE_SCOPE);
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
        if (state == KogitoProcessInstance.STATE_COMPLETED
                || state == KogitoProcessInstance.STATE_ABORTED) {
            this.endDate = new Date();
            if (this.slaCompliance == KogitoProcessInstance.SLA_PENDING) {
                if (System.currentTimeMillis() > slaDueDate.getTime()) {
                    // completion of the process instance is after expected SLA due date, mark it accordingly
                    this.slaCompliance = KogitoProcessInstance.SLA_VIOLATED;
                } else {
                    this.slaCompliance = state == KogitoProcessInstance.STATE_COMPLETED ? KogitoProcessInstance.SLA_MET : KogitoProcessInstance.SLA_ABORTED;
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
                nodeInstance.cancel();
            }
            if (this.slaTimerId != null && !slaTimerId.trim().isEmpty()) {
                processRuntime.getJobsService().cancelJob(this.slaTimerId);
                logger.debug("SLA Timer {} has been canceled", this.slaTimerId);
            }
            removeEventListeners();
            processRuntime.getProcessInstanceManager().removeProcessInstance(this);
            processRuntime.getProcessEventSupport().fireAfterProcessCompleted(this, kruntime);

            if (isSignalCompletion()) {

                List<KogitoEventListener> listeners = eventListeners.get("processInstanceCompleted:" + getStringId());
                if (listeners != null) {
                    for (KogitoEventListener listener : listeners) {
                        listener.signalEvent("processInstanceCompleted:" + getStringId(), this);
                    }
                }

                processRuntime.getSignalManager().signalEvent("processInstanceCompleted:" + getStringId(), this);
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
                ((EventBasedNodeInstanceInterface) nodeInstance).addEventListeners();
            }
        }
        registerExternalEventNodeListeners();
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkflowProcessInstance")
                .append(" [Id=")
                .append(getStringId())
                .append(",processId=")
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
            org.kie.api.definition.process.Node[] nodes = getNodeContainer().getNodes();
            for (org.kie.api.definition.process.Node node : nodes) {
                if (node instanceof EventSubProcessNode) {
                    Map<Timer, DroolsAction> timers = ((EventSubProcessNode) node).getTimers();
                    if (timers != null && !timers.isEmpty()) {
                        EventSubProcessNodeInstance eventSubProcess = (EventSubProcessNodeInstance) getNodeInstance(node);
                        eventSubProcess.trigger(null, Node.CONNECTION_DEFAULT_TYPE);
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
                this.slaCompliance = KogitoProcessInstance.SLA_PENDING;
                logger.debug("SLA for process instance {} is PENDING with due date {}", this.getStringId(), this.slaDueDate);
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
            ProcessInstanceJobDescription description = ProcessInstanceJobDescription.of(new TimerJobId(-1L), DurationExpirationTime.after(duration), getStringId(), getProcessId());
            timerInstance.setId((InternalProcessRuntime.asKogitoProcessRuntime(kruntime.getProcessRuntime()).getJobsService().scheduleProcessInstanceJob(description)));
        }
        return timerInstance;
    }

    private void registerExternalEventNodeListeners() {
        for (org.kie.api.definition.process.Node node : getWorkflowProcess().getNodes()) {
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
            }
        }
        if (getWorkflowProcess().getMetaData().containsKey(COMPENSATION)) {
            addEventListener("Compensation", new CompensationEventListener(this), true);
        }
    }

    private void unregisterExternalEventNodeListeners() {
        for (org.kie.api.definition.process.Node node : getWorkflowProcess().getNodes()) {
            if (node instanceof EventNode && "external".equals(((EventNode) node).getScope())) {
                externalEventListeners.remove(((EventNode) node).getType());
            }
        }
    }

    private void handleSLAViolation() {
        if (slaCompliance == KogitoProcessInstance.SLA_PENDING) {

            InternalKnowledgeRuntime kruntime = getKnowledgeRuntime();
            InternalProcessRuntime processRuntime = (InternalProcessRuntime) kruntime.getProcessRuntime();
            processRuntime.getProcessEventSupport().fireBeforeSLAViolated(this, kruntime);
            logger.debug("SLA violated on process instance {}", getStringId());
            this.slaCompliance = KogitoProcessInstance.SLA_VIOLATED;
            this.slaTimerId = null;
            processRuntime.getProcessEventSupport().fireAfterSLAViolated(this, kruntime);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void signalEvent(String type, Object event) {
        logger.debug("Signal {} received with data {} in process instance {}", type, event, getStringId());
        synchronized (this) {
            if (getState() != KogitoProcessInstance.STATE_ACTIVE) {
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
                List<KogitoEventListener> listeners = eventListeners.get(type);
                if (listeners != null) {
                    for (KogitoEventListener listener : listeners) {
                        listener.signalEvent(type, event);
                    }
                }
                listeners = externalEventListeners.get(type);
                if (listeners != null) {
                    for (KogitoEventListener listener : listeners) {
                        listener.signalEvent(type, event);
                    }
                }
                for (org.kie.api.definition.process.Node node : getWorkflowProcess().getNodes()) {
                    if (node instanceof EventNodeInterface
                            && ((EventNodeInterface) node).acceptsEvent(type, event, getResolver(node, currentView))) {
                        if (node instanceof EventNode && ((EventNode) node).getFrom() == null) {
                            EventNodeInstance eventNodeInstance = (EventNodeInstance) getNodeInstance(node);
                            eventNodeInstance.signalEvent(type, event, getResolver(node, currentView));
                        } else {
                            if (node instanceof EventSubProcessNode && (resolveVariables(((EventSubProcessNode) node).getEvents()).contains(type))) {
                                EventSubProcessNodeInstance eventNodeInstance = (EventSubProcessNodeInstance) getNodeInstance(node);
                                eventNodeInstance.signalEvent(type, event);
                            } else {
                                List<NodeInstance> nodeInstances = getNodeInstances(node.getId(), currentView);
                                if (nodeInstances != null && !nodeInstances.isEmpty()) {
                                    for (NodeInstance nodeInstance : nodeInstances) {
                                        ((EventNodeInstanceInterface) nodeInstance).signalEvent(type, event, getResolver(node, currentView));
                                    }
                                }
                            }
                        }
                    }
                }

                if (((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()) {
                    for (org.kie.api.definition.process.Node node : getWorkflowProcess().getNodes()) {
                        if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
                            NodeInstance nodeInstance = getNodeInstance(node);
                            if (event != null) {
                                Map<String, Object> dynamicParams = new HashMap<>(getVariables());
                                if (event instanceof Map) {
                                    dynamicParams.putAll((Map<String, Object>) event);
                                } else {
                                    dynamicParams.put("Data", event);
                                }
                                nodeInstance.setDynamicParameters(dynamicParams);
                            }
                            nodeInstance.trigger(null, Node.CONNECTION_DEFAULT_TYPE);
                        } else if (node instanceof CompositeNode) {
                            Optional<NodeInstance> instance = this.nodeInstances.stream().filter(ni -> ni.getNodeId() == node.getId()).findFirst();
                            instance.ifPresent(n -> ((CompositeNodeInstance) n).signalEvent(type, event));
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

    private Function<String, Object> getResolver(org.kie.api.definition.process.Node node, List<NodeInstance> currentView) {
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
                        MVELEvaluator mvelEvaluator = MVELProcessHelper.evaluator();
                        variableValue = mvelEvaluator.eval(paramName, factory);
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
    public void addEventListener(String type, KogitoEventListener listener, boolean external) {
        Map<String, List<KogitoEventListener>> eventListeners = external ? this.externalEventListeners : this.eventListeners;
        List<KogitoEventListener> listeners = eventListeners.computeIfAbsent(type, listenerType -> {
            final List<KogitoEventListener> newListenersList = new CopyOnWriteArrayList<>();
            if (external) {
                ((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
                        .getSignalManager().addEventListener(listenerType, this);
            }
            return newListenersList;
        });
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(String type, KogitoEventListener listener, boolean external) {
        Map<String, List<KogitoEventListener>> eventListeners = external ? this.externalEventListeners : this.eventListeners;
        List<KogitoEventListener> listeners = eventListeners.get(type);
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
        if (getState() == KogitoProcessInstance.STATE_COMPLETED || getState() == KogitoProcessInstance.STATE_ABORTED) {
            return Collections.emptySet();
        }
        VariableScope variableScope = (VariableScope) ((ContextContainer) getProcess()).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        Set<EventDescription<?>> eventDesciptions = new LinkedHashSet<>();

        List<KogitoEventListener> activeListeners = eventListeners.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        activeListeners.addAll(externalEventListeners.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        activeListeners.forEach(el -> eventDesciptions.addAll(el.getEventDescriptions()));

        ((org.jbpm.workflow.core.WorkflowProcess) getProcess()).getNodesRecursively().stream().filter(n -> n instanceof EventNodeInterface).forEach(n -> {

            NamedDataType dataType = null;
            if (((EventNodeInterface) n).getVariableName() != null) {
                Variable eventVar = variableScope.findVariable(((EventNodeInterface) n).getVariableName());
                if (eventVar != null) {
                    dataType = new NamedDataType(eventVar.getName(), eventVar.getType());
                }
            }
            if (n instanceof BoundaryEventNode) {
                BoundaryEventNode boundaryEventNode = (BoundaryEventNode) n;
                StateBasedNodeInstance attachedToNodeInstance = (StateBasedNodeInstance) getNodeInstances(true).stream()
                        .filter(ni -> ni.getNode().getMetaData().get(UNIQUE_ID).equals(boundaryEventNode.getAttachedToNodeId())).findFirst().orElse(null);
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

                    eventDesciptions.add(new BaseEventDescription(eventName, (String) n.getMetaData().get(UNIQUE_ID), n.getName(), eventType, null, getStringId(), dataType, properties));

                }

            } else if (n instanceof EventSubProcessNode) {
                EventSubProcessNode eventSubProcessNode = (EventSubProcessNode) n;
                org.kie.api.definition.process.Node startNode = eventSubProcessNode.findStartNode();
                Map<Timer, DroolsAction> timers = eventSubProcessNode.getTimers();
                if (timers != null && !timers.isEmpty()) {
                    getNodeInstances(eventSubProcessNode.getId()).forEach(ni -> {

                        Map<String, String> timerProperties = ((StateBasedNodeInstance) ni).extractTimerEventInformation();
                        if (timerProperties != null) {

                            eventDesciptions.add(new BaseEventDescription("timerTriggered", (String) startNode.getMetaData().get("UniqueId"), startNode.getName(), "timer", ni.getStringId(),
                                    getStringId(), null, timerProperties));

                        }
                    });
                } else {

                    for (String eventName : eventSubProcessNode.getEvents()) {

                        eventDesciptions.add(new BaseEventDescription(eventName, (String) startNode.getMetaData().get("UniqueId"), startNode.getName(), "signal", null, getStringId(), dataType));
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
                                ni.getStringId(),
                                getStringId(),
                                finalDataType)));
            } else if (n instanceof StateNode) {
                getNodeInstances(n.getId()).forEach(ni -> eventDesciptions.add(
                        new BaseEventDescription(
                                (String) n.getMetaData().get(CONDITION),
                                (String) n.getMetaData().get(UNIQUE_ID),
                                n.getName(),
                                (String) n.getMetaData().getOrDefault(EVENT_TYPE, EVENT_TYPE_SIGNAL),
                                ni.getStringId(),
                                getStringId(),
                                null)));
            }

        });

        return eventDesciptions;
    }

    @Override
    public void nodeInstanceCompleted(NodeInstance nodeInstance, String outType) {
        org.kie.api.definition.process.Node nodeInstanceNode = nodeInstance.getNode();
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
                setState(KogitoProcessInstance.STATE_COMPLETED);
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
                org.kie.api.definition.process.Node node = nodeInstance.getNode();
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

    @Override
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

    @Override
    public void setStartDate(Date startDate) {
        if (this.startDate == null) {
            this.startDate = startDate;
        }
    }

    protected boolean hasDeploymentId() {
        return this.deploymentId != null && !this.deploymentId.isEmpty();
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
    public Optional<Throwable> getErrorCause() {
        return errorCause;
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
        this.nodeIdInError = nodeInstanceInError.getNodeDefinitionId();
        this.errorCause = Optional.of(e);
        Throwable rootException = getRootException(e);
        this.errorMessage = rootException.getClass().getCanonicalName() + " - " + rootException.getMessage();
        setState(STATE_ERROR);
        logger.error("Unexpected error while executing node {} in process instance {}", nodeInstanceInError.getNode().getName(), this.getStringId(), e);
        // remove node instance that caused an error
        ((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstanceInError.getNodeInstanceContainer()).removeNodeInstance(nodeInstanceInError);
    }

    public void internalSetErrorNodeId(String errorNodeId) {
        this.nodeIdInError = errorNodeId;
    }

    public void internalSetErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCause = Optional.empty();
    }

    @Override
    public Collection<AdHocFragment> adHocFragments() {
        return Stream.of(getNodeContainer().getNodes())
                .filter(n -> !(n instanceof StartNode) && !(n instanceof BoundaryEventNode))
                .filter(n -> n.getIncomingConnections().isEmpty())
                .map(node -> new AdHocFragment.Builder(node.getClass())
                        .withName(node.getName())
                        .withAutoStart(Boolean.parseBoolean((String) node.getMetaData().get(Metadata.CUSTOM_AUTO_START)))
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Milestone> milestones() {
        return getNodesByType(MilestoneNode.class)
                .map(n -> {
                    String uid = (String) n.getMetaData().get(UNIQUE_ID);
                    return Milestone.builder()
                            .withId(uid)
                            .withName(n.getName())
                            .withStatus(getMilestoneStatus(uid))
                            .build();
                })
                .collect(Collectors.toSet());
    }

    private <N extends org.kie.api.definition.process.Node> Stream<N> getNodesByType(Class<N> nodeClass) {
        return getWorkflowProcess().getNodesRecursively().stream().filter(nodeClass::isInstance).map(nodeClass::cast);
    }

    private ItemDescription.Status getMilestoneStatus(String uid) {
        if (getCompletedNodeIds().contains(uid)) {
            return COMPLETED;
        }
        if (getActiveNodeIds().contains(uid)) {
            return ACTIVE;
        }
        return AVAILABLE;
    }

    protected Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    @Override
    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    @Override
    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public final void wrap(ProcessInstance<?> kogitoProcessInstance) {
        this.kogitoProcessInstance = kogitoProcessInstance;
    }

    public final ProcessInstance<?> unwrap() {
        return this.kogitoProcessInstance;
    }
}
