package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.GraphNode;

public interface GraphStore<T> extends Iterable<GraphNode<T>> {
    public GraphNode<T> addNode();

    public GraphNode<T> removeNode(int id);

    public GraphNode<T> getNode(int id);

    public int size();
}
