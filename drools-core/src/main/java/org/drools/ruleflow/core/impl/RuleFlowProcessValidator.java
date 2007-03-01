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

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IEndNode;
import org.drools.ruleflow.core.IJoin;
import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.core.IRuleSetNode;
import org.drools.ruleflow.core.ISplit;
import org.drools.ruleflow.core.IStartNode;
import org.drools.ruleflow.core.IVariable;
import org.drools.ruleflow.core.IRuleFlowProcess;
import org.drools.ruleflow.core.IRuleFlowProcessValidationError;
import org.drools.ruleflow.core.IRuleFlowProcessValidator;

/**
 * Default implementation of a RuleFlow validator.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcessValidator implements IRuleFlowProcessValidator {
	
    private static RuleFlowProcessValidator instance;
    
    private RuleFlowProcessValidator() {
    }
    
    public static RuleFlowProcessValidator getInstance() {
        if (instance == null) {
            instance = new RuleFlowProcessValidator();
        }
        return instance;
    }
    
    public IRuleFlowProcessValidationError[] validateProcess(IRuleFlowProcess process) {
        List errors = new ArrayList();
        
        if (process.getName() == null) {
            errors.add(new RuleFlowProcessValidationError(
                    IRuleFlowProcessValidationError.NO_PROCESS_NAME));
        }
        
        if (process.getId() == null || "".equals(process.getId())) {
            errors.add(new RuleFlowProcessValidationError(
                    IRuleFlowProcessValidationError.NO_PROCESS_ID));
        }
        
        // check start node of process
        if (process.getStart() == null) {
            errors.add(new RuleFlowProcessValidationError(
					IRuleFlowProcessValidationError.NO_START_NODE));
        }
        
        boolean startNodeFound = false;
        boolean endNodeFound = false;
        INode[] nodes = process.getNodes();
        for (int i = 0; i < nodes.length; i++) { 
        	INode node = nodes[i];
            if (node instanceof IStartNode) {
                IStartNode startNode = (IStartNode) node;
                startNodeFound = true;
                if (startNode.getTo() == null) {
                    errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.START_NODE_WITHOUT_OUTGOING_NODES));
                }
            } else if (node instanceof IEndNode) {
                IEndNode endNode = (IEndNode) node;
                endNodeFound = true;            
                if (endNode.getFrom() == null) {
                    errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.END_NODE_HAS_NO_INCOMING_CONNECTIONS));
                }
            } else if (node instanceof IRuleSetNode) {
                IRuleSetNode ruleSetNode = (IRuleSetNode) node;
                if (ruleSetNode.getFrom() == null) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_INCOMING_CONNECTIONS));                    
                }
                
                if (ruleSetNode.getTo() == null) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_OUTGOING_CONNECTIONS));
                }
                String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
                if (ruleFlowGroup == null || "".equals(ruleFlowGroup)) {
                	errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.RULE_SET_NODE_WITHOUT_RULE_SET_GROUP));
                }
            } else if (node instanceof ISplit) {
                ISplit split = (ISplit) node;
                if (split.getType() == ISplit.TYPE_UNDEFINED) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.SPLIT_WITHOUT_TYPE));
                }
                if (split.getFrom() == null) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.SPLIT_WITHOUT_INCOMING_CONNECTION));
                }
                if (split.getOutgoingConnections().size() < 2) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.SPLIT_NOT_ENOUGH_OUTGOING_CONNECTIONS));
                }
				if (split.getType() == ISplit.TYPE_XOR || split.getType() == ISplit.TYPE_OR) {
                	for (Iterator it = split.getOutgoingConnections().iterator(); it.hasNext(); ) {
                		IConnection connection = (IConnection) it.next();
						if (split.getConstraint(connection) == null) {
							errors.add(new RuleFlowProcessValidationError(
									IRuleFlowProcessValidationError.SPLIT_OUTGOING_CONNECTION_WITHOUT_CONSTRAINT));
                		}
                	}
                }
            } else if (node instanceof IJoin) {
                IJoin join = (IJoin) node;
                if (join.getType() == IJoin.TYPE_UNDEFINED) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.JOIN_WITHOUT_TYPE));
                }
                if (join.getIncomingConnections().size() < 2) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.JOIN_NOT_ENOUGH_INCOMING_CONNECTIONS));
                }
                if (join.getTo() == null) {
					errors.add(new RuleFlowProcessValidationError(
							IRuleFlowProcessValidationError.JOIN_WITHOUT_OUTGOING_CONNECTION));
                }
            }
        }
        if (!startNodeFound) {
			errors.add(new RuleFlowProcessValidationError(
					IRuleFlowProcessValidationError.NO_START_NODE));
        }
        if (!endNodeFound) {
            errors.add(new RuleFlowProcessValidationError(
					IRuleFlowProcessValidationError.NO_END_NODE));
        }
        for (Iterator it = process.getVariables().iterator(); it.hasNext(); ) {
        	IVariable variable = (IVariable) it.next();
            if (variable.getType() == null) {
				errors.add(new RuleFlowProcessValidationError(
						IRuleFlowProcessValidationError.VARIABLE_WITHOUT_TYPE));
            }
        }
        
        checkAllNodesConnectedToStart(process, errors);
		
		return (IRuleFlowProcessValidationError[]) errors.toArray(new IRuleFlowProcessValidationError[errors.size()]);
    }

	private void checkAllNodesConnectedToStart(
			IRuleFlowProcess process, 
			List errors) {
		Map processNodes = new HashMap();  
        INode[] nodes = process.getNodes();
        for (int i = 0; i < nodes.length; i++) { 
        	INode node = nodes[i];
			processNodes.put(node, Boolean.FALSE);
		}
		
		INode start = process.getStart();
		if (start != null) {
			processNode(start, processNodes);		
		}
		
		for (Iterator it = processNodes.keySet().iterator(); it.hasNext(); ) {
			INode node = (INode) it.next();
			if (Boolean.FALSE.equals(processNodes.get(node))) {
				errors.add(new RuleFlowProcessValidationError(
						IRuleFlowProcessValidationError.ALL_NODES_CONNECTED_TO_START));
			}
		}
	}
	
	private void processNode(INode node, Map nodes) {
		if (!nodes.containsKey(node)) {
			throw new IllegalStateException("A process node is connected with "
					+ "a node that does not belong to the process.");
		}
		Boolean prevValue = (Boolean) nodes.put(node, Boolean.TRUE);
        if (prevValue == Boolean.FALSE) {
    		for (Iterator it = node.getOutgoingConnections().iterator(); it.hasNext(); ) {
    			IConnection connection = (IConnection) it.next();
    			processNode(connection.getTo(), nodes);
    		}
        }
	}
	
}
