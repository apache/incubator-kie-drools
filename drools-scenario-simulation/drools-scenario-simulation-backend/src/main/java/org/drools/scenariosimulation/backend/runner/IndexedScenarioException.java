package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.backend.util.ScenarioSimulationServerMessages;

public class IndexedScenarioException extends ScenarioException {

    private final int index;
    private final String scenarioDescription;
    private final String fileName;

    public IndexedScenarioException(int index, String scenarioDescription, String fileName, Throwable cause) {
        super(cause);
        this.index = index;
        this.scenarioDescription = scenarioDescription;
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        String exceptionMessage = getCause() != null ? getCause().getMessage() : super.getMessage();
        return ScenarioSimulationServerMessages.getIndexedScenarioMessage(exceptionMessage,
                                                                          index,
                                                                          scenarioDescription,
                                                                          fileName);
    }
}
