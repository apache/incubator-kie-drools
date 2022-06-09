package org.optaplanner.operator.impl.solver.model.messaging;

public enum MessageAddress {

    INPUT("problem"),
    OUTPUT("solution");

    private final String name;

    MessageAddress(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
