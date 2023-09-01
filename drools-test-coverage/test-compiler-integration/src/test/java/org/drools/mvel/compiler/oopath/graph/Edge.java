package org.drools.mvel.compiler.oopath.graph;

public class Edge {

    private Vertex<?> inV;
    private Vertex<?> outV;

    public Vertex<?> getInV() {
        return inV;
    }

    public void setInV( Vertex<?> inV ) {
        this.inV = inV;
    }

    public Vertex<?> getOutV() {
        return outV;
    }

    public void setOutV( Vertex<?> outV ) {
        this.outV = outV;
    }
}
