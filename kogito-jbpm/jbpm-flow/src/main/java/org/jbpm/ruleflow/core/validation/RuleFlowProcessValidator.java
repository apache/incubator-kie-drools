/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.ruleflow.core.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.drools.core.time.impl.CronExpression;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.core.validation.ProcessValidator;
import org.jbpm.process.core.validation.impl.ProcessValidationErrorImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.CompositeNode.CompositeNodeEnd;
import org.jbpm.workflow.core.node.CompositeNode.NodeAndType;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachJoinNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachSplitNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.mvel2.ErrorDetail;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a RuleFlow validator.
 */
public class RuleFlowProcessValidator implements ProcessValidator {

    public static final String ASSOCIATIONS = "BPMN.Associations";

    // TODO: make this pluggable
    // TODO: extract generic process stuff and generic workflow stuff

    private static RuleFlowProcessValidator instance;

    private static final Logger logger = LoggerFactory.getLogger(RuleFlowProcessValidator.class);

    private RuleFlowProcessValidator() {
    }

    public static RuleFlowProcessValidator getInstance() {
        if (instance == null) {
            instance = new RuleFlowProcessValidator();
        }
        return instance;
    }

    public ProcessValidationError[] validateProcess(final RuleFlowProcess process) {
        final List<ProcessValidationError> errors = new ArrayList<ProcessValidationError>();

        if (process.getName() == null) {
            errors.add(new ProcessValidationErrorImpl(process,
                                                      "Process has no name."));
        }

        if (process.getId() == null || "".equals(process.getId())) {
            errors.add(new ProcessValidationErrorImpl(process,
                                                      "Process has no id."));
        }

        // check start node of process
        if (process.getStartNodes().isEmpty() && !process.isDynamic()) {
            errors.add(new ProcessValidationErrorImpl(process,
                                                      "Process has no start node."));
        }

        // Check end node of the process.
        if (process.getEndNodes().isEmpty() && !process.isDynamic()) {
            errors.add(new ProcessValidationErrorImpl(process,
                                                      "Process has no end node."));
        }

        validateNodes(process.getNodes(),
                      errors,
                      process);

        validateVariables(errors,
                          process);

        checkAllNodesConnectedToStart(process,
                                      process.isDynamic(),
                                      errors,
                                      process);

        return errors.toArray(new ProcessValidationError[errors.size()]);
    }

