package org.drools.beliefs.graph.impl;

import org.drools.beliefs.graph.GraphNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListGraphStore<T> implements GraphStore<T> {
    List<GraphNode<T>> nodes = new ArrayList<GraphNode<T>>();

    List<Integer> oldIds = new ArrayList<Integer>();

    @Override
    public GraphNode<T> addNode() {
        GraphNode<T> v = new GraphNodeImpl<T>(nodes.size());
        nodes.add( v );
        return v;
    }

    @Override
    public GraphNode<T> removeNode(int id) {
        throw new UnsupportedOperationException(ListGraphStore.class.getSimpleName() + " is additive only" );
    }

    @Override
    public GraphNode<T> getNode(int id) {
        return nodes.get( id );
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return nodes.iterator();
    }
}
