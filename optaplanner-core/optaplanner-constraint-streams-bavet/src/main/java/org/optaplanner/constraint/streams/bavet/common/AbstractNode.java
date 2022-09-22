package org.optaplanner.constraint.streams.bavet.common;

public abstract class AbstractNode {

    private long id;

    public abstract void calculateScore();

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        // Useful for debugging if a constraint has multiple nodes of the same type
        return getClass().getSimpleName() + "-" + id;
    }

}