    private void validateNodes(Node[] nodes,
                               List<ProcessValidationError> errors,
                               RuleFlowProcess process) {
        String isForCompensation = "isForCompensation";
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            if (node instanceof StartNode) {
                final StartNode startNode = (StartNode) node;
                if (startNode.getTo() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Start has no outgoing connection.");
                }
                if (startNode.getTimer() != null) {
                    validateTimer(startNode.getTimer(),
                                  node,
                                  process,
                                  errors);
                }
            } else if (node instanceof EndNode) {
                final EndNode endNode = (EndNode) node;
                if (endNode.getFrom() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "End has no incoming connection.");
                }
                validateCompensationIntermediateOrEndEvent(endNode,
                                                           process,
                                                           errors);
            } else if (node instanceof RuleSetNode) {
                final RuleSetNode ruleSetNode = (RuleSetNode) node;
                if (ruleSetNode.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "RuleSet has no incoming connection.");
                }
                if (ruleSetNode.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "RuleSet has no outgoing connection.");
                }
                final String language = ruleSetNode.getLanguage();

                if (RuleSetNode.DRL_LANG.equals(language)) {
                    final String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
                    if (ruleFlowGroup == null || "".equals(ruleFlowGroup)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "RuleSet (DRL) has no ruleflow-group.");
                    }
                } else if (RuleSetNode.DMN_LANG.equals(language)) {
                    final String namespace = ruleSetNode.getNamespace();
                    if (namespace == null || "".equals(namespace)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "RuleSet (DMN) has no namespace.");
                    }
                    final String model = ruleSetNode.getModel();
                    if (model == null || "".equals(model)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "RuleSet (DMN) has no model.");
                    }
                } else {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Unsupported rule language '" + language + "'");
                }
                if (ruleSetNode.getTimers() != null) {
                    for (Timer timer : ruleSetNode.getTimers().keySet()) {
                        validateTimer(timer,
                                      node,
                                      process,
                                      errors);
                    }
                }
            } else if (node instanceof Split) {
                final Split split = (Split) node;
                if (split.getType() == Split.TYPE_UNDEFINED) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Split has no type.");
                }
                if (split.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Split has no incoming connection.");
                }
                if (split.getDefaultOutgoingConnections().size() < 2) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Split does not have more than one outgoing connection: " + split.getOutgoingConnections().size() + ".");
                }
                if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
                    for (final Iterator<Connection> it = split.getDefaultOutgoingConnections().iterator(); it.hasNext(); ) {
                        final Connection connection = it.next();
                        if (split.getConstraint(connection) == null && !split.isDefault(connection)
                                || (!split.isDefault(connection)
                                && (split.getConstraint(connection).getConstraint() == null
                                || split.getConstraint(connection).getConstraint().trim().length() == 0))) {
                            addErrorMessage(process,
                                            node,
                                            errors,
                                            "Split does not have a constraint for " + connection.toString() + ".");
                        }
                    }
                }
            } else if (node instanceof Join) {
                final Join join = (Join) node;
                if (join.getType() == Join.TYPE_UNDEFINED) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Join has no type.");
                }
                if (join.getDefaultIncomingConnections().size() < 2) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Join does not have more than one incoming connection: " + join.getIncomingConnections().size() + ".");
                }
                if (join.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Join has no outgoing connection.");
                }
                if (join.getType() == Join.TYPE_N_OF_M) {
                    String n = join.getN();
                    if (!n.startsWith("#{") || !n.endsWith("}")) {
                        try {
                            new Integer(n);
                        } catch (NumberFormatException e) {
                            addErrorMessage(process,
                                            node,
                                            errors,
                                            "Join has illegal n value: " + n);
                        }
                    }
                }
            } else if (node instanceof MilestoneNode) {
                final MilestoneNode milestone = (MilestoneNode) node;
                if (milestone.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Milestone has no incoming connection.");
                }

                if (milestone.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Milestone has no outgoing connection.");
                }
                if (milestone.getConstraint() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Milestone has no constraint.");
                }
                if (milestone.getTimers() != null) {
                    for (Timer timer : milestone.getTimers().keySet()) {
                        validateTimer(timer,
                                      node,
                                      process,
                                      errors);
                    }
                }
            } else if (node instanceof StateNode) {
                final StateNode stateNode = (StateNode) node;
                if (stateNode.getDefaultIncomingConnections().size() == 0 && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "State has no incoming connection");
                }
            } else if (node instanceof SubProcessNode) {
                final SubProcessNode subProcess = (SubProcessNode) node;
                if (subProcess.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "SubProcess has no incoming connection.");
                }
                if (subProcess.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    Object compensationObj = subProcess.getMetaData(isForCompensation);
                    if (compensationObj == null || !((Boolean) compensationObj)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "SubProcess has no outgoing connection.");
                    }
                }
                if (subProcess.getProcessId() == null && subProcess.getProcessName() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "SubProcess has no process id.");
                }
                if (subProcess.getTimers() != null) {
                    for (Timer timer : subProcess.getTimers().keySet()) {
                        validateTimer(timer,
                                      node,
                                      process,
                                      errors);
                    }
                }
                if (!subProcess.isIndependent() && !subProcess.isWaitForCompletion()) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "SubProcess you can only set " +
                                            "independent to 'false' only when 'Wait for completion' is set to true.");
                }
            } else if (node instanceof ActionNode) {
                final ActionNode actionNode = (ActionNode) node;
                if (actionNode.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Action has no incoming connection.");
                }
                if (actionNode.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    Object compensationObj = actionNode.getMetaData(isForCompensation);
                    if (compensationObj == null || !((Boolean) compensationObj)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Action has no outgoing connection.");
                    }
                }
                if (actionNode.getAction() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Action has no action.");
                } else if (actionNode.getAction() instanceof DroolsConsequenceAction) {
                    DroolsConsequenceAction droolsAction = (DroolsConsequenceAction) actionNode.getAction();
                    String actionString = droolsAction.getConsequence();
                    if (actionString == null) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Action has empty action.");
                    } else if ("mvel".equals(droolsAction.getDialect())) {
                        try {
                            ParserContext parserContext = new ParserContext();
                            //parserContext.setStrictTypeEnforcement(true);
                            ExpressionCompiler compiler = new ExpressionCompiler(actionString,
                                                                                 parserContext);
                            compiler.setVerifying(true);
                            compiler.compile();
                            List<ErrorDetail> mvelErrors = parserContext.getErrorList();
                            if (mvelErrors != null) {
                                for (Iterator<ErrorDetail> iterator = mvelErrors.iterator(); iterator.hasNext(); ) {
                                    ErrorDetail error = iterator.next();
                                    addErrorMessage(process,
                                                    node,
                                                    errors,
                                                    "Action has invalid action: " + error.getMessage() + ".");
                                }
                            }
                        } catch (Throwable t) {
                            addErrorMessage(process,
                                            node,
                                            errors,
                                            "Action has invalid action: " + t.getMessage() + ".");
                        }
                    }
                    // TODO: validation for "java" and "drools" scripts!
                    validateCompensationIntermediateOrEndEvent(actionNode,
                                                               process,
                                                               errors);
                }
            } else if (node instanceof WorkItemNode) {
                final WorkItemNode workItemNode = (WorkItemNode) node;
                if (workItemNode.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Task has no incoming connection.");
                }
                if (workItemNode.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    Object compensationObj = workItemNode.getMetaData(isForCompensation);
                    if (compensationObj == null || !((Boolean) compensationObj)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Task has no outgoing connection.");
                    }
                }
                if (workItemNode.getWork() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Task has no work specified.");
                } else {
                    Work work = workItemNode.getWork();
                    if (work.getName() == null || work.getName().trim().length() == 0) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Task has no task type.");
                    }
                }
                if (workItemNode.getTimers() != null) {
                    for (Timer timer : workItemNode.getTimers().keySet()) {
                        validateTimer(timer,
                                      node,
                                      process,
                                      errors);
                    }
                }
            } else if (node instanceof ForEachNode) {
                final ForEachNode forEachNode = (ForEachNode) node;
                String variableName = forEachNode.getVariableName();
                if (variableName == null || "".equals(variableName)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "ForEach has no variable name");
                }
                String collectionExpression = forEachNode.getCollectionExpression();
                if (collectionExpression == null || "".equals(collectionExpression)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "ForEach has no collection expression");
                }
                if (forEachNode.getDefaultIncomingConnections().size() == 0 && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "ForEach has no incoming connection");
                }
                if (forEachNode.getDefaultOutgoingConnections().size() == 0 && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "ForEach has no outgoing connection");
                }
                // TODO: check, if no linked connections, for start and end node(s)
