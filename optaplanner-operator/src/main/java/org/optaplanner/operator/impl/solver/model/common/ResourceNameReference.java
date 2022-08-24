package org.optaplanner.operator.impl.solver.model.common;

public final class ResourceNameReference {

    private String name;

    public ResourceNameReference() {
        // Required by Jackson.
    }

    public ResourceNameReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
