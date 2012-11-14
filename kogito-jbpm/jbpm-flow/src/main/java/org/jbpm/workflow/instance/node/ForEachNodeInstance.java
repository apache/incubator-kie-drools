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

package org.jbpm.workflow.instance.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.definition.process.Connection;
import org.kie.definition.process.Node;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachJoinNode;
import org.jbpm.workflow.core.node.ForEachNode.ForEachSplitNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.mvel2.MVEL;

/**
 * Runtime counterpart of a for each node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ForEachNodeInstance extends CompositeNodeInstance {

    private static final long serialVersionUID = 510l;
    
    public ForEachNode getForEachNode() {
        return (ForEachNode) getNode();
    }

    public NodeInstance getNodeInstance(final Node node) {
        // TODO do this cleaner for split / join of for each?
        if (node instanceof ForEachSplitNode) {
            ForEachSplitNodeInstance nodeInstance = new ForEachSplitNodeInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(this);
            nodeInstance.setProcessInstance(getProcessInstance());
            return nodeInstance;
        } else if (node instanceof ForEachJoinNode) {
            ForEachJoinNodeInstance nodeInstance = (ForEachJoinNodeInstance)
                getFirstNodeInstance(node.getId());
            if (nodeInstance == null) {
                nodeInstance = new ForEachJoinNodeInstance();
                nodeInstance.setNodeId(node.getId());
                nodeInstance.setNodeInstanceContainer(this);
                nodeInstance.setProcessInstance(getProcessInstance());
            }
            return nodeInstance;
        }
        return super.getNodeInstance(node);
    }
    
    private Collection<?> evaluateCollectionExpression(String collectionExpression) {
        // TODO: should evaluate this expression using MVEL
        Object collection = null;
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            resolveContextInstance(VariableScope.VARIABLE_SCOPE, collectionExpression);
        if (variableScopeInstance != null) {
            collection = variableScopeInstance.getVariable(collectionExpression);
        } else {
            try {
                collection = MVEL.eval(collectionExpression, new NodeInstanceResolverFactory(this));
            } catch (Throwable t) {
                throw new IllegalArgumentException(
                    "Could not find collection " + collectionExpression);
            }
            
        }
        if (collection == null) {
            return Collections.EMPTY_LIST;
        }
        if (collection instanceof Collection<?>) {
            return (Collection<?>) collection;
        }
        if (collection.getClass().isArray() ) {
            List<Object> list = new ArrayList<Object>();
            for (Object o: (Object[]) collection) {
                list.add(o);
            }
            return list;
        }
        throw new IllegalArgumentException(
            "Unexpected collection type: " + collection.getClass());
    }
    
    public class ForEachSplitNodeInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;
        
        public ForEachSplitNode getForEachSplitNode() {
            return (ForEachSplitNode) getNode();
        }

        public void internalTrigger(org.kie.runtime.process.NodeInstance fromm, String type) {
            String collectionExpression = getForEachNode().getCollectionExpression();
            Collection<?> collection = evaluateCollectionExpression(collectionExpression);
            ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            if (collection.isEmpty()) {
            	ForEachNodeInstance.this.triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
            } else {
            	List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();
            	for (Object o: collection) {
            		String variableName = getForEachNode().getVariableName();
            		NodeInstance nodeInstance = (NodeInstance)
            		((NodeInstanceContainer) getNodeInstanceContainer()).getNodeInstance(getForEachSplitNode().getTo().getTo());
            		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            			nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
            		variableScopeInstance.setVariable(variableName, o);
            		nodeInstances.add(nodeInstance);
            	}
            	for (NodeInstance nodeInstance: nodeInstances) {
            		((org.jbpm.workflow.instance.NodeInstance) nodeInstance).trigger(this, getForEachSplitNode().getTo().getToType());
            	}
	            if (!getForEachNode().isWaitForCompletion()) {
	            	ForEachNodeInstance.this.triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, false);
	            }
            }
        }


    }
    
    public class ForEachJoinNodeInstance extends NodeInstanceImpl {

        private static final long serialVersionUID = 510l;
        
        public ForEachJoinNode getForEachJoinNode() {
            return (ForEachJoinNode) getNode();
        }

        public void internalTrigger(org.kie.runtime.process.NodeInstance from, String type) {
            
            if (getForEachNode().getOutputVariableName() != null) {
                Collection outputCollection = evaluateCollectionExpression(getForEachNode().getOutputCollectionExpression());
                if (outputCollection == null) {
                    outputCollection = Collections.emptyList();
                }
            
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                ((NodeInstanceImpl)from).resolveContextInstance(VariableScope.VARIABLE_SCOPE, getForEachNode().getOutputVariableName());
                Object outputVariable = null;
                if (variableScopeInstance != null) {
                    outputVariable = variableScopeInstance.getVariable(getForEachNode().getOutputVariableName());
                }
                outputCollection.add(outputVariable);
                VariableScopeInstance subprocessVariableScopeInstance = (VariableScopeInstance)
                ((NodeInstanceImpl)from).resolveContextInstance(VariableScope.VARIABLE_SCOPE, getForEachNode().getOutputCollectionExpression());
                subprocessVariableScopeInstance.setVariable(getForEachNode().getOutputCollectionExpression(), outputCollection);
            }
            if (getNodeInstanceContainer().getNodeInstances().size() == 1) {
            	((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
                if (getForEachNode().isWaitForCompletion()) {
                	
                	if (System.getProperty("jbpm.enable.multi.con") == null) {
                		
                		triggerConnection(getForEachJoinNode().getTo());
                	} else {
                	
	                    List<Connection> connections = getForEachJoinNode().getOutgoingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
	                	for (Connection connection : connections) {
	                	    triggerConnection(connection);
	                	}
                	}
                }
            }
        }
        
    }
    
}
