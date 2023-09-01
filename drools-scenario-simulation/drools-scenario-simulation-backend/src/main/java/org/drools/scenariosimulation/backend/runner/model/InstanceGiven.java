package org.drools.scenariosimulation.backend.runner.model;

import org.drools.scenariosimulation.api.model.FactIdentifier;

public class InstanceGiven {

    private final FactIdentifier factIdentifier;
    private final Object value;

    public InstanceGiven(FactIdentifier factIdentifier, Object value) {
        this.factIdentifier = factIdentifier;
        this.value = value;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public Object getValue() {
        return value;
    }
}
