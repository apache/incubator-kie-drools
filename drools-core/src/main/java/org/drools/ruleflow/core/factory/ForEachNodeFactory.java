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

/*
/*
 * Copyright 2008 JBoss Inc
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

import org.drools.process.core.datatype.DataType;
import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.ForEachNode;

/**
 *
 * @author salaboy
 */
public class ForEachNodeFactory extends RuleFlowNodeContainerFactory {
	
	private RuleFlowNodeContainerFactory nodeContainerFactory;
	private NodeContainer nodeContainer;
	private long linkedIncomingNodeId = -1;
	private long linkedOutgoingNodeId = -1;

    public ForEachNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
    	this.nodeContainerFactory = nodeContainerFactory;
    	this.nodeContainer = nodeContainer;
    	ForEachNode forEachNode = new ForEachNode();
        forEachNode.setId(id);
        setNodeContainer(forEachNode);
    }
    
    protected ForEachNode getForEachNode() {
    	return (ForEachNode) getNodeContainer();
    }

    public ForEachNodeFactory collectionExpression(String collectionExpression) {
    	getForEachNode().setCollectionExpression(collectionExpression);
        return this;
    }

    public ForEachNodeFactory variable(String variableName, DataType dataType) {
    	getForEachNode().setVariable(variableName, dataType);
        return this;
    }

    public ForEachNodeFactory waitForCompletion(boolean waitForCompletion) {
    	getForEachNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public ForEachNodeFactory linkIncomingConnections(long nodeId) {
    	this.linkedIncomingNodeId = nodeId;
        return this;
    }

    public ForEachNodeFactory linkOutgoingConnections(long nodeId) {
    	this.linkedOutgoingNodeId = nodeId;
    	return this;
    }

    public RuleFlowNodeContainerFactory done() {
    	if (linkedIncomingNodeId != -1) {
			getForEachNode().linkIncomingConnections(
				Node.CONNECTION_DEFAULT_TYPE,
		        linkedIncomingNodeId, Node.CONNECTION_DEFAULT_TYPE);
    	}
    	if (linkedOutgoingNodeId != -1) {
    		getForEachNode().linkOutgoingConnections(
				linkedOutgoingNodeId, Node.CONNECTION_DEFAULT_TYPE,
	            Node.CONNECTION_DEFAULT_TYPE);
    	}
        nodeContainer.addNode(getForEachNode());
        return nodeContainerFactory;
    }

}
