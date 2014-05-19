package org.drools.beliefs.graph;

import java.util.List;

public interface GraphNode<T> {
    public int getId();

    public List<Edge> getInEdges();

    public List<Edge> getOutEdges();

    public T getContent();

    public void setContent(T content);
}
