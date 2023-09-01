package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.kie.api.runtime.KieContainer;

@FunctionalInterface
public interface ScenarioRunnerProvider {

    AbstractScenarioRunner create(KieContainer kieContainer,
                                  ScenarioRunnerDTO scenarioRunnerDTO);
}
