package org.optaplanner.core.impl.exhaustivesearch.node;

public class ExhaustiveSearchLayer {

    private final int depth;
    private final Object entity;

    private long nextBreadth;

    public ExhaustiveSearchLayer(int depth, Object entity) {
        this.depth = depth;
        this.entity = entity;
        nextBreadth = 0L;
    }

    public int getDepth() {
        return depth;
    }

    public Object getEntity() {
        return entity;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public boolean isLastLayer() {
        return entity == null;
    }

    public long assignBreadth() {
        long breadth = nextBreadth;
        nextBreadth++;
        return breadth;
    }

    @Override
    public String toString() {
        return depth + (isLastLayer() ? " last layer" : " (" + entity + ")");
    }

}
