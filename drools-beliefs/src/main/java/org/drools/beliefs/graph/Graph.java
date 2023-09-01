package org.drools.beliefs.graph;

public interface Graph<T> extends Iterable<GraphNode<T>> {
    public GraphNode<T> addNode();

    public GraphNode<T> removeNode(int id);

    public GraphNode<T> getNode(int id);

    public int size();


}
