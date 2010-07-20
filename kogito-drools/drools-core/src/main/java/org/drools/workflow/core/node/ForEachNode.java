package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Node;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

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

/**
 * A for each node.
 * 
 * This node activates the contained subflow for each element of a collection.
 * The node continues if all activated the subflow has been completed for each
 * of the elements in the collection.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ForEachNode extends CompositeNode {
    
    private static final long serialVersionUID = 4L;
    
    private String variableName;
    private String collectionExpression;
    private boolean waitForCompletion = true;

    public ForEachNode() {
        // Split
        ForEachSplitNode split = new ForEachSplitNode();
        split.setName("ForEachSplit");
        split.setMetaData("hidden", true);
        super.addNode(split);
        super.linkIncomingConnections(
            org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, 
            new CompositeNode.NodeAndType(split, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE));
        // Composite node
        CompositeContextNode compositeNode = new CompositeContextNode();
        compositeNode.setName("ForEachComposite");
        compositeNode.setMetaData("hidden", true);
        super.addNode(compositeNode);
        VariableScope variableScope = new VariableScope();
        compositeNode.addContext(variableScope);
        compositeNode.setDefaultContext(variableScope);
        // Join
        ForEachJoinNode join = new ForEachJoinNode();
        join.setName("ForEachJoin");
        join.setMetaData("hidden", true);
        super.addNode(join);
        super.linkOutgoingConnections(
            new CompositeNode.NodeAndType(join, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE),
            org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        new ConnectionImpl(
            super.getNode(1), org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
            getCompositeNode(), org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            getCompositeNode(), org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
            super.getNode(3), org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE
        );
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public DataType getVariableType() {
    	if (variableName == null) {
    		return null;
    	}
    	for (Variable variable: ((VariableScope) getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE)).getVariables()) {
    		if (variableName.equals(variable.getName())) {
    			return variable.getType();
    		}
    	}
    	return null;
    }
    
    public CompositeContextNode getCompositeNode() {
        return (CompositeContextNode) super.getNode(2); 
    }
    
    public ForEachSplitNode getForEachSplitNode() {
        return (ForEachSplitNode) super.getNode(1); 
    }
    
    public ForEachJoinNode getForEachJoinNode() {
        return (ForEachJoinNode) super.getNode(3); 
    }
    
    public void addNode(Node node) {
    	getCompositeNode().addNode(node);
    }
    
    protected void internalAddNode(Node node) {
    	super.addNode(node);
    }
    
    public Node getNode(long id) {
    	return getCompositeNode().getNode(id);
    }
    
    public Node internalGetNode(long id) {
    	return super.getNode(id);
    }
    
    public Node[] getNodes() {
    	return getCompositeNode().getNodes();
    }
    
    public void removeNode(Node node) {
    	getCompositeNode().removeNode(node);
    }
    
    protected void internalRemoveNode(Node node) {
    	super.removeNode(node);
    }
    
    public void linkIncomingConnections(String inType, long inNodeId, String inNodeType) {
    	getCompositeNode().linkIncomingConnections(inType, inNodeId, inNodeType);
    }

    public void linkOutgoingConnections(long outNodeId, String outNodeType, String outType) {
    	getCompositeNode().linkOutgoingConnections(outNodeId, outNodeType, outType);
	}
    
    public CompositeNode.NodeAndType getLinkedIncomingNode(String inType) {
    	return getCompositeNode().getLinkedIncomingNode(inType);
    }

    public CompositeNode.NodeAndType internalGetLinkedIncomingNode(String inType) {
        return super.getLinkedIncomingNode(inType);
    }
    
    public CompositeNode.NodeAndType getLinkedOutgoingNode(String inType) {
    	return getCompositeNode().getLinkedOutgoingNode(inType);
    }

    public CompositeNode.NodeAndType internalGetLinkedOutgoingNode(String inType) {
        return super.getLinkedOutgoingNode(inType);
    }
    
    public void setVariable(String variableName, DataType type) {
        this.variableName = variableName;
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName(variableName);
        variable.setType(type);
        variables.add(variable);
        ((VariableScope) getCompositeNode().getDefaultContext(VariableScope.VARIABLE_SCOPE)).setVariables(variables);
    }
    
    public String getCollectionExpression() {
        return collectionExpression;
    }

    public void setCollectionExpression(String collectionExpression) {
        this.collectionExpression = collectionExpression;
    }

    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

   public class ForEachSplitNode extends ExtendedNodeImpl {
        private static final long serialVersionUID = 4L;
    }

    public class ForEachJoinNode extends ExtendedNodeImpl {
        private static final long serialVersionUID = 4L;
    }

}
