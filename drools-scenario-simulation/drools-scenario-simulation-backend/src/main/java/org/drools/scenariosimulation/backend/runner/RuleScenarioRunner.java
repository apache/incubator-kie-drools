package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.kie.api.runtime.KieContainer;

public class RuleScenarioRunner extends AbstractScenarioRunner {

    public RuleScenarioRunner(KieContainer kieContainer, ScenarioRunnerDTO scenarioRunnerDTO) {
        super(kieContainer,
              scenarioRunnerDTO,
              ExpressionEvaluatorFactory.create(
                      kieContainer.getClassLoader(),
                      scenarioRunnerDTO.getSettings().getType()));
    }

    @Override
    protected AbstractRunnerHelper newRunnerHelper() {
        return new RuleScenarioRunnerHelper();
    }
}
