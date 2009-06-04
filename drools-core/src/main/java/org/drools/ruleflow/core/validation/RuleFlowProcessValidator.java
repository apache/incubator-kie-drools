package org.drools.ruleflow.core.validation;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.process.core.Work;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.validation.ProcessValidationError;
import org.drools.process.core.validation.ProcessValidator;
import org.drools.process.core.validation.impl.ProcessValidationErrorImpl;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.core.node.CompositeNode.NodeAndType;
import org.drools.workflow.core.node.StateNode;
import org.mvel2.ErrorDetail;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

/**
 * Default implementation of a RuleFlow validator.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcessValidator implements ProcessValidator {
    
    // TODO: make this pluggable
    // TODO: extract generic process stuff and generic workflow stuff

    private static RuleFlowProcessValidator instance;

    private boolean startNodeFound;
    private boolean endNodeFound;
    
    private RuleFlowProcessValidator() {
    }

    public static RuleFlowProcessValidator getInstance() {
        if ( instance == null ) {
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

        if ( process.getPackageName() == null || "".equals( process.getPackageName() ) ) {
            errors.add(new ProcessValidationErrorImpl(process,
                "Process has no package name."));
        }

        // check start node of process
        if ( process.getStart() == null ) {
            errors.add(new ProcessValidationErrorImpl(process,
                "Process has no start node."));
        }

        startNodeFound = false;
        endNodeFound = false;
        final Node[] nodes = process.getNodes();
        validateNodes(nodes, errors, process);
        if (!startNodeFound) {
            errors.add(new ProcessValidationErrorImpl(process,
                "Process has no start node."));
        }
        if (!endNodeFound) {
            errors.add(new ProcessValidationErrorImpl(process,
                "Process has no end node."));
        }
        for (final Iterator<Variable> it = process.getVariableScope().getVariables().iterator(); it.hasNext(); ) {
            final Variable variable = it.next();
            if (variable.getType() == null) {
                errors.add(new ProcessValidationErrorImpl(process,
                    "Variable '" + variable.getName() + "' has no type."));
            }
        }

        checkAllNodesConnectedToStart(process, errors);

        return errors.toArray(new ProcessValidationError[errors.size()]);
    }
    
    private void validateNodes(Node[] nodes, List<ProcessValidationError> errors, RuleFlowProcess process) {
        for ( int i = 0; i < nodes.length; i++ ) {
            final Node node = nodes[i];
            if (node instanceof StartNode) {
                final StartNode startNode = (StartNode) node;
                startNodeFound = true;
                if (startNode.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Start node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
            } else if (node instanceof EndNode) {
                final EndNode endNode = (EndNode) node;
                endNodeFound = true;
                if (endNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "End node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
            } else if (node instanceof RuleSetNode) {
                final RuleSetNode ruleSetNode = (RuleSetNode) node;
                if (ruleSetNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "RuleSet node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
                if (ruleSetNode.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "RuleSet node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                final String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
                if (ruleFlowGroup == null || "".equals(ruleFlowGroup)) {
                    errors.add( new ProcessValidationErrorImpl(process,
                        "RuleSet node '" + node.getName() + "' [" + node.getId() + "] has no ruleflow-group."));
                }
            } else if (node instanceof Split) {
                final Split split = (Split) node;
                if (split.getType() == Split.TYPE_UNDEFINED) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Split node '" + node.getName() + "' [" + node.getId() + "] has no type."));
                }
                if (split.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Split node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
                if (split.getDefaultOutgoingConnections().size() < 2) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Split node '" + node.getName() + "' [" + node.getId() + "] does not have more than one outgoing connection: " + split.getOutgoingConnections().size() + "."));
                }
                if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR ) {
                    for ( final Iterator<Connection> it = split.getDefaultOutgoingConnections().iterator(); it.hasNext(); ) {
                        final Connection connection = it.next();
                        if (split.getConstraint(connection) == null) {
                            errors.add(new ProcessValidationErrorImpl(process,
                                "Split node '" + node.getName() + "' [" + node.getId() + "] does not have a constraint for " + connection.toString() + "."));
                        }
                    }
                }
            } else if ( node instanceof Join ) {
                final Join join = (Join) node;
                if (join.getType() == Join.TYPE_UNDEFINED) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Join node '" + node.getName() + "' [" + node.getId() + "] has no type."));
                }
                if (join.getDefaultIncomingConnections().size() < 2) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Join node '" + node.getName() + "' [" + node.getId() + "] does not have more than one incoming connection: " + join.getIncomingConnections().size() + "."));
                }
                if (join.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Join node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                if (join.getType() == Join.TYPE_N_OF_M) {
                	String n = join.getN();
                	if (!n.startsWith("#{") || !n.endsWith("}")) {
                		try {
                			new Integer(n);
                		} catch (NumberFormatException e) {
                            errors.add(new ProcessValidationErrorImpl(process,
                                "Join node '" + node.getName() + "' [" + node.getId() + "] has illegal n value: " + n));
                		}
                	}
                }
            } else if (node instanceof MilestoneNode) {
                final MilestoneNode milestone = (MilestoneNode) node;
                if (milestone.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Milestone node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }

                if (milestone.getTo() == null) {
                    errors.add( new ProcessValidationErrorImpl(process,
                        "Milestone node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                if (milestone.getConstraint() == null) {
                    errors.add( new ProcessValidationErrorImpl(process,
                        "Milestone node '" + node.getName() + "' [" + node.getId() + "] has no constraint."));
                }
            }else if (node instanceof StateNode) {
                final StateNode stateNode = (StateNode) node;
                if (stateNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "State node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }

               
               
            }
            else if (node instanceof SubProcessNode) {
                final SubProcessNode subProcess = (SubProcessNode) node;
                if (subProcess.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "SubProcess node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
                if (subProcess.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "SubProcess node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                if (subProcess.getProcessId() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "SubProcess node '" + node.getName() + "' [" + node.getId() + "] has no process id."));
                }
            } else if (node instanceof ActionNode) {
                final ActionNode actionNode = (ActionNode) node;
                if (actionNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Action node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
                if (actionNode.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Action node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                if (actionNode.getAction() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Action node '" + node.getName() + "' [" + node.getId() + "] has no action."));
                } else {
                    if (actionNode.getAction() instanceof DroolsConsequenceAction) {
                        DroolsConsequenceAction droolsAction = (DroolsConsequenceAction) actionNode.getAction();
                        String actionString = droolsAction.getConsequence();
                        if (actionString == null) {
                            errors.add(new ProcessValidationErrorImpl(process,
                                "Action node '" + node.getName() + "' [" + node.getId() + "] has empty action."));
                        } else if( "mvel".equals( droolsAction.getDialect() ) ) {
                            try {
                                ExpressionCompiler compiler = new ExpressionCompiler(actionString);
                                compiler.setVerifying(true);
                                ParserContext parserContext = new ParserContext();
                                //parserContext.setStrictTypeEnforcement(true);
                                compiler.compile(parserContext);
                                List<ErrorDetail> mvelErrors = parserContext.getErrorList();
                                if (mvelErrors != null) {
                                    for (Iterator<ErrorDetail> iterator = mvelErrors.iterator(); iterator.hasNext(); ) {
                                        ErrorDetail error = iterator.next();
                                        errors.add(new ProcessValidationErrorImpl(process,
                                            "Action node '" + node.getName() + "' [" + node.getId() + "] has invalid action: " + error.getMessage() + "."));
                                    }
                                }
                            } catch (Throwable t) {
                                errors.add(new ProcessValidationErrorImpl(process,
                                    "Action node '" + node.getName() + "' [" + node.getId() + "] has invalid action: " + t.getMessage() + "."));
                            }
                        }
                    }
                }
            } else if (node instanceof WorkItemNode) {
                final WorkItemNode workItemNode = (WorkItemNode) node;
                if (workItemNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "WorkItem node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
                if (workItemNode.getTo() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "WorkItem node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection."));
                }
                if (workItemNode.getWork() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "WorkItem node '" + node.getName() + "' [" + node.getId() + "] has no work specified."));
                } else {
                    Work work = workItemNode.getWork();
                    if (work.getName() == null) {
                        errors.add(new ProcessValidationErrorImpl(process,
                            "WorkItem node '" + node.getName() + "' [" + node.getId() + "] has no work name."));
                    }
                }
            } else if (node instanceof ForEachNode) {
                final ForEachNode forEachNode = (ForEachNode) node;
                String variableName = forEachNode.getVariableName();
                if (variableName == null || "".equals(variableName)) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no variable name"));
                }
                String collectionExpression = forEachNode.getCollectionExpression();
                if (collectionExpression == null || "".equals(collectionExpression)) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no collection expression"));
                }
                if (forEachNode.getIncomingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE).size() == 0) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection"));
                }
                if (forEachNode.getOutgoingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE).size() == 0) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection"));
                }
                if (forEachNode.getLinkedIncomingNode(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE) == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no linked start node"));
                }
                if (forEachNode.getLinkedOutgoingNode(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE) == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "ForEach node '" + node.getName() + "' [" + node.getId() + "] has no linked end node"));
                }
                validateNodes(forEachNode.getNodes(), errors, process);
            } else if (node instanceof CompositeNode) {
                final CompositeNode compositeNode = (CompositeNode) node;
                for (Map.Entry<String, NodeAndType> inType: compositeNode.getLinkedIncomingNodes().entrySet()) {
                    if (compositeNode.getIncomingConnections(inType.getKey()).size() == 0) {
                        errors.add(new ProcessValidationErrorImpl(process,
                            "Composite node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection for type " + inType.getKey()));
                    }
                	if (inType.getValue().getNode() == null) {
                        errors.add(new ProcessValidationErrorImpl(process,
                            "Composite node '" + node.getName() + "' [" + node.getId() + "] has invalid linked incoming node for type " + inType.getKey()));
                	}
                }
                for (Map.Entry<String, NodeAndType> outType: compositeNode.getLinkedOutgoingNodes().entrySet()) {
                    if (compositeNode.getOutgoingConnections(outType.getKey()).size() == 0) {
                        errors.add(new ProcessValidationErrorImpl(process,
                            "Composite node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection for type " + outType.getKey()));
                    }
                	if (outType.getValue().getNode() == null) {
                        errors.add(new ProcessValidationErrorImpl(process,
                            "Composite node '" + node.getName() + "' [" + node.getId() + "] has invalid linked outgoing node for type " + outType.getKey()));
                	}
                }
                validateNodes(compositeNode.getNodes(), errors, process);
            } else if (node instanceof EventNode) {
                final EventNode eventNode = (EventNode) node;
                if (eventNode.getEventFilters().size() == 0) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Event node '" + node.getName() + "' [" + node.getId() + "] should specify an event type"));
                }
                if (eventNode.getOutgoingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE).size() == 0) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Event node '" + node.getName() + "' [" + node.getId() + "] has no outgoing connection"));
                }
            } else if (node instanceof FaultNode) {
            	endNodeFound = true;
                final FaultNode faultNode = (FaultNode) node;
            	if (faultNode.getFrom() == null) {
                    errors.add(new ProcessValidationErrorImpl(process,
                        "Fault node '" + node.getName() + "' [" + node.getId() + "] has no incoming connection."));
                }
            	if (faultNode.getFaultName() == null) {
            		errors.add(new ProcessValidationErrorImpl(process,
                        "Fault node '" + node.getName() + "' [" + node.getId() + "] has no fault name."));
            	}
            } 
        }

    }

    private void checkAllNodesConnectedToStart(final RuleFlowProcess process,
                                               final List<ProcessValidationError> errors) {
        final Map<Node, Boolean> processNodes = new HashMap<Node, Boolean>();
        final Node[] nodes = process.getNodes();
        List<Node> eventNodes = new ArrayList<Node>();
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            processNodes.put(node, Boolean.FALSE);
            if (node instanceof EventNode) {
            	eventNodes.add(node);
            }
        }
        final Node start = process.getStart();
        if (start != null) {
            processNode(start, processNodes);
        }
        for (Node eventNode: eventNodes) {
            processNode(eventNode, processNodes);
        }
        for ( final Iterator<Node> it = processNodes.keySet().iterator(); it.hasNext(); ) {
            final Node node = it.next();
            if (Boolean.FALSE.equals(processNodes.get(node))) {
                errors.add(new ProcessValidationErrorImpl(process,
                    "Node '" + node.getName() + "' [" + node.getId() + "] has no connection to the start node."));
            }
        }
    }

    private void processNode(final Node node, final Map<Node, Boolean> nodes) {
        if (!nodes.containsKey(node) ) {
            throw new IllegalStateException("A process node is connected with a node that does not belong to the process: " + node.getName());
        }
        final Boolean prevValue = (Boolean) nodes.put(node, Boolean.TRUE);
        if (prevValue == Boolean.FALSE) {
            for (final Iterator<List<Connection>> it = node.getOutgoingConnections().values().iterator(); it.hasNext(); ) {
                final List<Connection> list = it.next();
                for (final Iterator<Connection> it2 = list.iterator(); it2.hasNext(); ) {
                    processNode(it2.next().getTo(), nodes);
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

}
