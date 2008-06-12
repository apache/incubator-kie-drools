package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;

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

    public ForEachNode() {
        // Split
        ForEachSplitNode split = new ForEachSplitNode();
        split.setName("ForEachSplit");
        addNode(split);
        linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE, 
            new CompositeNode.NodeAndType(split, Node.CONNECTION_DEFAULT_TYPE));
        // Composite node
        CompositeNode compositeNode = new CompositeNode();
        compositeNode.setName("ForEachComposite");
        addNode(compositeNode);
        VariableScope variableScope = new VariableScope();
        compositeNode.setContext(VariableScope.VARIABLE_SCOPE, variableScope);
        // Join
        ForEachJoinNode join = new ForEachJoinNode();
        join.setName("ForEachJoin");
        addNode(join);
        linkOutgoingConnections(
            new CompositeNode.NodeAndType(join, Node.CONNECTION_DEFAULT_TYPE),
            Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public CompositeNode getCompositeNode() {
        return (CompositeNode) getNode(2); 
    }

    public void setVariable(String variableName, DataType type) {
        this.variableName = variableName;
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName(variableName);
        variable.setType(type);
        variables.add(variable);
        ((VariableScope) getCompositeNode().getContext(VariableScope.VARIABLE_SCOPE)).setVariables(variables);
        // TODO: can only create connections after linking composite node ports 
        new ConnectionImpl(
            getNode(1), Node.CONNECTION_DEFAULT_TYPE,
            getCompositeNode(), Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            getCompositeNode(), Node.CONNECTION_DEFAULT_TYPE,
            getNode(3), Node.CONNECTION_DEFAULT_TYPE
        );
    }
    
    public String getCollectionExpression() {
        return collectionExpression;
    }

    public void setCollectionExpression(String collectionExpression) {
        this.collectionExpression = collectionExpression;
    }

    public class ForEachSplitNode extends SequenceNode {
        private static final long serialVersionUID = 4L;
    }

    public class ForEachJoinNode extends SequenceNode {
        private static final long serialVersionUID = 4L;
    }

}
