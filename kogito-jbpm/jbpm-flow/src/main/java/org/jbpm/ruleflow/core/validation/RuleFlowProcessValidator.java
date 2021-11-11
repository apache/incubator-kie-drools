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
package org.jbpm.ruleflow.core.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.drools.core.time.impl.CronExpression;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.core.validation.ProcessValidator;
import org.jbpm.process.core.validation.impl.ProcessValidationErrorImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.CompositeNode.CompositeNodeEnd;
import org.jbpm.workflow.core.node.CompositeNode.NodeAndType;
import org.jbpm.workflow.core.node.DataAssociation;
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
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.SIGNAL_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;

/**
 * Default implementation of a RuleFlow validator.
 */
public class RuleFlowProcessValidator implements ProcessValidator {

    public static final String ASSOCIATIONS = "BPMN.Associations";
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleFlowProcessValidator.class);
    private static final String KCONTEXT = "kcontext";
    private static RuleFlowProcessValidator INSTANCE;

    private RuleFlowProcessValidator() {
    }

    public static RuleFlowProcessValidator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuleFlowProcessValidator();
        }
        return INSTANCE;
    }

    public ProcessValidationError[] validateProcess(final RuleFlowProcess process) {
        final List<ProcessValidationError> errors = new ArrayList<>();

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

        validateNodes(process.getNodes(), errors, process);

        validateVariables(errors, process);

        validateDataAssignments(errors, process);

        checkAllNodesConnectedToStart(process,
                process.isDynamic(),
                errors,
                process);

        return errors.toArray(new ProcessValidationError[errors.size()]);
    }

    protected void validateNodes(org.kie.api.definition.process.Node[] nodes,
            List<ProcessValidationError> errors,
            RuleFlowProcess process) {
        String isForCompensation = "isForCompensation";
        for (int i = 0; i < nodes.length; i++) {
            final org.kie.api.definition.process.Node node = nodes[i];
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
                validateOnEntryOnExitScripts(ruleSetNode, errors, process);
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

                RuleSetNode.RuleType ruleType = ruleSetNode.getRuleType();
                if (RuleSetNode.DRL_LANG.equals(language)) {
                    final String ruleFlowGroup = ruleType.getName();
                    if (ruleFlowGroup == null || "".equals(ruleFlowGroup)) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "RuleSet (DRL) has no ruleflow-group.");
                    }
                } else if (RuleSetNode.RULE_UNIT_LANG.equals(language)) {
                    final String unit = ruleType.getName();
                    if (unit == null || "".equals(unit)) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "RuleSet (Rule Unit) has no ruleflow-group.");
                    }
                } else if (RuleSetNode.DMN_LANG.equals(language)) {
                    RuleSetNode.RuleType.Decision decision = (RuleSetNode.RuleType.Decision) ruleType;
                    final String namespace = decision.getNamespace();
                    if (namespace == null || "".equals(namespace)) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "RuleSet (DMN) has no namespace.");
                    }
                    final String model = decision.getModel();
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
                    for (final Iterator<Connection> it = split.getDefaultOutgoingConnections().iterator(); it.hasNext();) {
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
                            Integer.parseInt(n);
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
                validateOnEntryOnExitScripts(milestone, errors, process);
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
                if (stateNode.getDefaultIncomingConnections().isEmpty() && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "State has no incoming connection");
                }
            } else if (node instanceof SubProcessNode) {
                final SubProcessNode subProcess = (SubProcessNode) node;
                validateOnEntryOnExitScripts(subProcess, errors, process);
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
                // don't add message if action node action is null
                // with codegen the ActionNodeVisitor will add the action
                // so when testing outside codegen having no action
                // does not mean the action node has an error (this was true before with jBPM but not in Kogito)
                if (actionNode.getAction() instanceof DroolsConsequenceAction) {
                    DroolsConsequenceAction droolsAction = (DroolsConsequenceAction) actionNode.getAction();
                    String actionString = droolsAction.getConsequence();
                    if (actionString == null) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "Action has empty action.");
                    }
                    if (!"java".equals(droolsAction.getDialect())) {
                        addErrorMessage(process,
                                node,
                                errors,
                                droolsAction.getDialect() + " script language is not supported in Kogito.");
                    }

                    TypeSolver typeSolver = new ReflectionTypeSolver();
                    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
                    JavaParser parser = new JavaParser(new ParserConfiguration().setSymbolResolver(symbolSolver));

                    ParseResult<CompilationUnit> parse = parser.parse("import org.kie.kogito.internal.process.runtime.KogitoProcessContext;\n" +
                            "import org.jbpm.process.instance.impl.Action;\n" +
                            " class Test {\n" +
                            "    Action action = kcontext -> {" + actionString + "};\n" +
                            "}");

                    if (parse.isSuccessful()) {
                        CompilationUnit unit = parse.getResult().get();

                        //Check local variables declaration
                        Set<String> knownVariables = unit.findAll(VariableDeclarationExpr.class).stream().flatMap(v -> v.getVariables().stream()).map(v -> v.getNameAsString()).collect(toSet());

                        knownVariables.add(KCONTEXT);
                        knownVariables.addAll(Arrays.stream(process.getVariableScope().getVariableNames()).collect(toSet()));
                        knownVariables.addAll(Arrays.asList(process.getGlobalNames()));

                        if (actionNode.getParentContainer() instanceof ContextContainer) {
                            ContextContainer contextContainer = (ContextContainer) actionNode.getParentContainer();
                            VariableScope variableScope = (VariableScope) contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE);
                            if (variableScope != null) {
                                knownVariables.addAll(Arrays.stream(variableScope.getVariableNames()).collect(toSet()));
                            }
                        }

                        BlockStmt blockStmt = unit.findFirst(BlockStmt.class).get();
                        try {
                            resolveVariablesType(unit, knownVariables);
                        } catch (UnsolvedSymbolException ex) {
                            DefaultPrettyPrinterVisitor v1 = new DefaultPrettyPrinterVisitor(new DefaultPrinterConfiguration());
                            blockStmt.accept(v1, null);
                            LOGGER.error("\n" + v1);
                            //Small hack to extract the variable name causing the issue
                            //Name comes as "Solving x" where x is the variable name
                            final String[] solving = ex.getName().split(" ");
                            final String var = solving.length == 2 ? solving[1] : solving[0];
                            addErrorMessage(process,
                                    node,
                                    errors,
                                    format("uses unknown variable in the script: %s", var));
                        }
                    } else {
                        addErrorMessage(process,
                                node,
                                errors,
                                format("unable to parse Java content: %s", parse.getProblems().get(0).getMessage()));
                    }

                    validateCompensationIntermediateOrEndEvent(actionNode,
                            process,
                            errors);
                }
            } else if (node instanceof WorkItemNode) {
                final WorkItemNode workItemNode = (WorkItemNode) node;
                validateOnEntryOnExitScripts(workItemNode, errors, process);
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
                validateOnEntryOnExitScripts(forEachNode, errors, process);
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
                if (forEachNode.getDefaultIncomingConnections().isEmpty() && !acceptsNoIncomingConnections(node)) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "ForEach has no incoming connection");
                }
                if (forEachNode.getDefaultOutgoingConnections().isEmpty() && !acceptsNoOutgoingConnections(node)) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "ForEach has no outgoing connection");
                }

                final List<org.kie.api.definition.process.Node> start = RuleFlowProcess.getStartNodes(forEachNode.getNodes());
                if (start != null) {
                    for (org.kie.api.definition.process.Node s : start) {
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
                validateOnEntryOnExitScripts(dynamicNode, errors, process);

                if (dynamicNode.getDefaultIncomingConnections().isEmpty() && !acceptsNoIncomingConnections(dynamicNode)) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "Dynamic has no incoming connection");
                }

                if (dynamicNode.getDefaultOutgoingConnections().isEmpty() && !acceptsNoOutgoingConnections(dynamicNode)) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "Dynamic has no outgoing connection");
                }

                if (!dynamicNode.hasCompletionCondition() && !dynamicNode.isAutoComplete()) {
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
                validateOnEntryOnExitScripts(compositeNode, errors, process);
                for (Map.Entry<String, NodeAndType> inType : compositeNode.getLinkedIncomingNodes().entrySet()) {
                    if (compositeNode.getIncomingConnections(inType.getKey()).isEmpty() && !acceptsNoIncomingConnections(node)) {
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
                    if (compositeNode.getOutgoingConnections(outType.getKey()).isEmpty()) {
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

                if (compositeNode.getLinkedIncomingNodes().values().isEmpty()) {
                    boolean foundStartNode = false;

                    for (org.kie.api.definition.process.Node internalNode : compositeNode.getNodes()) {
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
                    org.kie.api.definition.process.Node[] eventSubProcessNodes = compositeNode.getNodes();
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

                    final List<org.kie.api.definition.process.Node> start = RuleFlowProcess.getStartNodes(compositeNode.getNodes());
                    if (start != null) {
                        for (org.kie.api.definition.process.Node s : start) {
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
                if (eventNode.getEventFilters().isEmpty()) {
                    addErrorMessage(process,
                            node,
                            errors,
                            "Event should specify an event type");
                }
                if (eventNode instanceof BoundaryEventNode && EVENT_TYPE_MESSAGE.equals(eventNode.getMetaData(EVENT_TYPE))) {
                    if (eventNode.getMetaData(TRIGGER_REF) == null) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "Boundary event missing message name");
                    }

                    if (eventNode.getVariableName() == null) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "Boundary event missing variable in data assignment");
                    }

                    if (eventNode.getMetaData(MESSAGE_TYPE) == null) {
                        addErrorMessage(process,
                                node,
                                errors,
                                "Boundary event missing message type");
                    }
                }
                if (eventNode.getDefaultOutgoingConnections().isEmpty()) {
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

    private void resolveVariablesType(com.github.javaparser.ast.Node node, Set<String> knownVariables) {
        node.findAll(MethodCallExpr.class).stream()
                .filter(m -> m.getScope().isPresent())
                .forEach(m -> {
                    Expression expression = m.getScope().get();
                    if (expression.isNameExpr() && !knownVariables.contains(expression.asNameExpr().getNameAsString())) {
                        expression.calculateResolvedType();
                    }
                });
        node.findAll(AssignExpr.class).stream()
                .forEach(m -> {
                    Expression expression = m.getTarget();
                    if (expression.isNameExpr() && !knownVariables.contains(expression.asNameExpr().getNameAsString())) {
                        expression.calculateResolvedType();
                    }
                });
        resolveVariablesTypes(node, knownVariables);
    }

    private void resolveVariablesTypes(com.github.javaparser.ast.Node node, Set<String> knownVariables) {
        node.findAll(MethodCallExpr.class).stream()
                .flatMap(m -> m.getArguments().stream())
                .forEach(arg -> {
                    if (arg.isMethodCallExpr() || arg.isBinaryExpr()) {
                        resolveVariablesTypes(arg, knownVariables);
                    } else {
                        arg.findAll(NameExpr.class).stream().filter(ex -> !knownVariables.contains(ex.getNameAsString())).forEach(ex -> ex.calculateResolvedType());
                    }
                });
        node.findAll(BinaryExpr.class).stream()
                .map(bex -> bex.asBinaryExpr())
                .forEach(bex -> {
                    if (bex.getLeft().isNameExpr()) {
                        if (!knownVariables.contains(bex.getLeft().asNameExpr().getNameAsString())) {
                            bex.getLeft().calculateResolvedType();
                        }
                    } else {
                        resolveVariablesTypes(bex.getLeft(), knownVariables);
                    }
                    if (bex.getRight().isNameExpr()) {
                        if (!knownVariables.contains(bex.getRight().asNameExpr().getNameAsString())) {
                            bex.getRight().calculateResolvedType();
                        }
                    } else {
                        resolveVariablesTypes(bex.getRight(), knownVariables);
                    }
                });
    }

    private void checkAllNodesConnectedToStart(final NodeContainer container,
            boolean isDynamic,
            final List<ProcessValidationError> errors,
            RuleFlowProcess process) {
        final Map<org.kie.api.definition.process.Node, Boolean> processNodes = new HashMap<>();
        final org.kie.api.definition.process.Node[] nodes;
        if (container instanceof CompositeNode) {
            nodes = ((CompositeNode) container).internalGetNodes();
        } else {
            nodes = container.getNodes();
        }
        List<org.kie.api.definition.process.Node> eventNodes = new ArrayList<>();
        List<CompositeNode> compositeNodes = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            final org.kie.api.definition.process.Node node = nodes[i];
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
            for (org.kie.api.definition.process.Node node : nodes) {
                if (node.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
                    processNode(node,
                            processNodes);
                }
            }
        } else {
            final List<org.kie.api.definition.process.Node> start = RuleFlowProcess.getStartNodes(nodes);
            if (start != null) {
                for (org.kie.api.definition.process.Node s : start) {
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
        for (org.kie.api.definition.process.Node eventNode : eventNodes) {
            processNode(eventNode, processNodes);
        }
        for (CompositeNode compositeNode : compositeNodes) {
            checkAllNodesConnectedToStart(
                    compositeNode,
                    compositeNode instanceof DynamicNode,
                    errors,
                    process);
        }
        for (final Iterator<org.kie.api.definition.process.Node> it = processNodes.keySet().iterator(); it.hasNext();) {
            final org.kie.api.definition.process.Node node = it.next();
            if (Boolean.FALSE.equals(processNodes.get(node)) && !(node instanceof StartNode) && !(node instanceof EventSubProcessNode)) {
                addErrorMessage(process,
                        node,
                        errors,
                        "Has no connection to the start node.");
            }
        }
    }

    private void processNode(final org.kie.api.definition.process.Node node,
            final Map<org.kie.api.definition.process.Node, Boolean> nodes) {
        if (!nodes.containsKey(node) && !((node instanceof CompositeNodeEnd) || (node instanceof ForEachSplitNode) || (node instanceof ForEachJoinNode))) {
            throw new IllegalStateException("A process node is connected with a node that does not belong to the process: " + node.getName());
        }
        final Boolean prevValue = nodes.put(node, Boolean.TRUE);
        if (prevValue == null || Boolean.FALSE.equals(prevValue)) {
            for (final List<Connection> list : node.getOutgoingConnections().values()) {
                for (final Connection connection : list) {
                    processNode(connection.getTo(), nodes);
                }
            }
        }
    }

    private boolean acceptsNoIncomingConnections(org.kie.api.definition.process.Node node) {
        return acceptsNoOutgoingConnections(node);
    }

    private boolean acceptsNoOutgoingConnections(org.kie.api.definition.process.Node node) {
        NodeContainer nodeContainer = ((Node) node).getParentContainer();
        return nodeContainer instanceof DynamicNode ||
                (nodeContainer instanceof WorkflowProcess && ((WorkflowProcess) nodeContainer).isDynamic());
    }

    private void validateTimer(final Timer timer,
            final org.kie.api.definition.process.Node node,
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
                            if (!CronExpression.isValidExpression(timer.getDelay())) {
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
        if (timer.getPeriod() != null && !timer.getPeriod().contains("#{")) {
            try {
                if (!CronExpression.isValidExpression(timer.getPeriod())) {
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

        if (timer.getDate() != null && !timer.getDate().contains("#{")) {
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

    public ProcessValidationError[] validateProcess(Process process) {
        if (!(process instanceof RuleFlowProcess)) {
            throw new IllegalArgumentException(
                    "This validator can only validate ruleflow processes!");
        }
        return validateProcess((RuleFlowProcess) process);
    }

    //TODO To be removed once https://issues.redhat.com/browse/KOGITO-2067 is fixed
    private void validateOnEntryOnExitScripts(Node node, List<ProcessValidationError> errors, RuleFlowProcess process) {
        if (node instanceof ExtendedNodeImpl) {
            List<DroolsAction> actions = ((ExtendedNodeImpl) node).getActions(ExtendedNodeImpl.EVENT_NODE_ENTER);
            if (actions != null && !actions.isEmpty()) {
                addErrorMessage(process, node, errors, "On Entry Action is not yet supported in Kogito");
            }
            actions = ((ExtendedNodeImpl) node).getActions(ExtendedNodeImpl.EVENT_NODE_EXIT);
            if (actions != null && !actions.isEmpty()) {
                addErrorMessage(process, node, errors, "On Exit Action is not yet supported in Kogito");
            }
        }
    }

    private void validateVariables(List<ProcessValidationError> errors, RuleFlowProcess process) {

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

    private void validateDataAssignments(List<ProcessValidationError> errors, RuleFlowProcess process) {
        Arrays.stream(process.getNodes())
                .filter(node -> node instanceof Mappable)
                .forEach(node -> {
                    Mappable m = (Mappable) node;
                    m.getInAssociations().forEach(da -> {
                        validateDataAssignmentsIn(errors, process, node, da);
                    });
                    m.getOutAssociations().forEach(da -> {
                        validateDataAssignmentsOut(errors, process, node, da);
                    });
                });
    }

    private void validateDataAssignmentsOut(List<ProcessValidationError> errors, RuleFlowProcess process, org.kie.api.definition.process.Node node, DataAssociation da) {
        if (node instanceof StartNode || node instanceof EventNode) {
            String type = getEventVariableType(node);
            if (type == null || type.trim().isEmpty()) {
                return;
            }
            String var = da.getSources().get(0);
            Variable variable = process.getVariableScope().findVariable(var);
            DataType dataType = DataTypeResolver.fromType(type, Thread.currentThread().getContextClassLoader());
            if (!variable.getType().equals(dataType)) {
                addErrorMessage(process, node, errors,
                        format("Target variable '%s':'%s' has different data type from '%s':'%s' in data output assignment", var, variable.getType().getStringType(), da.getTarget(),
                                dataType.getStringType()));
            }
        }
    }

    private void validateDataAssignmentsIn(List<ProcessValidationError> errors, RuleFlowProcess process, org.kie.api.definition.process.Node node, DataAssociation da) {
        if (node instanceof EndNode || node instanceof ActionNode) {
            String type = getEventVariableType(node);
            if (type == null || type.trim().isEmpty()) {
                return;
            }
            String var = (String) node.getMetaData().get(MAPPING_VARIABLE);
            Variable variable = process.getVariableScope().findVariable(var);
            DataType dataType = DataTypeResolver.fromType(type, Thread.currentThread().getContextClassLoader());
            if (!variable.getType().equals(dataType)) {
                addErrorMessage(process, node, errors,
                        format("Source variable '%s':'%s' has different data type from '%s':'%s' in data input assignment", var, variable.getType().getStringType(), da.getSources().get(0),
                                dataType.getStringType()));
            }
        }
    }

    private String getEventVariableType(org.kie.api.definition.process.Node node) {
        if (EVENT_TYPE_SIGNAL.equals(node.getMetaData().get(EVENT_TYPE))) {
            return (String) node.getMetaData().get(SIGNAL_TYPE);
        } else if (EVENT_TYPE_MESSAGE.equals(node.getMetaData().get(EVENT_TYPE))) {
            return (String) node.getMetaData().get(MESSAGE_TYPE);
        }
        return null;
    }

    @Override
    public boolean accept(Process process, Resource resource) {
        return RuleFlowProcess.RULEFLOW_TYPE.equals(process.getType());
    }

    protected void validateCompensationIntermediateOrEndEvent(org.kie.api.definition.process.Node node,
            RuleFlowProcess process,
            List<ProcessValidationError> errors) {
        if (node.getMetaData().containsKey("Compensation")) {
            // Validate that activityRef in throw/end compensation event refers to "visible" compensation
            String activityRef = (String) node.getMetaData().get("Compensation");
            org.kie.api.definition.process.Node refNode = null;
            if (activityRef != null) {
                Queue<org.kie.api.definition.process.Node> nodeQueue = new LinkedList<>();
                nodeQueue.addAll(Arrays.asList(process.getNodes()));
                while (!nodeQueue.isEmpty()) {
                    org.kie.api.definition.process.Node polledNode = nodeQueue.poll();
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

            CompensationScope compensationScope = (CompensationScope) ((NodeImpl) node).resolveContext(CompensationScope.COMPENSATION_SCOPE,
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
            org.kie.api.definition.process.Node node,
            List<ProcessValidationError> errors,
            String message) {
        String error = String.format("Node '%s' [%d] %s",
                node.getName(),
                node.getId(),
                message);
        errors.add(new ProcessValidationErrorImpl(process,
                error));
    }
}
