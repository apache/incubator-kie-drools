package org.optaplanner.operator.impl.solver.model;

public class Scaling {

    private boolean dynamic;

    private int replicas = 1;

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
