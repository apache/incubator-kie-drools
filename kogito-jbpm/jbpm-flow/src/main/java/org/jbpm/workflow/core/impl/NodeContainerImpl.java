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

package org.jbpm.workflow.core.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.definition.process.Node;
import org.jbpm.process.core.Context;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class NodeContainerImpl implements Serializable, NodeContainer {

    private static final long serialVersionUID = 510l;

    private Map<Long, Node> nodes;

    public NodeContainerImpl() {
        this.nodes = new HashMap<Long, Node>();
    }

    public void addNode(final Node node) {
        validateAddNode(node);
        if (!this.nodes.containsValue(node)) {
            this.nodes.put(new Long(node.getId()), node);
        }
    }

    protected void validateAddNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null!");
        }
    }

    public Node[] getNodes() {
        return (Node[]) this.nodes.values()
                .toArray(new Node[this.nodes.size()]);
    }

    public Node getNode(final long id) {
        Node node = this.nodes.get(id);
        if (node == null) {
            node = getInnerNode(id, this.nodes.values().toArray(new Node[this.nodes.size()]));
        }
        if (node == null) {
            throw new IllegalArgumentException("Unknown node id: " + id);
        }
        return node; 
    }

    /**
     * If any of the inner nodes are CompositeNodes: 
     * 1. Check if they contain Node[id]
     * 2. Otherwise, call this on the nodes of the inner CompositeNode
     * @param id The id of the Node we're searching for
     * @param nodes The list of nodes from the CompositeNode we encountered in the method above us.
     * @return The searched for Node, or null, if it could not be found. 
     */
    private Node getInnerNode(final long id, Node [] nodes) { 
        for( Node node : nodes ) { 
            if( node instanceof CompositeNode ) { 
                Node innerNode = ((CompositeNode) node).getNode(id);
                if( innerNode != null ) { 
                    return innerNode;
                }
                return getInnerNode(id, ((CompositeNode) node).getNodes() );
            }
        }
        return null;
    }
    
    public Node internalGetNode(long id) {
    	return getNode(id);
    }

    public void removeNode(final Node node) {
        validateRemoveNode(node);
        this.nodes.remove(new Long(node.getId()));
    }

    protected void validateRemoveNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null");
        }
        if (this.nodes.get(node.getId()) == null) {
            throw new IllegalArgumentException("Unknown node: " + node);
        }
    }

    public Context resolveContext(String contextId, Object param) {
        return null;
    }

}
