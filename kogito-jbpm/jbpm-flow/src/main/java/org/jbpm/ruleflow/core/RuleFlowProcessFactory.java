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

package org.jbpm.ruleflow.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.impl.actions.CancelNodeInstanceAction;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.Trigger;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.ruleflow.core.Metadata.ACTION;
import static org.jbpm.ruleflow.core.Metadata.ATTACHED_TO;
import static org.jbpm.ruleflow.core.Metadata.CANCEL_ACTIVITY;
import static org.jbpm.ruleflow.core.Metadata.SIGNAL_NAME;
import static org.jbpm.ruleflow.core.Metadata.TIME_CYCLE;
import static org.jbpm.ruleflow.core.Metadata.TIME_DATE;
import static org.jbpm.ruleflow.core.Metadata.TIME_DURATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.workflow.core.impl.ExtendedNodeImpl.EVENT_NODE_EXIT;

public class RuleFlowProcessFactory extends RuleFlowNodeContainerFactory {

    public static final String METHOD_NAME = "name";
    public static final String METHOD_PACKAGE_NAME = "packageName";
    public static final String METHOD_DYNAMIC = "dynamic";
    public static final String METHOD_VERSION = "version";
    public static final String METHOD_VISIBILITY = "visibility";
    public static final String METHOD_VALIDATE = "validate";
    public static final String METHOD_IMPORTS = "imports";
    public static final String METHOD_GLOBAL = "global";
    public static final String METHOD_VARIABLE = "variable";
    public static final String METHOD_ADD_COMPENSATION_CONTEXT = "addCompensationContext";

    private static final Logger logger = LoggerFactory.getLogger(RuleFlowProcessFactory.class);


    public static RuleFlowProcessFactory createProcess(String id) {
        return new RuleFlowProcessFactory(id);
    }

    protected RuleFlowProcessFactory(String id) {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(id);
        process.setAutoComplete(true);
        setNodeContainer(process);
    }

    protected RuleFlowProcess getRuleFlowProcess() {
        return (RuleFlowProcess) getNodeContainer();
    }

    public RuleFlowProcessFactory name(String name) {
        getRuleFlowProcess().setName(name);
        return this;
    }

    public RuleFlowProcessFactory visibility(String visibility) {
        getRuleFlowProcess().setVisibility(visibility);
        return this;
    }

    public RuleFlowProcessFactory dynamic(boolean dynamic) {
        getRuleFlowProcess().setDynamic(dynamic);
        if (dynamic) {
            getRuleFlowProcess().setAutoComplete(false);
        }
        return this;
    }

    public RuleFlowProcessFactory version(String version) {
        getRuleFlowProcess().setVersion(version);
        return this;
    }

    public RuleFlowProcessFactory packageName(String packageName) {
        getRuleFlowProcess().setPackageName(packageName);
        return this;
    }

    public RuleFlowProcessFactory imports(String... imports) {
        getRuleFlowProcess().addImports(Arrays.asList(imports));
        return this;
    }

    public RuleFlowProcessFactory functionImports(String... functionImports) {
        getRuleFlowProcess().addFunctionImports(Arrays.asList(functionImports));
        return this;
    }

    public RuleFlowProcessFactory globals(Map<String, String> globals) {
        getRuleFlowProcess().setGlobals(globals);
        return this;
    }

    public RuleFlowProcessFactory global(String name, String type) {
        Map<String, String> globals = getRuleFlowProcess().getGlobals();
        if (globals == null) {
            globals = new HashMap<String, String>();
            getRuleFlowProcess().setGlobals(globals);
        }
        globals.put(name, type);
        return this;
    }

    public RuleFlowProcessFactory variable(String name, DataType type) {
        return variable(name, type, null);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, Object value) {
        return variable(name, type, value, null, null);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, String metaDataName, Object metaDataValue) {
        return variable(name, type, null, metaDataName, metaDataValue);
    }

