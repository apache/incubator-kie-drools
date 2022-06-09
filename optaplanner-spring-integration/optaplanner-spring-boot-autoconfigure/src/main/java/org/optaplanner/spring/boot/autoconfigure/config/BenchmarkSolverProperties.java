package org.optaplanner.spring.boot.autoconfigure.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class BenchmarkSolverProperties {

    @NestedConfigurationProperty
    private TerminationProperties termination;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public TerminationProperties getTermination() {
        return termination;
    }

    public void setTermination(TerminationProperties termination) {
        this.termination = termination;
    }

}
