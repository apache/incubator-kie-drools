package org.drools.verifier.core.maps;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public abstract class InspectorFactory<Result, Input> {

    protected AnalyzerConfiguration configuration;

    public InspectorFactory(final AnalyzerConfiguration configuration) {
        this.configuration = configuration;
    }

    public abstract Result make(final Input input);
}
