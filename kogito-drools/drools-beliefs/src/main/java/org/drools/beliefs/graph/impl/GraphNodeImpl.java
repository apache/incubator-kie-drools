package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.Edge;
import org.drools.beliefs.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeImpl<T> implements GraphNode<T> {
    private int id;
    private T content;


    private List<Edge> inEdges = new ArrayList<Edge>();
    private List<Edge> outEdges = new ArrayList<Edge>();

    public GraphNodeImpl(int id) {
        this.id = id;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public int getId() {
        return this.id;
    }

    public void addInEdge(Edge outEdge) {
        inEdges.add( outEdge );
    }

    public void addOutEdge(Edge inEdge) {
        outEdges.add( inEdge );
    }

    public void removeInEdge(Edge outEdge) {
        inEdges.remove(outEdge);
    }

    public void removeOutEdge(Edge inEdge) {
        outEdges.remove(inEdge);
    }

    @Override
    public List<Edge> getInEdges() {
        return inEdges;
    }

    @Override
    public List<Edge> getOutEdges() {
        return outEdges;
    }

    @Override
    public String toString() {
        return "VertexImpl{" +
               "id=" + id +
               ", content=" + content +
               ", inEdges=" + inEdges +
               ", outEdges=" + outEdges +
               '}';
    }
}