//                if (forEachNode.getLinkedIncomingNode(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE) == null) {
//                    errors.add(new ProcessValidationErrorImpl(process,
//                        "ForEach node '%s' [%d] has no linked start node"));
//                }
//                if (forEachNode.getLinkedOutgoingNode(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE) == null) {
//                    errors.add(new ProcessValidationErrorImpl(process,
//                        "ForEach node '%s' [%d] has no linked end node"));
//                }
                final List<Node> start = RuleFlowProcess.getStartNodes(forEachNode.getNodes());
                if (start != null) {
                    for (Node s : start) {
                        if (((StartNode) s).getTriggers() != null && !((StartNode) s).getTriggers().isEmpty() || ((StartNode) s).getTimer() != null) {
                            addErrorMessage(process,
                                            node,
                                            errors,
                                            "MultiInstance subprocess can only have none start event.");
                        }
                    }
                }
                validateNodes(forEachNode.getNodes(),
                              errors,
                              process);
            } else if (node instanceof DynamicNode) {
                final DynamicNode dynamicNode = (DynamicNode) node;

                if (dynamicNode.getDefaultIncomingConnections().size() == 0 && !acceptsNoIncomingConnections(dynamicNode)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Dynamic has no incoming connection");
                }

                if (dynamicNode.getDefaultOutgoingConnections().size() == 0 && !acceptsNoOutgoingConnections(dynamicNode)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Dynamic has no outgoing connection");
                }

                if ("".equals(dynamicNode.getCompletionExpression()) && !dynamicNode.isAutoComplete()) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Dynamic has no completion condition set");
                }
                validateNodes(dynamicNode.getNodes(),
                              errors,
                              process);
            } else if (node instanceof CompositeNode) {
                final CompositeNode compositeNode = (CompositeNode) node;
                for (Map.Entry<String, NodeAndType> inType : compositeNode.getLinkedIncomingNodes().entrySet()) {
                    if (compositeNode.getIncomingConnections(inType.getKey()).size() == 0 && !acceptsNoIncomingConnections(node)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Composite has no incoming connection for type " + inType.getKey());
                    }
                    if (inType.getValue().getNode() == null && !acceptsNoOutgoingConnections(node)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Composite has invalid linked incoming node for type " + inType.getKey());
                    }
                }
                for (Map.Entry<String, NodeAndType> outType : compositeNode.getLinkedOutgoingNodes().entrySet()) {
                    if (compositeNode.getOutgoingConnections(outType.getKey()).size() == 0) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Composite has no outgoing connection for type " + outType.getKey());
                    }
                    if (outType.getValue().getNode() == null) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Composite has invalid linked outgoing node for type " + outType.getKey());
                    }
                }

                if (compositeNode.getLinkedIncomingNodes().values().size() < 1) {
                    boolean foundStartNode = false;

                    for (Node internalNode : compositeNode.getNodes()) {
                        if (internalNode instanceof StartNode) {
                            foundStartNode = true;
                        }
                    }

                    if (!foundStartNode) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Composite has no start node defined.");
                    }
                }

                if (compositeNode instanceof EventSubProcessNode) {
                    if (compositeNode.getIncomingConnections().size() > 0) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Event subprocess is not allowed to have any incoming connections.");
                    }
                    if (compositeNode.getOutgoingConnections().size() > 0) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Event subprocess is not allowed to have any outgoing connections.");
                    }
                    Node[] eventSubProcessNodes = compositeNode.getNodes();
                    int startEventCount = 0;
                    for (int j = 0; j < eventSubProcessNodes.length; ++j) {
                        if (eventSubProcessNodes[j] instanceof StartNode) {
                            StartNode startNode = (StartNode) eventSubProcessNodes[j];
                            if (++startEventCount == 2) {
                                addErrorMessage(process,
                                                compositeNode,
                                                errors,
                                                "Event subprocess is not allowed to have more than one start node.");
                            }
                            if (startNode.getTriggers() == null || startNode.getTriggers().isEmpty()) {
                                addErrorMessage(process,
                                                startNode,
                                                errors,
                                                "Start in Event SubProcess '" + compositeNode.getName() + "' [" + compositeNode.getId() + "] must contain a trigger (event definition).");
                            }
                        }
                    }
                } else {
                    Boolean isForCompensationObject = (Boolean) compositeNode.getMetaData("isForCompensation");
                    if (compositeNode.getIncomingConnections().size() == 0 && !Boolean.TRUE.equals(isForCompensationObject)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Embedded subprocess does not have incoming connection.");
                    }
                    if (compositeNode.getOutgoingConnections().size() == 0 && !Boolean.TRUE.equals(isForCompensationObject)) {
                        addErrorMessage(process,
                                        node,
                                        errors,
                                        "Embedded subprocess does not have outgoing connection.");
                    }

                    final List<Node> start = RuleFlowProcess.getStartNodes(compositeNode.getNodes());
                    if (start != null) {
                        for (Node s : start) {
                            if (((StartNode) s).getTriggers() != null && !((StartNode) s).getTriggers().isEmpty() || ((StartNode) s).getTimer() != null) {
                                addErrorMessage(process,
                                                node,
                                                errors,
                                                "Embedded subprocess can only have none start event.");
                            }
                        }
                    }
                }

                if (compositeNode.getTimers() != null) {
                    for (Timer timer : compositeNode.getTimers().keySet()) {
                        validateTimer(timer,
                                      node,
                                      process,
                                      errors);
                    }
                }
                validateNodes(compositeNode.getNodes(),
                              errors,
                              process);
            } else if (node instanceof EventNode) {
                final EventNode eventNode = (EventNode) node;
                if (eventNode.getEventFilters().size() == 0) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Event should specify an event type");
                }
                if (eventNode.getDefaultOutgoingConnections().size() == 0) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Event has no outgoing connection");
                } else {
                    List<EventFilter> eventFilters = eventNode.getEventFilters();
                    boolean compensationHandler = false;
                    for (EventFilter eventFilter : eventFilters) {
                        if (((EventTypeFilter) eventFilter).getType().startsWith("Compensation")) {
                            compensationHandler = true;
                            break;
                        }
                    }
                    if (compensationHandler && eventNode instanceof BoundaryEventNode) {
                        Connection connection = eventNode.getDefaultOutgoingConnections().get(0);
                        Boolean isAssociation = (Boolean) connection.getMetaData().get("association");
                        if (isAssociation == null) {
                            isAssociation = false;
                        }
                        if (!(eventNode.getDefaultOutgoingConnections().size() == 1 && connection != null && isAssociation)) {
                            addErrorMessage(process,
                                            node,
                                            errors,
                                            "Compensation Boundary Event is only allowed to have 1 association to 1 compensation activity.");
                        }
                    }
                }
            } else if (node instanceof FaultNode) {
                final FaultNode faultNode = (FaultNode) node;
                if (faultNode.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Fault has no incoming connection.");
                }
                if (faultNode.getFaultName() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Fault has no fault name.");
                }
            } else if (node instanceof TimerNode) {
                TimerNode timerNode = (TimerNode) node;
                if (timerNode.getFrom() == null && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Timer has no incoming connection.");
                }
                if (timerNode.getTo() == null && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Timer has no outgoing connection.");
                }
                if (timerNode.getTimer() == null) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Timer has no timer specified.");
                } else {
                    validateTimer(timerNode.getTimer(),
                                  node,
                                  process,
                                  errors);
                }
            } else if (node instanceof CatchLinkNode) {
                // catchlink validation here, there also are validations in
                // ProcessHandler regarding connection issues
            } else if (node instanceof ThrowLinkNode) {
                // throw validation here, there also are validations in
                // ProcessHandler regarding connection issues
            } else {
                errors.add(new ProcessValidationErrorImpl(process,
                                                          "Unknown node type '" + node.getClass().getName() + "'"));
            }
        }
    }

    private void checkAllNodesConnectedToStart(final NodeContainer container,
                                               boolean isDynamic,
                                               final List<ProcessValidationError> errors,
                                               RuleFlowProcess process) {
        final Map<Node, Boolean> processNodes = new HashMap<Node, Boolean>();
        final Node[] nodes;
        if (container instanceof CompositeNode) {
            nodes = ((CompositeNode) container).internalGetNodes();
        } else {
            nodes = container.getNodes();
        }
        List<Node> eventNodes = new ArrayList<Node>();
        List<CompositeNode> compositeNodes = new ArrayList<CompositeNode>();
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            processNodes.put(node,
                             Boolean.FALSE);
            if (node instanceof EventNode) {
                eventNodes.add(node);
            }
            if (node instanceof CompositeNode) {
                compositeNodes.add((CompositeNode) node);
            }
        }
        if (isDynamic) {
            for (Node node : nodes) {
                if (node.getIncomingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE).isEmpty()) {
                    processNode(node,
                                processNodes);
                }
            }
        } else {
            final List<Node> start = RuleFlowProcess.getStartNodes(nodes);
            if (start != null) {
                for (Node s : start) {
                    processNode(s,
                                processNodes);
                }
            }
            if (container instanceof CompositeNode) {
                for (CompositeNode.NodeAndType nodeAndTypes : ((CompositeNode) container).getLinkedIncomingNodes().values()) {
                    processNode(nodeAndTypes.getNode(),
                                processNodes);
                }
            }
        }
        for (Node eventNode : eventNodes) {
            processNode(eventNode,
                        processNodes);
        }
        for (CompositeNode compositeNode : compositeNodes) {
            checkAllNodesConnectedToStart(
                    compositeNode,
                    compositeNode instanceof DynamicNode,
                    errors,
                    process);
        }
        for (final Iterator<Node> it = processNodes.keySet().iterator(); it.hasNext(); ) {
            final Node node = it.next();
            if (Boolean.FALSE.equals(processNodes.get(node)) && !(node instanceof StartNode) && !(node instanceof EventSubProcessNode)) {
                addErrorMessage(process,
                                node,
                                errors,
                                "Has no connection to the start node.");
            }
        }
    }

    private void processNode(final Node node,
                             final Map<Node, Boolean> nodes) {
        if (!nodes.containsKey(node) && !((node instanceof CompositeNodeEnd) || (node instanceof ForEachSplitNode) || (node instanceof ForEachJoinNode))) {
            throw new IllegalStateException("A process node is connected with a node that does not belong to the process: " + node.getName());
        }
        final Boolean prevValue = (Boolean) nodes.put(node,
                                                      Boolean.TRUE);
        if (prevValue == Boolean.FALSE || prevValue == null) {
            for (final Iterator<List<Connection>> it = node.getOutgoingConnections().values().iterator(); it.hasNext(); ) {
                final List<Connection> list = it.next();
                for (final Iterator<Connection> it2 = list.iterator(); it2.hasNext(); ) {
                    processNode(it2.next().getTo(),
                                nodes);
                }
            }
        }
    }

    private boolean acceptsNoIncomingConnections(Node node) {
        NodeContainer nodeContainer = node.getNodeContainer();
        return nodeContainer instanceof DynamicNode ||
                (nodeContainer instanceof WorkflowProcess && ((WorkflowProcess) nodeContainer).isDynamic());
    }

    private boolean acceptsNoOutgoingConnections(Node node) {
        NodeContainer nodeContainer = node.getNodeContainer();
        return nodeContainer instanceof DynamicNode ||
                (nodeContainer instanceof WorkflowProcess && ((WorkflowProcess) nodeContainer).isDynamic());
    }

    private void validateTimer(final Timer timer,
                               final Node node,
                               final RuleFlowProcess process,
                               final List<ProcessValidationError> errors) {
        if (timer.getDelay() == null && timer.getDate() == null) {
            addErrorMessage(process,
                            node,
                            errors,
                            "Has timer with no delay or date specified.");
        } else {
            if (timer.getDelay() != null && !timer.getDelay().contains("#{")) {
                try {
                    switch (timer.getTimeType()) {
                        case Timer.TIME_CYCLE:
                            if (CronExpression.isValidExpression(timer.getDelay())) {

                            } else {

                                // when using ISO date/time period is not set
                                DateTimeUtils.parseRepeatableDateTime(timer.getDelay());
                            }

                            break;
                        case Timer.TIME_DURATION:

                            DateTimeUtils.parseDuration(timer.getDelay());

                            break;
                        case Timer.TIME_DATE:
                            DateTimeUtils.parseDateAsDuration(timer.getDate());

                            break;

                        default:
                            break;
                    }
                } catch (RuntimeException e) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Could not parse delay '" + timer.getDelay() + "': " + e.getMessage());
                }
            }
        }
        if (timer.getPeriod() != null) {
            if (!timer.getPeriod().contains("#{")) {
                try {
                    if (CronExpression.isValidExpression(timer.getPeriod())) {

                    } else {
                        // when using ISO date/time period is not set
                        DateTimeUtils.parseRepeatableDateTime(timer.getPeriod());
                    }
                } catch (RuntimeException e) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Could not parse period '" + timer.getPeriod() + "': " + e.getMessage());
                }
            }
        }

        if (timer.getDate() != null) {
            if (!timer.getDate().contains("#{")) {
                try {

                    DateTimeUtils.parseDateAsDuration(timer.getDate());
                } catch (RuntimeException e) {
                    addErrorMessage(process,
                                    node,
                                    errors,
                                    "Could not parse date '" + timer.getDate() + "': " + e.getMessage());
                }
            }
        }
    }

    public ProcessValidationError[] validateProcess(Process process) {
        if (!(process instanceof RuleFlowProcess)) {
            throw new IllegalArgumentException(
                    "This validator can only validate ruleflow processes!");
        }
        return validateProcess((RuleFlowProcess) process);
    }

    private void validateVariables(List<ProcessValidationError> errors,
                                   RuleFlowProcess process) {

        List<Variable> variables = process.getVariableScope().getVariables();

        if (variables != null) {
            for (Variable var : variables) {
                DataType varDataType = var.getType();
                if (varDataType == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                                                              "Variable '" + var.getName() + "' has no type."));
                }
            }
        }
    }

    @Override
    public boolean accept(Process process,
                          Resource resource) {
        if (RuleFlowProcess.RULEFLOW_TYPE.equals(process.getType())) {
            return true;
        }
        return false;
    }

    protected void validateCompensationIntermediateOrEndEvent(Node node,
                                                              RuleFlowProcess process,
                                                              List<ProcessValidationError> errors) {
        if (node.getMetaData().containsKey("Compensation")) {
            // Validate that activityRef in throw/end compensation event refers to "visible" compensation
            String activityRef = (String) node.getMetaData().get("Compensation");
            Node refNode = null;
            if (activityRef != null) {
                Queue<Node> nodeQueue = new LinkedList<Node>();
                nodeQueue.addAll(Arrays.asList(process.getNodes()));
                while (!nodeQueue.isEmpty()) {
                    Node polledNode = nodeQueue.poll();
                    if (activityRef.equals(polledNode.getMetaData().get("UniqueId"))) {
                        refNode = polledNode;
                        break;
                    }
                    if (node instanceof NodeContainer) {
                        nodeQueue.addAll(Arrays.asList(((NodeContainer) node).getNodes()));
                    }
                }
            }
            if (refNode == null) {
                addErrorMessage(process,
                                node,
                                errors,
                                "Does not reference an activity that exists (" + activityRef
                                        + ") in its compensation event definition.");
            }

            CompensationScope compensationScope
                    = (CompensationScope) ((NodeImpl) node).resolveContext(CompensationScope.COMPENSATION_SCOPE,
                                                                           activityRef);
            if (compensationScope == null) {
                addErrorMessage(process,
                                node,
                                errors,
                                "References an activity (" + activityRef
                                        + ") in its compensation event definition that is not visible to it.");
            }
        }
    }

    @Override
    public boolean compilationSupported() {
        return true;
    }

    protected void addErrorMessage(RuleFlowProcess process,
                                   Node node,
                                   List<ProcessValidationError> errors,
                                   String message) {
        String error = String.format("Node '%s' [%d] " + message,
                                     node.getName(),
                                     node.getId());
        errors.add(new ProcessValidationErrorImpl(process,
                                                  error));
    }
}