package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.backend.util.ScenarioSimulationServerMessages;

public class IndexedScenarioAssertionError extends AssertionError {

    private final int index;
    private final String scenarioDescription;
    private final String fileName;

    public IndexedScenarioAssertionError(int index, String scenarioDescription, String fileName, Throwable cause) {
        super(cause);
        this.index = index;
        this.scenarioDescription = scenarioDescription;
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        String assertionError = getCause() != null ? getCause().getMessage() : super.getMessage();
        return ScenarioSimulationServerMessages.getIndexedScenarioMessage(assertionError,
                                                                          index,
                                                                          scenarioDescription,
                                                                          fileName);
    }
}
