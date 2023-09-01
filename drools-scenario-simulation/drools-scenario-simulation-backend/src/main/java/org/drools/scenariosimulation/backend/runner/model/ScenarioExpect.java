package org.drools.scenariosimulation.backend.runner.model;

import java.util.List;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValue;

public class ScenarioExpect {

    private final FactIdentifier factIdentifier;

    private final List<FactMappingValue> expectedResult;

    private final boolean isNewFact;

    public ScenarioExpect(FactIdentifier factIdentifier, List<FactMappingValue> expectedResult, boolean isNewFact) {
        this.factIdentifier = factIdentifier;
        this.expectedResult = expectedResult;
        this.isNewFact = isNewFact;
    }

    public ScenarioExpect(FactIdentifier factIdentifier, List<FactMappingValue> expectedResult) {
        this(factIdentifier, expectedResult, false);
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public List<FactMappingValue> getExpectedResult() {
        return expectedResult;
    }

    public boolean isNewFact() {
        return isNewFact;
    }
}
