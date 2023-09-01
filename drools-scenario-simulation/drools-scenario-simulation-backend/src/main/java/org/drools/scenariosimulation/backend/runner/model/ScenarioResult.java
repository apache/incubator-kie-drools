package org.drools.scenariosimulation.backend.runner.model;

import java.util.Optional;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValue;

public class ScenarioResult {

    private final FactMappingValue factMappingValue;
    private final Object resultValue;
    private boolean result = false;

    public ScenarioResult(FactMappingValue factMappingValue) {
        this(factMappingValue, null);
    }

    public ScenarioResult(FactMappingValue factMappingValue, Object resultValue) {
        this.factMappingValue = factMappingValue;
        this.resultValue = resultValue;
    }

    public FactIdentifier getFactIdentifier() {
        return factMappingValue.getFactIdentifier();
    }

    public FactMappingValue getFactMappingValue() {
        return factMappingValue;
    }

    public Optional<Object> getResultValue() {
        return Optional.ofNullable(resultValue);
    }

    public ScenarioResult setResult(boolean result) {
        this.result = result;
        return this;
    }

    public boolean getResult() {
        return result;
    }
}
