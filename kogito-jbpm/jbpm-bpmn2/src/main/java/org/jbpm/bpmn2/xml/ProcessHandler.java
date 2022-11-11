/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Collaboration;
import org.jbpm.bpmn2.core.CorrelationKey;
import org.jbpm.bpmn2.core.CorrelationProperty;
import org.jbpm.bpmn2.core.CorrelationSubscription;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Expression;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.bpmn2.core.Signal;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.CompensationHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.correlation.CorrelationManager;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.event.MVELMessageExpressionEvaluator;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.impl.actions.CancelNodeInstanceAction;
import org.jbpm.process.instance.impl.actions.ProcessInstanceCompensationAction;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.Connection;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ConstraintTrigger;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.Trigger;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);

    public static final String CURRENT_PROCESS = "BPMN.Process";
    public static final String CONNECTIONS = "BPMN.Connections";
    public static final String LINKS = "BPMN.ThrowLinks";
    public static final String ASSOCIATIONS = "BPMN.Associations";
    public static final String ERRORS = "BPMN.Errors";
    public static final String ESCALATIONS = "BPMN.Escalations";

    @SuppressWarnings("unchecked")
    public ProcessHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet();
            this.validParents.add(Definitions.class);

            this.validPeers = new HashSet();
            this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);
            this.validPeers.add(Escalation.class);
            this.validPeers.add(Error.class);
            this.validPeers.add(Signal.class);
            this.validPeers.add(DataStore.class);
            this.validPeers.add(RuleFlowProcess.class);

            this.allowNesting = false;
        }
    }

    @Override
    public Object start(final String uri, final String localName,
            final Attributes attrs, final Parser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String id = attrs.getValue("id");
        String name = attrs.getValue("name");
        String visibility = attrs.getValue("processType");
        String packageName = attrs.getValue("http://www.jboss.org/drools", "packageName");
        String dynamic = attrs.getValue("http://www.jboss.org/drools", "adHoc");
        String version = attrs.getValue("http://www.jboss.org/drools", "version");

        RuleFlowProcess process = new RuleFlowProcess();
        process.setAutoComplete(true);
        process.setId(id);
        if (name == null) {
            name = id;
        }
        process.setName(name);
        process.setType(KogitoWorkflowProcess.BPMN_TYPE);
        if (packageName == null) {
            packageName = "org.drools.bpmn2";
        }
        process.setPackageName(packageName);
        if ("true".equals(dynamic)) {
            process.setDynamic(true);
            process.setAutoComplete(false);
        }
        if (version != null) {
            process.setVersion(version);
        }
        if (visibility == null || "".equals(visibility)) {
            visibility = KogitoWorkflowProcess.NONE_VISIBILITY;
        }
        process.setVisibility(visibility);
        ((ProcessBuildData) parser.getData()).setMetaData(CURRENT_PROCESS, process);
        ((ProcessBuildData) parser.getData()).addProcess(process);
        // register the definitions object as metadata of process.
        process.setMetaData("Definitions", parser.getParent());
        // register bpmn2 imports as meta data of process
        Object typedImports = ((ProcessBuildData) parser.getData()).getMetaData("Bpmn2Imports");
        if (typedImports != null) {
            process.setMetaData("Bpmn2Imports", typedImports);
        }
        // register item definitions as meta data of process
        Object itemDefinitions = ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
        if (itemDefinitions != null) {
            process.setMetaData("ItemDefinitions", itemDefinitions);
        }

        // for unique id's of nodes, start with one to avoid returning wrong nodes for dynamic nodes
        parser.getMetaData().put("idGen", new AtomicInteger(1));
        parser.getMetaData().put("CurrentProcessDefinition", process);
        process.getCorrelationManager().setClassLoader(parser.getClassLoader());
        return process;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object end(final String uri, final String localName,
            final Parser parser) throws SAXException {
        parser.endElementBuilder();

        RuleFlowProcess process = (RuleFlowProcess) parser.getCurrent();
        List<IntermediateLink> throwLinks = (List<IntermediateLink>) process
                .getMetaData(LINKS);
        linkIntermediateLinks(process, throwLinks);

        List<SequenceFlow> connections = (List<SequenceFlow>) process.getMetaData(CONNECTIONS);
        linkConnections(process, connections);
        linkBoundaryEvents(process);

        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) process.getMetaData(ASSOCIATIONS);
        linkAssociations((Definitions) process.getMetaData("Definitions"), process, associations);

        List<Lane> lanes = (List<Lane>) process.getMetaData(LaneHandler.LANES);
        assignLanes(process, lanes);
        postProcessNodes(process, process);
        postProcessCollaborations(process, parser);
        return process;
    }

    private void postProcessCollaborations(RuleFlowProcess process, Parser parser) {
        // now we wire correlation process subscriptions
        CorrelationManager correlationManager = process.getCorrelationManager();
        for (Message message : HandlerUtil.messages(parser).values()) {
            correlationManager.newMessage(message.getId(), message.getName(), message.getType());
        }

        // only the ones this process is member of
        List<Collaboration> collaborations = HandlerUtil.collaborations(parser).values().stream().filter(c -> c.getProcessesRef().contains(process.getId())).collect(Collectors.toList());
        for (Collaboration collaboration : collaborations) {
            for (CorrelationKey key : collaboration.getCorrelationKeys()) {

                correlationManager.newCorrelation(key.getId(), key.getName());
                List<CorrelationProperty> properties = key.getPropertiesRef().stream().map(k -> HandlerUtil.correlationProperties(parser).get(k)).collect(Collectors.toList());
                for (CorrelationProperty correlationProperty : properties) {
                    correlationProperty.getMessageRefs().forEach(messageRef -> {

                        // for now only MVEL expressions
                        MVELMessageExpressionEvaluator evaluator = new MVELMessageExpressionEvaluator(correlationProperty.getRetrievalExpression(messageRef).getScript());
                        correlationManager.addMessagePropertyExpression(key.getId(), messageRef, correlationProperty.getId(), evaluator);
                    });
                }
            }
        }

        // we create the correlations
        for (CorrelationSubscription subscription : HandlerUtil.correlationSubscription(process).values()) {
            correlationManager.subscribeTo(subscription.getCorrelationKeyRef());
            for (Map.Entry<String, Expression> binding : subscription.getPropertyExpressions().entrySet()) {
                MVELMessageExpressionEvaluator evaluator = new MVELMessageExpressionEvaluator(binding.getValue().getScript());
                correlationManager.addProcessSubscriptionPropertyExpression(subscription.getCorrelationKeyRef(), binding.getKey(), evaluator);
            }
        }
    }

    public static void linkIntermediateLinks(NodeContainer process, List<IntermediateLink> links) {
        if (links == null) {
            return;
        }
        Map<String, IntermediateLink> catchLinks = new HashMap<>();
        Map<String, Collection<IntermediateLink>> throwLinks = new HashMap<>();
        Collection<IntermediateLink> noNameLinks = new ArrayList<>();
        Collection<IntermediateLink> duplicatedTarget = new LinkedHashSet<>();
        Collection<IntermediateLink> unconnectedTarget = new ArrayList<>();

        // collect errors and nodes in first loop
        for (IntermediateLink link : links) {
            if (link.getName() == null || link.getName().isEmpty()) {
                noNameLinks.add(link);
            } else if (link.isThrowLink()) {
                throwLinks.computeIfAbsent(link.getName(), s -> new ArrayList<>()).add(link);
            } else {
                IntermediateLink duplicateLink = catchLinks.putIfAbsent(link.getName(), link);
                if (duplicateLink != null) {
                    duplicatedTarget.add(duplicateLink);
                    duplicatedTarget.add(link);
                }
            }
        }

        // second loop for connection
        for (IntermediateLink catchLink : catchLinks.values()) {
            Collection<IntermediateLink> associatedLinks = throwLinks.remove(catchLink.getName());
            if (associatedLinks != null) {
                // connect throw to catch
                Node catchNode = findNodeByIdOrUniqueIdInMetadata(process, catchLink.getUniqueId());
                if (catchNode != null) {
                    for (IntermediateLink throwLink : associatedLinks) {
                        Node throwNode = findNodeByIdOrUniqueIdInMetadata(process,
                                throwLink.getUniqueId());
                        if (throwNode != null) {
                            Connection result = new ConnectionImpl(throwNode,
                                    org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, catchNode,
                                    org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                            result.setMetaData("linkNodeHidden", "yes");
                        }
                    }
                }
            } else {
                unconnectedTarget.add(catchLink);
            }
        }

        // throw exception if any error (this is done at the end of the process to show the user as much errors as possible) 
        StringBuilder errors = new StringBuilder();
        if (!noNameLinks.isEmpty()) {
            formatError(errors, "These nodes do not have a name ", noNameLinks.stream(), process);
        }
        if (!duplicatedTarget.isEmpty()) {
            formatError(errors, "\nThere are multiple catch nodes with the same name ", duplicatedTarget.stream(),
                    process);
        }
        if (!unconnectedTarget.isEmpty()) {
            formatError(errors, "\nThere is not connection from any throw link to these catch links ", unconnectedTarget
                    .stream(), process);
        }
        if (!throwLinks.isEmpty()) {
            formatError(errors, "\nThere is not connection to any catch link from these throw links ", throwLinks
                    .values()
                    .stream()
                    .flatMap(Collection::stream), process);
        }
        if (errors.length() > 0) {
            throw new ProcessParsingValidationException(errors.toString());
        }

    }

    private static void formatError(StringBuilder errors,
            String message,
            Stream<IntermediateLink> stream,
            NodeContainer container) {
        errors.append(message).append(stream.map(IntermediateLink::getUniqueId).collect(Collectors.joining(", ", "{",
                "}")));
        if (container instanceof Process) {
            errors.append(" for process ").append(((Process) container).getId());
        } else if (container instanceof Node) {
            errors.append(" for subprocess ").append(((Node) container).getId());
        }
    }

    private static Object findNodeOrDataStoreByUniqueId(Definitions definitions, NodeContainer nodeContainer, final String nodeRef, String errorMsg) {
        if (definitions != null) {
            List<DataStore> dataStores = definitions.getDataStores();
            if (dataStores != null) {
                for (DataStore dataStore : dataStores) {
                    if (nodeRef.equals(dataStore.getId())) {
                        return dataStore;
                    }
                }
            }
        }
        return findNodeByIdOrUniqueIdInMetadata(nodeContainer, nodeRef, errorMsg);
    }

    private static Node findNodeByIdOrUniqueIdInMetadata(
            NodeContainer nodeContainer, String targetRef) {
        return findNodeByIdOrUniqueIdInMetadata(nodeContainer, targetRef, "Could not find target node for connection:" + targetRef);
    }

    private static Node findNodeByIdOrUniqueIdInMetadata(NodeContainer nodeContainer, final String nodeRef, String errorMsg) {
        Node node = null;
        // try looking for a node with same "UniqueId" (in metadata)
        for (Node containerNode : nodeContainer.getNodes()) {
            if (nodeRef.equals(containerNode.getMetaData().get("UniqueId"))) {
                node = containerNode;
                break;
            }
        }
        if (node == null) {
            throw new ProcessParsingValidationException(errorMsg);
        }
        return node;
    }

    @Override
    public Class<?> generateNodeFor() {
        return RuleFlowProcess.class;
    }

    public static void linkConnections(NodeContainer nodeContainer, List<SequenceFlow> connections) {
        if (connections != null) {
            for (SequenceFlow connection : connections) {
                String sourceRef = connection.getSourceRef();
                Node source = findNodeByIdOrUniqueIdInMetadata(nodeContainer, sourceRef, "Could not find source node for connection:" + sourceRef);

                if (source instanceof EventNode) {
                    for (EventFilter eventFilter : ((EventNode) source).getEventFilters()) {
                        if (eventFilter instanceof EventTypeFilter) {
                            if ("Compensation".equals(((EventTypeFilter) eventFilter).getType())) {
                                // While this isn't explicitly stated in the spec,
                                // BPMN Method & Style, 2nd Ed. (Silver), states this on P. 131
                                throw new ProcessParsingValidationException(
                                        "A Compensation Boundary Event can only be *associated* with a compensation activity via an Association, not via a Sequence Flow element.");
                            }
                        }
                    }
                }

                String targetRef = connection.getTargetRef();
                Node target = findNodeByIdOrUniqueIdInMetadata(nodeContainer, targetRef, "Could not find target node for connection:" + targetRef);

                Connection result = new ConnectionImpl(
                        source, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
                        target, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                result.setMetaData("bendpoints", connection.getBendpoints());
                result.setMetaData("UniqueId", connection.getId());

                if ("true".equals(System.getProperty("jbpm.enable.multi.con"))) {
                    NodeImpl nodeImpl = (NodeImpl) source;
                    Constraint constraint = buildConstraint(connection, nodeImpl);
                    if (constraint != null) {
                        nodeImpl.addConstraint(new ConnectionRef(connection.getId(), target.getId(), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE),
                                constraint);
                    }

                } else if (source instanceof Split) {
                    Split split = (Split) source;
                    Constraint constraint = buildConstraint(connection, split);
                    split.addConstraint(
                            new ConnectionRef(connection.getId(), target.getId(), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE),
                            constraint);
                }
            }
        }
    }

    public static void linkBoundaryEvents(NodeContainer nodeContainer) {
        for (Node node : nodeContainer.getNodes()) {
            if (node instanceof EventNode) {
                final String attachedTo = (String) node.getMetaData().get("AttachedTo");
                if (attachedTo != null) {
                    for (EventFilter filter : ((EventNode) node).getEventFilters()) {
                        String type = ((EventTypeFilter) filter).getType();
                        Node attachedNode = findNodeByIdOrUniqueIdInMetadata(nodeContainer, attachedTo, "Could not find node to attach to: " + attachedTo);

                        // 
                        if (!(attachedNode instanceof StateBasedNode) && !type.equals("Compensation")) {
                            throw new ProcessParsingValidationException("Boundary events are supported only on StateBasedNode, found node: "
                                    + attachedNode.getClass().getName() + " [" + attachedNode.getMetaData().get("UniqueId") + "]");
                        }

                        if (type.startsWith("Escalation")) {
                            linkBoundaryEscalationEvent(node, attachedTo, attachedNode);
                        } else if (type.startsWith("Error-")) {
                            linkBoundaryErrorEvent(node, attachedTo, attachedNode);
                        } else if (type.startsWith("Timer-")) {
                            linkBoundaryTimerEvent(node, attachedTo, attachedNode);
                        } else if (type.equals("Compensation")) {
                            linkBoundaryCompensationEvent(node);
                        } else if (node.getMetaData().get("SignalName") != null || type.startsWith("Message-")) {
                            linkBoundarySignalEvent(node, attachedTo);
                        } else if (type.startsWith("Condition-")) {
                            linkBoundaryConditionEvent(nodeContainer, node, attachedTo);
                        }
                    }
                }
            }
        }
    }

    private static void linkBoundaryEscalationEvent(Node node, String attachedTo, Node attachedNode) {
        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
        String escalationCode = (String) node.getMetaData().get("EscalationEvent");
        String escalationStructureRef = (String) node.getMetaData().get("EscalationStructureRef");

        ContextContainer compositeNode = (ContextContainer) attachedNode;
        ExceptionScope exceptionScope = (ExceptionScope) compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
        if (exceptionScope == null) {
            exceptionScope = new ExceptionScope();
            compositeNode.addContext(exceptionScope);
            compositeNode.setDefaultContext(exceptionScope);
        }

        String variable = ((EventNode) node).getVariableName();
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        DroolsConsequenceAction action =
                createJavaAction(new SignalProcessInstanceAction("Escalation-" + attachedTo + "-" + escalationCode, variable, null, SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
        exceptionHandler.setAction(action);
        exceptionHandler.setFaultVariable(variable);
        exceptionScope.setExceptionHandler(escalationCode, exceptionHandler);
        if (escalationStructureRef != null) {
            exceptionScope.setExceptionHandler(escalationStructureRef, exceptionHandler);
        }

        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction cancelAction = new DroolsConsequenceAction("java", "");
            cancelAction.setMetaData("Action", new CancelNodeInstanceAction(attachedTo));
            actions.add(cancelAction);
            ((EventNode) node).setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
        }
    }

    private static void linkBoundaryErrorEvent(Node node, String attachedTo, Node attachedNode) {
        ContextContainer compositeNode = (ContextContainer) attachedNode;
        ExceptionScope exceptionScope = (ExceptionScope) compositeNode.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
        if (exceptionScope == null) {
            exceptionScope = new ExceptionScope();
            compositeNode.addContext(exceptionScope);
            compositeNode.setDefaultContext(exceptionScope);
        }
        String errorCode = (String) node.getMetaData().get("ErrorEvent");
        boolean hasErrorCode = (Boolean) node.getMetaData().get("HasErrorEvent");
        String errorStructureRef = (String) node.getMetaData().get("ErrorStructureRef");
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();

        String variable = ((EventNode) node).getVariableName();
        SignalProcessInstanceAction signalAction = new SignalProcessInstanceAction("Error-" + attachedTo + "-" + errorCode, variable, null, SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE);
        DroolsConsequenceAction action = createJavaAction(signalAction);
        exceptionHandler.setAction(action);
        exceptionHandler.setFaultVariable(variable);
        exceptionScope.setExceptionHandler(hasErrorCode ? errorCode : null, exceptionHandler);
        if (errorStructureRef != null) {
            exceptionScope.setExceptionHandler(errorStructureRef, exceptionHandler);
        }

        List<DroolsAction> actions = ((EventNode) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
        if (actions == null) {
            actions = new ArrayList<>();
        }
        DroolsConsequenceAction cancelAction = new DroolsConsequenceAction("java", null);
        cancelAction.setMetaData("Action", new CancelNodeInstanceAction(attachedTo));
        actions.add(cancelAction);
        ((EventNode) node).setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
    }

    private static void linkBoundaryTimerEvent(Node node, String attachedTo, Node attachedNode) {
        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
        StateBasedNode compositeNode = (StateBasedNode) attachedNode;
        String timeDuration = (String) node.getMetaData().get("TimeDuration");
        String timeCycle = (String) node.getMetaData().get("TimeCycle");
        String timeDate = (String) node.getMetaData().get("TimeDate");
        Timer timer = new Timer();
        if (timeDuration != null) {
            timer.setDelay(timeDuration);
            timer.setTimeType(Timer.TIME_DURATION);
            DroolsConsequenceAction consequenceAction = createJavaAction(new SignalProcessInstanceAction("Timer-" + attachedTo + "-" + timeDuration + "-" + node.getId(),
                    kcontext -> kcontext.getNodeInstance().getStringId(), SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
            compositeNode.addTimer(timer, consequenceAction);
        } else if (timeCycle != null) {
            int index = timeCycle.indexOf("###");
            if (index != -1) {
                String period = timeCycle.substring(index + 3);
                timeCycle = timeCycle.substring(0, index);
                timer.setPeriod(period);
            }
            timer.setDelay(timeCycle);
            timer.setTimeType(Timer.TIME_CYCLE);

            String finalTimeCycle = timeCycle;

            DroolsConsequenceAction action =
                    createJavaAction(new SignalProcessInstanceAction("Timer-" + attachedTo + "-" + finalTimeCycle + (timer.getPeriod() == null ? "" : "###" + timer.getPeriod()) + "-" + node.getId(),
                            kcontext -> kcontext.getNodeInstance().getStringId(), SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
            compositeNode.addTimer(timer, action);
        } else if (timeDate != null) {
            timer.setDate(timeDate);
            timer.setTimeType(Timer.TIME_DATE);
            DroolsConsequenceAction action = createJavaAction(new SignalProcessInstanceAction("Timer-" + attachedTo + "-" + timeDate + "-" + node.getId(),
                    kcontext -> kcontext.getNodeInstance().getStringId(), SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
            compositeNode.addTimer(timer, action);
        }

        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction action = createJavaAction(new CancelNodeInstanceAction(attachedTo));
            actions.add(action);
            ((EventNode) node).setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
        }
    }

    private static void linkBoundaryCompensationEvent(Node node) {
        /**
         * BPMN2 Spec, p. 264:
         * "For an Intermediate event attached to the boundary of an activity:"
         * ...
         * The Activity the Event is attached to will provide the Id necessary
         * to match the Compensation Event with the Event that threw the compensation"
         * 
         * In other words: "activityRef" is and should be IGNORED
         */

        String activityRef = (String) node.getMetaData().get("ActivityRef");
        if (activityRef != null) {
            logger.warn("Attribute activityRef={} will be IGNORED since this is a Boundary Compensation Event.", activityRef);
        }

        // linkAssociations takes care of the rest
    }

    private static void linkBoundarySignalEvent(Node node, String attachedTo) {
        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction action = createJavaAction(new CancelNodeInstanceAction(attachedTo));
            actions.add(action);
            ((EventNode) node).setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
        }
    }

    private static void linkBoundaryConditionEvent(NodeContainer nodeContainer, Node node, String attachedTo) {
        String processId = ((RuleFlowProcess) nodeContainer).getId();
        String eventType = "RuleFlowStateEvent-" + processId + "-" + ((EventNode) node).getUniqueId() + "-" + attachedTo;
        ((EventTypeFilter) ((EventNode) node).getEventFilters().get(0)).setType(eventType);
        boolean cancelActivity = (Boolean) node.getMetaData().get("CancelActivity");
        if (cancelActivity) {
            List<DroolsAction> actions = ((EventNode) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
            if (actions == null) {
                actions = new ArrayList<>();
            }
            DroolsConsequenceAction action = createJavaAction(new CancelNodeInstanceAction(attachedTo));
            actions.add(action);
            ((EventNode) node).setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
        }
    }

    public static void linkAssociations(Definitions definitions, NodeContainer nodeContainer, List<Association> associations) {
        if (associations != null) {
            for (Association association : associations) {
                String sourceRef = association.getSourceRef();
                Object source = null;
                try {
                    source = findNodeOrDataStoreByUniqueId(definitions, nodeContainer, sourceRef,
                            "Could not find source [" + sourceRef + "] for association " + association.getId() + "]");
                } catch (IllegalArgumentException e) {
                    // source not found
                }
                String targetRef = association.getTargetRef();
                Object target = null;
                try {
                    target = findNodeOrDataStoreByUniqueId(definitions, nodeContainer, targetRef,
                            "Could not find target [" + targetRef + "] for association [" + association.getId() + "]");
                } catch (IllegalArgumentException e) {
                    // target not found
                }
                if (source == null || target == null) {
                    // TODO: ignoring this association for now
                } else if (target instanceof DataStore || source instanceof DataStore) {
                    // TODO: ignoring data store associations for now
                } else if (source instanceof EventNode) {
                    EventNode sourceNode = (EventNode) source;
                    KogitoNode targetNode = (KogitoNode) target;
                    checkBoundaryEventCompensationHandler(association, sourceNode, targetNode);

                    // make sure IsForCompensation is set to true on target
                    NodeImpl targetNodeImpl = (NodeImpl) target;
                    String isForCompensation = "isForCompensation";
                    Object compensationObject = targetNodeImpl.getMetaData(isForCompensation);
                    if (compensationObject == null) {
                        targetNodeImpl.setMetaData(isForCompensation, true);
                        logger.warn("Setting {} attribute to true for node {}", isForCompensation, targetRef);
                    } else if (!Boolean.parseBoolean(compensationObject.toString())) {
                        throw new ProcessParsingValidationException(isForCompensation + " attribute [" + compensationObject + "] should be true for Compensation Activity [" + targetRef + "]");
                    }

                    // put Compensation Handler in CompensationHandlerNode
                    NodeContainer sourceParent = sourceNode.getParentContainer();
                    NodeContainer targetParent = targetNode.getParentContainer();
                    if (!sourceParent.equals(targetParent)) {
                        throw new ProcessParsingValidationException("Compensation Associations may not cross (sub-)process boundaries,");
                    }

                    // connect boundary event to compensation activity
                    ConnectionImpl connection = new ConnectionImpl(sourceNode, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, targetNode, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
                    connection.setMetaData("UniqueId", null);
                    connection.setMetaData("hidden", true);
                    connection.setMetaData("association", true);

                    // Compensation use cases: 
                    // - boundary event --associated-> activity
                    // - implicit sub process compensation handler + recursive? 

                    /**
                     * BPMN2 spec, p.442:
                     * "A Compensation Event Sub-process becomes enabled when its parent Activity transitions into state
                     * Completed. At that time, a snapshot of the data associated with the parent Acitivity is taken and kept for
                     * later usage by the Compensation Event Sub-Process."
                     */
                }
            }
        }
    }

    /**
     * This logic belongs in {@link RuleFlowProcessValidator} -- except that {@link Association}s are a jbpm-bpmn2 class,
     * and {@link RuleFlowProcessValidator} is a jbpm-flow class..
     * </p>
     * Maybe we should have a BPMNProcessValidator class?
     * 
     * @param association The association to check.
     * @param source The source of the association.
     * @param target The target of the association.
     */
    private static void checkBoundaryEventCompensationHandler(Association association, Node source, Node target) {
        // check that 
        // - event node is boundary event node
        if (!(source instanceof BoundaryEventNode)) {
            throw new ProcessParsingValidationException("(Compensation) activities may only be associated with Boundary Event Nodes (not with" +
                    source.getClass().getSimpleName() + " nodes [node " + ((String) source.getMetaData().get("UniqueId")) + "].");
        }
        BoundaryEventNode eventNode = (BoundaryEventNode) source;

        // - event node has compensationEvent
        List<EventFilter> eventFilters = eventNode.getEventFilters();
        boolean compensationCheckPassed = false;
        if (eventFilters != null) {
            for (EventFilter filter : eventFilters) {
                if (filter instanceof EventTypeFilter) {
                    String type = ((EventTypeFilter) filter).getType();
                    if (type != null && type.equals("Compensation")) {
                        compensationCheckPassed = true;
                    }
                }
            }
        }

        if (!compensationCheckPassed) {
            throw new ProcessParsingValidationException("An Event [" + ((String) eventNode.getMetaData("UniqueId"))
                    + "] linked from an association [" + association.getId()
                    + "] must be a (Boundary) Compensation Event.");
        }

        // - boundary event node is attached to the correct type of node? 
        /**
         * Tasks:
         * business: RuleSetNode
         * manual: WorkItemNode
         * receive: WorkItemNode
         * script: ActionNode
         * send: WorkItemNode
         * service: WorkItemNode
         * task: WorkItemNode
         * user: HumanTaskNode
         */
        String attachedToId = eventNode.getAttachedToNodeId();
        Node attachedToNode = null;
        for (Node node : eventNode.getParentContainer().getNodes()) {
            if (attachedToId.equals(node.getMetaData().get("UniqueId"))) {
                attachedToNode = node;
                break;
            }
        }
        if (attachedToNode == null) {
            throw new ProcessParsingValidationException("Boundary Event [" + ((String) eventNode.getMetaData("UniqueId"))
                    + "] is not attached to a node [" + attachedToId + "] that can be found.");
        }
        if (!(attachedToNode instanceof RuleSetNode
                || attachedToNode instanceof WorkItemNode
                || attachedToNode instanceof ActionNode
                || attachedToNode instanceof HumanTaskNode
                || attachedToNode instanceof CompositeNode
                || attachedToNode instanceof SubProcessNode)) {
            throw new ProcessParsingValidationException("Compensation Boundary Event [" + ((String) eventNode.getMetaData("UniqueId"))
                    + "] must be attached to a task or sub-process.");
        }

        // - associated node is a task or subProcess
        compensationCheckPassed = false;
        if (target instanceof WorkItemNode || target instanceof HumanTaskNode
                || target instanceof CompositeContextNode || target instanceof SubProcessNode) {
            compensationCheckPassed = true;
        } else if (target instanceof ActionNode) {
            Object nodeTypeObj = ((ActionNode) target).getMetaData("NodeType");
            if (nodeTypeObj != null && nodeTypeObj.equals("ScriptTask")) {
                compensationCheckPassed = true;
            }
        }
        if (!compensationCheckPassed) {
            throw new ProcessParsingValidationException("An Activity ["
                    + ((String) ((NodeImpl) target).getMetaData("UniqueId")) +
                    "] associated with a Boundary Compensation Event must be a Task or a (non-Event) Sub-Process");
        }

        // - associated node does not have outgoingConnections of it's own
        compensationCheckPassed = true;
        NodeImpl targetNode = (NodeImpl) target;
        Map<String, List<org.kie.api.definition.process.Connection>> connectionsMap = targetNode.getOutgoingConnections();
        ConnectionImpl outgoingConnection = null;
        for (String connectionType : connectionsMap.keySet()) {
            List<org.kie.api.definition.process.Connection> connections = connectionsMap.get(connectionType);
            if (connections != null && !connections.isEmpty()) {
                for (org.kie.api.definition.process.Connection connection : connections) {
                    Object hiddenObj = connection.getMetaData().get("hidden");
                    if (hiddenObj != null && ((Boolean) hiddenObj)) {
                        continue;
                    }
                    outgoingConnection = (ConnectionImpl) connection;
                    compensationCheckPassed = false;
                    break;
                }
            }
        }
        if (!compensationCheckPassed) {
            throw new ProcessParsingValidationException("A Compensation Activity ["
                    + ((String) targetNode.getMetaData("UniqueId"))
                    + "] may not have any outgoing connection ["
                    + (String) outgoingConnection.getMetaData("UniqueId") + "]");
        }
    }

    private void assignLanes(RuleFlowProcess process, List<Lane> lanes) {
        List<String> laneNames = new ArrayList<>();
        Map<String, String> laneMapping = new HashMap<>();
        if (lanes != null) {
            for (Lane lane : lanes) {
                String name = lane.getName();
                if (name != null) {
                    Swimlane swimlane = new Swimlane();
                    swimlane.setName(name);
                    process.getSwimlaneContext().addSwimlane(swimlane);
                    laneNames.add(name);
                    for (String flowElementRef : lane.getFlowElements()) {
                        laneMapping.put(flowElementRef, name);
                    }
                }
            }
        }
        assignLanes(process, laneMapping);
    }

    private void postProcessNodes(RuleFlowProcess process, NodeContainer container) {
        List<String> eventSubProcessHandlers = new ArrayList<>();
        for (Node node : container.getNodes()) {

            if (node instanceof StateNode) {
                StateNode stateNode = (StateNode) node;
                String condition = (String) stateNode.getMetaData("Condition");
                Constraint constraint = new ConstraintImpl();
                constraint.setConstraint(condition);
                constraint.setType("rule");
                for (org.kie.api.definition.process.Connection connection : stateNode.getDefaultOutgoingConnections()) {
                    stateNode.setConstraint(connection, constraint);
                }
            } else if (node instanceof NodeContainer) {
                // prepare event sub process
                if (node instanceof EventSubProcessNode) {
                    EventSubProcessNode eventSubProcessNode = (EventSubProcessNode) node;

                    Node[] nodes = eventSubProcessNode.getNodes();
                    for (Node subNode : nodes) {
                        // avoids cyclomatic complexity
                        if (subNode == null || !(subNode instanceof StartNode)) {
                            continue;
                        }
                        List<Trigger> triggers = ((StartNode) subNode).getTriggers();
                        if (triggers == null) {
                            continue;
                        }
                        for (Trigger trigger : triggers) {
                            if (trigger instanceof EventTrigger) {
                                final List<EventFilter> filters = ((EventTrigger) trigger).getEventFilters();

                                for (EventFilter filter : filters) {
                                    if (filter instanceof EventTypeFilter) {
                                        eventSubProcessNode.addEvent((EventTypeFilter) filter);

                                        String type = ((EventTypeFilter) filter).getType();
                                        if (type.startsWith("Error-") || type.startsWith("Escalation")) {
                                            String faultCode = (String) subNode.getMetaData().get("FaultCode");
                                            String replaceRegExp = "Error-|Escalation-";
                                            final String signalType = type;

                                            ExceptionScope exceptionScope =
                                                    (ExceptionScope) ((ContextContainer) eventSubProcessNode.getParentContainer()).getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
                                            if (exceptionScope == null) {
                                                exceptionScope = new ExceptionScope();
                                                ((ContextContainer) eventSubProcessNode.getParentContainer()).addContext(exceptionScope);
                                                ((ContextContainer) eventSubProcessNode.getParentContainer()).setDefaultContext(exceptionScope);
                                            }
                                            String faultVariable = null;
                                            if (trigger.getInAssociations() != null && !trigger.getInAssociations().isEmpty()) {
                                                faultVariable = findVariable(trigger.getInAssociations().get(0).getTarget().getLabel(), process.getVariableScope());
                                            }

                                            ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
                                            DroolsConsequenceAction action = new DroolsConsequenceAction("java", "");
                                            action.setMetaData("Action", new SignalProcessInstanceAction(signalType, faultVariable, null, SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
                                            exceptionHandler.setAction(action);
                                            exceptionHandler.setFaultVariable(faultVariable);
                                            if (faultCode != null) {
                                                String trimmedType = type.replaceFirst(replaceRegExp, "");
                                                exceptionScope.setExceptionHandler(trimmedType, exceptionHandler);
                                                eventSubProcessHandlers.add(trimmedType);
                                            } else {
                                                exceptionScope.setExceptionHandler(faultCode, exceptionHandler);
                                            }
                                        } else if (type.equals("Compensation")) {
                                            // 1. Find the parent sub-process to this event sub-process
                                            NodeContainer parentSubProcess = null;
                                            NodeContainer subProcess = eventSubProcessNode.getParentContainer();
                                            Object isForCompensationObj = eventSubProcessNode.getMetaData("isForCompensation");
                                            if (isForCompensationObj == null) {
                                                eventSubProcessNode.setMetaData("isForCompensation", true);
                                                logger.warn("Overriding empty value of \"isForCompensation\" attribute on Event Sub-Process [{}] and setting it to true.",
                                                        eventSubProcessNode.getMetaData("UniqueId"));
                                            }
                                            String compensationHandlerId = "";
                                            if (subProcess instanceof RuleFlowProcess) {
                                                // If jBPM deletes the process (instance) as soon as the process completes..
                                                // ..how do you expect to signal compensation on the completed process (instance)?!?
                                                throw new ProcessParsingValidationException("Compensation Event Sub-Processes at the process level are not supported.");
                                            }
                                            if (subProcess instanceof Node) {
                                                parentSubProcess = ((KogitoNode) subProcess).getParentContainer();
                                                compensationHandlerId = (String) ((CompositeNode) subProcess).getMetaData(Metadata.UNIQUE_ID);
                                            }
                                            // 2. The event filter (never fires, purely for dumping purposes) has already been added

                                            // 3. Add compensation scope
                                            addCompensationScope(process, eventSubProcessNode, parentSubProcess, compensationHandlerId);
                                        }
                                    }
                                }
                            } else if (trigger instanceof ConstraintTrigger) {
                                ConstraintTrigger constraintTrigger = (ConstraintTrigger) trigger;

                                if (constraintTrigger.getConstraint() != null) {
                                    String processId = ((RuleFlowProcess) container).getId();
                                    String type = "RuleFlowStateEventSubProcess-Event-" + processId + "-" + eventSubProcessNode.getUniqueId();
                                    EventTypeFilter eventTypeFilter = new EventTypeFilter();
                                    eventTypeFilter.setType(type);
                                    eventSubProcessNode.addEvent(eventTypeFilter);
                                }
                            }
                        }
                    }
                }
                postProcessNodes(process, (NodeContainer) node);
            } else if (node instanceof EndNode) {
                handleIntermediateOrEndThrowCompensationEvent((EndNode) node);
            } else if (node instanceof ActionNode) {
                handleIntermediateOrEndThrowCompensationEvent((ActionNode) node);
            } else if (node instanceof EventNode) {
                final EventNode eventNode = (EventNode) node;
                if (!(eventNode instanceof BoundaryEventNode) && eventNode.getDefaultIncomingConnections().isEmpty()) {
                    throw new ProcessParsingValidationException("Event node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection");
                }
            }
        }

        // process fault node to disable termnate parent if there is event subprocess handler
        for (Node node : container.getNodes()) {
            if (node instanceof FaultNode) {
                FaultNode faultNode = (FaultNode) node;
                if (eventSubProcessHandlers.contains(faultNode.getFaultName())) {
                    faultNode.setTerminateParent(false);
                }
            }
        }
    }

    private void assignLanes(NodeContainer nodeContainer, Map<String, String> laneMapping) {
        for (Node node : nodeContainer.getNodes()) {
            String lane = null;
            String uniqueId = (String) node.getMetaData().get("UniqueId");
            if (uniqueId != null) {
                lane = laneMapping.get(uniqueId);
            } else {
                lane = laneMapping.get(XmlBPMNProcessDumper.getUniqueNodeId(node));
            }
            if (lane != null) {
                ((NodeImpl) node).setMetaData("Lane", lane);
                if (node instanceof HumanTaskNode) {
                    ((HumanTaskNode) node).setSwimlane(lane);
                }
            }
            if (node instanceof NodeContainer) {
                assignLanes((NodeContainer) node, laneMapping);
            }
        }
    }

    private static Constraint buildConstraint(SequenceFlow connection, NodeImpl node) {
        if (connection.getExpression() == null) {
            return null;
        }

        Constraint constraint = new ConstraintImpl();
        String defaultConnection = (String) node.getMetaData("Default");
        if (defaultConnection != null && defaultConnection.equals(connection.getId())) {
            constraint.setDefault(true);
        }
        if (connection.getName() != null) {
            constraint.setName(connection.getName());
        } else {
            constraint.setName("");
        }
        if (connection.getType() != null) {
            constraint.setType(connection.getType());
        } else {
            constraint.setType("code");
        }
        if (connection.getLanguage() != null) {
            constraint.setDialect(connection.getLanguage());
        }
        if (connection.getExpression() != null) {
            constraint.setConstraint(connection.getExpression());
        }
        constraint.setPriority(connection.getPriority());

        return constraint;
    }

    protected static void addCompensationScope(final RuleFlowProcess process, final Node node,
            final org.kie.api.definition.process.NodeContainer parentContainer, final String compensationHandlerId) {
        process.getMetaData().put("Compensation", true);

        assert parentContainer instanceof ContextContainer
                : "Expected parent node to be a CompositeContextNode, not a " + parentContainer.getClass().getSimpleName();

        ContextContainer contextContainer = (ContextContainer) parentContainer;
        CompensationScope scope = null;
        boolean addScope = false;
        if (contextContainer.getContexts(CompensationScope.COMPENSATION_SCOPE) == null) {
            addScope = true;
        } else {
            scope = (CompensationScope) contextContainer.getContexts(CompensationScope.COMPENSATION_SCOPE).get(0);
            if (scope == null) {
                addScope = true;
            }
        }
        if (addScope) {
            scope = new CompensationScope();
            contextContainer.addContext(scope);
            contextContainer.setDefaultContext(scope);
            scope.setContextContainer(contextContainer);
        }

        CompensationHandler handler = new CompensationHandler();
        handler.setNode(node);
        if (scope.getExceptionHandler(compensationHandlerId) != null) {
            throw new ProcessParsingValidationException(
                    "More than one compensation handler per node (" + compensationHandlerId + ")" + " is not supported!");
        }
        scope.setExceptionHandler(compensationHandlerId, handler);
    }

    protected void handleIntermediateOrEndThrowCompensationEvent(ExtendedNodeImpl throwEventNode) {
        if (throwEventNode.getMetaData("compensation-activityRef") != null) {
            String activityRef = (String) throwEventNode.getMetaData().remove("compensation-activityRef");

            NodeContainer nodeParent = throwEventNode.getParentContainer();
            if (nodeParent instanceof EventSubProcessNode) {
                boolean compensationEventSubProcess = false;
                List<Trigger> startTriggers = ((EventSubProcessNode) nodeParent).findStartNode().getTriggers();
                CESP_CHECK: for (Trigger trigger : startTriggers) {
                    if (trigger instanceof EventTrigger) {
                        for (EventFilter filter : ((EventTrigger) trigger).getEventFilters()) {
                            if (((EventTypeFilter) filter).getType().equals(Metadata.EVENT_TYPE_COMPENSATION)) {
                                compensationEventSubProcess = true;
                                break CESP_CHECK;
                            }
                        }
                    }
                }
                if (compensationEventSubProcess) {
                    // BPMN2 spec, p. 252, p. 248: intermediate and end compensation event visibility scope
                    nodeParent = ((NodeImpl) nodeParent).getParentContainer();
                }
            }
            String parentId;
            if (nodeParent instanceof RuleFlowProcess) {
                parentId = ((RuleFlowProcess) nodeParent).getId();
            } else {
                parentId = (String) ((NodeImpl) nodeParent).getMetaData("UniqueId");
            }

            String compensationEvent;
            if (activityRef.isEmpty()) {
                // general/implicit compensation
                compensationEvent = CompensationScope.IMPLICIT_COMPENSATION_PREFIX + parentId;
            } else {
                // specific compensation
                compensationEvent = activityRef;
            }

            DroolsConsequenceAction compensationAction = new DroolsConsequenceAction("java", "");
            compensationAction.setMetaData("Action", new ProcessInstanceCompensationAction(compensationEvent));

            if (throwEventNode instanceof ActionNode) {
                ((ActionNode) throwEventNode).setAction(compensationAction);
            } else if (throwEventNode instanceof EndNode) {
                List<DroolsAction> actions = new ArrayList<>();
                actions.add(compensationAction);
                ((EndNode) throwEventNode).setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, actions);
            }
        }
    }

    /**
     * Finds the right variable by its name to make sure that when given as id it will be also matched
     * 
     * @param variableName name or id of the variable
     * @param variableScope VariableScope of given process
     * @return returns found variable name or given 'variableName' otherwise
     */
    protected String findVariable(String variableName, VariableScope variableScope) {
        if (variableName == null) {
            return null;
        }

        return variableScope.getVariables().stream().filter(v -> v.matchByIdOrName(variableName)).map(v -> v.getName()).findFirst().orElse(variableName);
    }

    public static DroolsConsequenceAction createJavaAction(Action action) {
        DroolsConsequenceAction consequenceAction = new DroolsConsequenceAction("java", "");
        consequenceAction.setMetaData("Action", action);
        return consequenceAction;
    }
}
