package org.drools.workflow.core.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.process.core.Context;
import org.drools.workflow.core.NodeContainer;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class NodeContainerImpl implements Serializable, NodeContainer {

    private static final long serialVersionUID = 400L;

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
            throw new IllegalArgumentException("Unknown node id: " + id);
        }
        return node; 
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
