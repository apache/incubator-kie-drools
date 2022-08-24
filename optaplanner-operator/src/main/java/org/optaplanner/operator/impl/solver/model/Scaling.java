package org.optaplanner.operator.impl.solver.model;

public final class Scaling {

    private boolean dynamic = false;

    private int replicas = 1;

    public Scaling() {
        // Required by Jackson
    }

    public Scaling(boolean dynamic, int replicas) {
        this.dynamic = dynamic;
        this.replicas = replicas;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