    public RuleFlowProcessFactory variable(String name, DataType type, Object value, String metaDataName, Object metaDataValue) {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setValue(value);
        if (metaDataName != null && metaDataValue != null) {
            variable.setMetaData(metaDataName, metaDataValue);
        }
        getRuleFlowProcess().getVariableScope().getVariables().add(variable);
        return this;
    }

    public RuleFlowProcessFactory swimlane(String name) {
        Swimlane swimlane = new Swimlane();
        swimlane.setName(name);
        getRuleFlowProcess().getSwimlaneContext().addSwimlane(swimlane);
        return this;
    }

    public RuleFlowProcessFactory addCompensationContext(String contextId) {
        CompensationScope compensationScope = new CompensationScope();
        compensationScope.setContextContainerId(contextId);
        getRuleFlowProcess().addContext(compensationScope);
        getRuleFlowProcess().setDefaultContext(compensationScope);
        return this;
    }

    public RuleFlowProcessFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        getRuleFlowProcess().getExceptionScope().setExceptionHandler(exception, exceptionHandler);
        return this;
    }

    public RuleFlowProcessFactory exceptionHandler(String exception, String dialect, String action) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        exceptionHandler.setAction(new DroolsConsequenceAction(dialect, action));
        return exceptionHandler(exception, exceptionHandler);
    }

    public RuleFlowProcessFactory metaData(String name, Object value) {
        getRuleFlowProcess().setMetaData(name, value);
        return this;
    }

    public RuleFlowProcessFactory validate() {
        link();
        ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess(getRuleFlowProcess());
        for (ProcessValidationError error : errors) {
            logger.error(error.toString());
        }
        if (errors.length > 0) {
            throw new RuntimeException("Process could not be validated !");
        }
        return this;
    }

    public RuleFlowProcessFactory link() {
        RuleFlowProcess process = getRuleFlowProcess();
        linkBoundaryEvents(process);
        postProcessNodes(process, process);
        return this;
    }

    public RuleFlowProcessFactory done() {
        throw new IllegalArgumentException("Already on the top-level.");
    }

    public RuleFlowProcess getProcess() {
        return getRuleFlowProcess();
    }

    @Override
    public RuleFlowProcessFactory connection(long fromId, long toId) {
        super.connection(fromId, toId);
        return this;
    }

    @Override
    public RuleFlowProcessFactory connection(long fromId, long toId, String uniqueId) {
        super.connection(fromId, toId, uniqueId);
        return this;
    }

    protected void linkBoundaryEvents(NodeContainer nodeContainer) {
        for (Node node : nodeContainer.getNodes()) {
            if (node instanceof CompositeNode) {
                CompositeNode compositeNode = (CompositeNode) node;
                linkBoundaryEvents(compositeNode.getNodeContainer());
            }
            if (node instanceof EventNode) {
                final String attachedTo = (String) node.getMetaData().get(ATTACHED_TO);
                if (attachedTo != null) {
                    Node attachedNode = findNodeByIdOrUniqueIdInMetadata(nodeContainer, attachedTo, "Could not find node to attach to: " + attachedTo);
                    for (EventFilter filter : ((EventNode) node).getEventFilters()) {
                        String type = ((EventTypeFilter) filter).getType();
                        if (type.startsWith("Timer-")) {
                            linkBoundaryTimerEvent(node, attachedTo, attachedNode);
                        } else if (node.getMetaData().get(SIGNAL_NAME) != null || type.startsWith("Message-")) {
                            linkBoundarySignalEvent(node, attachedTo);
                        }
                    }
                }
            }
        }
    }

    protected void linkBoundaryTimerEvent(Node node, String attachedTo, Node attachedNode) {
        boolean cancelActivity = (Boolean) node.getMetaData().get(CANCEL_ACTIVITY);
        StateBasedNode compositeNode = (StateBasedNode) attachedNode;
        String timeDuration = (String) node.getMetaData().get(TIME_DURATION);
        String timeCycle = (String) node.getMetaData().get(TIME_CYCLE);
        String timeDate = (String) node.getMetaData().get(TIME_DATE);
        Timer timer = new Timer();
        if (timeDuration != null) {
            timer.setDelay(timeDuration);
            timer.setTimeType(Timer.TIME_DURATION);
            compositeNode.addTimer(timer, timerAction("Timer-" + attachedTo + "-" + timeDuration + "-" + node.getId()));
        } else if (timeCycle != null) {
            int index = timeCycle.indexOf("###");
            if (index != -1) {
                String period = timeCycle.substring(index + 3);
                timeCycle = timeCycle.substring(0, index);
                timer.setPeriod(period);
            }
            timer.setDelay(timeCycle);
            timer.setTimeType(Timer.TIME_CYCLE);
            compositeNode.addTimer(timer, timerAction("Timer-" + attachedTo + "-" + timeCycle + (timer.getPeriod() == null ? "" : "###" + timer.getPeriod()) + "-" + node.getId()));
        } else if (timeDate != null) {
            timer.setDate(timeDate);
            timer.setTimeType(Timer.TIME_DATE);
            compositeNode.addTimer(timer, timerAction("Timer-" + attachedTo + "-" + timeDate + "-" + node.getId()));
        }

        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction cancelAction = new DroolsConsequenceAction("java", null);
            cancelAction.setMetaData(ACTION, new CancelNodeInstanceAction(attachedTo));
            actions.add(cancelAction);
            ((EventNode) node).setActions(EVENT_NODE_EXIT, actions);
        }
    }

    protected void linkBoundarySignalEvent(Node node, String attachedTo) {
        boolean cancelActivity = (Boolean) node.getMetaData().get(CANCEL_ACTIVITY);
        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction action = new DroolsConsequenceAction("java", null);
            action.setMetaData(ACTION, new CancelNodeInstanceAction(attachedTo));
            actions.add(action);
            ((EventNode) node).setActions(EVENT_NODE_EXIT, actions);
        }
    }

    protected DroolsAction timerAction(String type) {
        DroolsAction signal = new DroolsAction();

        Action action = kcontext -> kcontext.getProcessInstance().signalEvent(type, kcontext.getNodeInstance().getId());
        signal.wire(action);

        return signal;
    }

    protected Node findNodeByIdOrUniqueIdInMetadata(NodeContainer nodeContainer, final String nodeRef, String errorMsg) {
        Node node = null;
        // try looking for a node with same "UniqueId" (in metadata)
        for (Node containerNode : nodeContainer.getNodes()) {
            if (nodeRef.equals(containerNode.getMetaData().get(UNIQUE_ID))) {
                node = containerNode;
                break;
            }
        }
        if (node == null) {
            throw new IllegalArgumentException(errorMsg);
        }
        return node;
    }

    private void postProcessNodes(RuleFlowProcess process, NodeContainer container) {

        for (Node node : container.getNodes()) {
            if (node instanceof NodeContainer) {
                // prepare event sub process
                if (node instanceof EventSubProcessNode) {
                    EventSubProcessNode eventSubProcessNode = (EventSubProcessNode) node;

                    Node[] nodes = eventSubProcessNode.getNodes();
                    for (Node subNode : nodes) {
                        // avoids cyclomatic complexity
                        if (subNode instanceof StartNode) {

                            processEventSubprocessStartNode(((StartNode) subNode), eventSubProcessNode);
                        }
                    }
                }
                postProcessNodes(process, (NodeContainer) node);
            }
        }
    }

    private void processEventSubprocessStartNode(StartNode subNode, EventSubProcessNode eventSubProcessNode) {
        List<Trigger> triggers = subNode.getTriggers();
        if (triggers != null) {

            for (Trigger trigger : triggers) {
                if (trigger instanceof EventTrigger) {
                    final List<EventFilter> filters = ((EventTrigger) trigger).getEventFilters();

                    for (EventFilter filter : filters) {
                        eventSubProcessNode.addEvent((EventTypeFilter) filter);
                    }
                }
            }
        }
    }
}
