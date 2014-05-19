package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;

import java.util.Iterator;

public class GraphImpl<T> implements Graph<T>, Iterable<GraphNode<T>> {
    GraphStore<T> graphStore;

    public GraphImpl(GraphStore<T> graphStore) {
        this.graphStore = graphStore;
    }

    protected int idCounter;


    public GraphNode<T> addNode() {
        return graphStore.addNode();
    }

    public GraphNode<T> removeNode(int id) {
        return graphStore.removeNode(id);
    }

    @Override
    public GraphNode<T> getNode(int id) {
        return graphStore.getNode(id);
    }

    @Override
    public int size() {
        return graphStore.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return graphStore.iterator();
    }
}
