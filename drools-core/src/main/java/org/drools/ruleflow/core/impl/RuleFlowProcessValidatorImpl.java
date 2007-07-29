package org.drools.ruleflow.core.impl;

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

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.EndNode;
import org.drools.ruleflow.core.Join;
import org.drools.ruleflow.core.MilestoneNode;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.RuleFlowProcessValidationError;
import org.drools.ruleflow.core.RuleFlowProcessValidator;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.core.SubFlowNode;
import org.drools.ruleflow.core.Variable;

/**
 * Default implementation of a RuleFlow validator.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcessValidatorImpl
    implements
    RuleFlowProcessValidator {

    private static RuleFlowProcessValidatorImpl instance;

    private RuleFlowProcessValidatorImpl() {
    }

    public static RuleFlowProcessValidatorImpl getInstance() {
        if ( instance == null ) {
            instance = new RuleFlowProcessValidatorImpl();
        }
        return instance;
    }

    public RuleFlowProcessValidationError[] validateProcess(final RuleFlowProcess process) {
        final List errors = new ArrayList();

        if ( process.getName() == null ) {
            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.NO_PROCESS_NAME ) );
        }

        if ( process.getId() == null || "".equals( process.getId() ) ) {
            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.NO_PROCESS_ID ) );
        }

        // check start node of process
        if ( process.getStart() == null ) {
            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.NO_START_NODE ) );
        }

        boolean startNodeFound = false;
        boolean endNodeFound = false;
        final Node[] nodes = process.getNodes();
        for ( int i = 0; i < nodes.length; i++ ) {
            final Node node = nodes[i];
            if ( node instanceof StartNode ) {
                final StartNode startNode = (StartNode) node;
                startNodeFound = true;
                if ( startNode.getTo() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.START_NODE_WITHOUT_OUTGOING_NODES ) );
                }
            } else if ( node instanceof EndNode ) {
                final EndNode endNode = (EndNode) node;
                endNodeFound = true;
                if ( endNode.getFrom() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.END_NODE_HAS_NO_INCOMING_CONNECTIONS ) );
                }
            } else if ( node instanceof RuleSetNode ) {
                final RuleSetNode ruleSetNode = (RuleSetNode) node;
                if ( ruleSetNode.getFrom() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_INCOMING_CONNECTIONS ) );
                }

                if ( ruleSetNode.getTo() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_OUTGOING_CONNECTIONS ) );
                }
                final String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
                if ( ruleFlowGroup == null || "".equals( ruleFlowGroup ) ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_RULE_SET_GROUP ) );
                }
            } else if ( node instanceof Split ) {
                final Split split = (Split) node;
                if ( split.getType() == Split.TYPE_UNDEFINED ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SPLIT_WITHOUT_TYPE ) );
                }
                if ( split.getFrom() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SPLIT_WITHOUT_INCOMING_CONNECTION ) );
                }
                if ( split.getOutgoingConnections().size() < 2 ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SPLIT_NOT_ENOUGH_OUTGOING_CONNECTIONS ) );
                }
                if ( split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR ) {
                    for ( final Iterator it = split.getOutgoingConnections().iterator(); it.hasNext(); ) {
                        final Connection connection = (Connection) it.next();
                        if ( split.getConstraint( connection ) == null ) {
                            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SPLIT_OUTGOING_CONNECTION_WITHOUT_CONSTRAINT ) );
                        }
                    }
                }
            } else if ( node instanceof Join ) {
                final Join join = (Join) node;
                if ( join.getType() == Join.TYPE_UNDEFINED ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.JOIN_WITHOUT_TYPE ) );
                }
                if ( join.getIncomingConnections().size() < 2 ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.JOIN_NOT_ENOUGH_INCOMING_CONNECTIONS ) );
                }
                if ( join.getTo() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.JOIN_WITHOUT_OUTGOING_CONNECTION ) );
                }
            } else if ( node instanceof MilestoneNode ) {
                final MilestoneNode milestone = (MilestoneNode) node;
                if ( milestone.getFrom() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.MILESTONE_NODE_WITHOUT_INCOMING_CONNECTIONS ) );
                }

                if ( milestone.getTo() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.MILESTONE_NODE_WITHOUT_OUTGOING_CONNECTIONS ) );
                }
                if ( milestone.getConstraint() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.MILESTONE_WITHOUT_CONSTRAINT ) );
                }
            } else if ( node instanceof SubFlowNode ) {
                final SubFlowNode subFlow = (SubFlowNode) node;
                if ( subFlow.getFrom() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SUBFLOW_NODE_WITHOUT_INCOMING_CONNECTIONS ) );
                }

                if ( subFlow.getTo() == null ) {
                    errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SUBFLOW_NODE_WITHOUT_OUTGOING_CONNECTIONS ) );
                }
                if ( subFlow.getProcessId() == null ) {
                   errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.SUBFLOW_WITHOUT_PROCESS_ID ) );
                }
            }
        }
        if ( !startNodeFound ) {
            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.NO_START_NODE ) );
        }
        if ( !endNodeFound ) {
            errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.NO_END_NODE ) );
        }
        for ( final Iterator it = process.getVariables().iterator(); it.hasNext(); ) {
            final Variable variable = (Variable) it.next();
            if ( variable.getType() == null ) {
                errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.VARIABLE_WITHOUT_TYPE ) );
            }
        }

        checkAllNodesConnectedToStart( process,
                                       errors );

        return (RuleFlowProcessValidationError[]) errors.toArray( new RuleFlowProcessValidationError[errors.size()] );
    }

    private void checkAllNodesConnectedToStart(final RuleFlowProcess process,
                                               final List errors) {
        final Map processNodes = new HashMap();
        final Node[] nodes = process.getNodes();
        for ( int i = 0; i < nodes.length; i++ ) {
            final Node node = nodes[i];
            processNodes.put( node,
                              Boolean.FALSE );
        }

        final Node start = process.getStart();
        if ( start != null ) {
            processNode( start,
                         processNodes );
        }

        for ( final Iterator it = processNodes.keySet().iterator(); it.hasNext(); ) {
            final Node node = (Node) it.next();
            if ( Boolean.FALSE.equals( processNodes.get( node ) ) ) {
                errors.add( new RuleFlowProcessValidationErrorImpl( RuleFlowProcessValidationError.ALL_NODES_CONNECTED_TO_START ) );
            }
        }
    }

    private void processNode(final Node node,
                             final Map nodes) {
        if ( !nodes.containsKey( node ) ) {
            throw new IllegalStateException( "A process node is connected with " + "a node that does not belong to the process." );
        }
        final Boolean prevValue = (Boolean) nodes.put( node,
                                                       Boolean.TRUE );
        if ( prevValue == Boolean.FALSE ) {
            for ( final Iterator it = node.getOutgoingConnections().iterator(); it.hasNext(); ) {
                final Connection connection = (Connection) it.next();
                processNode( connection.getTo(),
                             nodes );
            }
        }
    }

}
