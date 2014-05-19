package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.Edge;
import org.drools.beliefs.graph.GraphNode;

public class EdgeImpl implements Edge {

    private GraphNode inGraphNode;

    private GraphNode outGraphNode;

    @Override
    public GraphNode getInGraphNode() {
        return inGraphNode;
    }

    @Override
    public GraphNode getOutGraphNode() {
        return outGraphNode;
    }

    public void setInGraphNode(GraphNode inGraphNode) {
        this.inGraphNode = inGraphNode;
        ((GraphNodeImpl) inGraphNode).addInEdge(this);
    }

    public void setOutGraphNode(GraphNode outGraphNode) {
        this.outGraphNode = outGraphNode;
        ((GraphNodeImpl) outGraphNode).addOutEdge(this);
    }

    @Override
    public String toString() {
        return "EdgeImpl{" +
               "inVertex=" + inGraphNode.getId() +
               ", outVertex=" + outGraphNode.getId() +
               '}';
    }
}
