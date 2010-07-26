/**
 * Copyright 2010 JBoss Inc
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

package org.drools.ruleflow.core.factory;

import org.drools.process.core.context.exception.ActionExceptionHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.CompositeContextNode;

/**
 *
 * @author salaboy
 */
public class CompositeNodeFactory extends RuleFlowNodeContainerFactory {

	private RuleFlowNodeContainerFactory nodeContainerFactory;
	private NodeContainer nodeContainer;
	private long linkedIncomingNodeId = -1;
	private long linkedOutgoingNodeId = -1;
	
    public CompositeNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
    	this.nodeContainerFactory = nodeContainerFactory;
    	this.nodeContainer = nodeContainer;
    	CompositeContextNode compositeNode = new CompositeContextNode();
        compositeNode.setId(id);
        setNodeContainer(compositeNode);
    }
    
    protected CompositeContextNode getCompositeNode() {
    	return (CompositeContextNode) getNodeContainer();
    }

    public CompositeNodeFactory variable(String name, DataType type) {
    	return variable(name, type, null);
    }
    
    public CompositeNodeFactory variable(String name, DataType type, Object value) {
    	Variable variable = new Variable();
    	variable.setName(name);
    	variable.setType(type);
    	variable.setValue(value);
    	VariableScope variableScope = (VariableScope)
			getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE);
		if (variableScope == null) {
			variableScope = new VariableScope();
			getCompositeNode().addContext(variableScope);
			getCompositeNode().setDefaultContext(variableScope);
		}
		variableScope.getVariables().add(variable);
        return this;
    }
    
    public CompositeNodeFactory exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
    	ExceptionScope exceptionScope = (ExceptionScope)
			getCompositeNode().getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
		if (exceptionScope == null) {
			exceptionScope = new ExceptionScope();
			getCompositeNode().addContext(exceptionScope);
			getCompositeNode().setDefaultContext(exceptionScope);
		}
		exceptionScope.setExceptionHandler(exception, exceptionHandler);
    	return this;
    }
    
    public CompositeNodeFactory exceptionHandler(String exception, String dialect, String action) {
    	ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
    	exceptionHandler.setAction(new DroolsConsequenceAction(dialect, action));
    	return exceptionHandler(exception, exceptionHandler);
    }
    
    public CompositeNodeFactory linkIncomingConnections(long nodeId) {
    	this.linkedIncomingNodeId = nodeId;
        return this;
    }

    public CompositeNodeFactory linkOutgoingConnections(long nodeId) {
    	this.linkedOutgoingNodeId = nodeId;
    	return this;
    }

    public RuleFlowNodeContainerFactory done() {
    	if (linkedIncomingNodeId != -1) {
    		getCompositeNode().linkIncomingConnections(
				Node.CONNECTION_DEFAULT_TYPE,
		        linkedIncomingNodeId, Node.CONNECTION_DEFAULT_TYPE);
    	}
    	if (linkedOutgoingNodeId != -1) {
    		getCompositeNode().linkOutgoingConnections(
				linkedOutgoingNodeId, Node.CONNECTION_DEFAULT_TYPE,
	            Node.CONNECTION_DEFAULT_TYPE);
    	}
        nodeContainer.addNode(getCompositeNode());
        return nodeContainerFactory;
    }

}
