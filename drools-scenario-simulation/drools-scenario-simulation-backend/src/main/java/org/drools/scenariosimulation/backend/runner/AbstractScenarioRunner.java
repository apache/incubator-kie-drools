/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.runner;

import java.util.List;
import java.util.Optional;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;

public abstract class AbstractScenarioRunner extends Runner {

    protected final ClassLoader classLoader;
    protected final ExpressionEvaluatorFactory expressionEvaluatorFactory;
    protected final Description desc;
    protected final KieContainer kieContainer;
    protected final ScenarioRunnerDTO scenarioRunnerDTO;
    protected SimulationRunMetadataBuilder simulationRunMetadataBuilder;

    public AbstractScenarioRunner(KieContainer kieContainer,
                                  ScenarioRunnerDTO scenarioRunnerDTO,
                                  ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        this.kieContainer = kieContainer;
        this.scenarioRunnerDTO = scenarioRunnerDTO;
        this.desc = getDescriptionForSimulation(getFileName(), scenarioRunnerDTO.getScenarioWithIndices());
        this.classLoader = kieContainer.getClassLoader();
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }

    public static Description getDescriptionForSimulation(Optional<String> className, List<ScenarioWithIndex> scenarios) {
        Description suiteDescription = Description.createSuiteDescription("Test Scenarios (Preview) tests");
        scenarios.forEach(scenarioWithIndex -> suiteDescription.addChild(
                getDescriptionForScenario(className, scenarioWithIndex.getIndex(), scenarioWithIndex.getScesimData())));
        return suiteDescription;
    }

    public static Description getDescriptionForScenario(Optional<String> className, int index, Scenario scenario) {
        return Description.createTestDescription(className.orElse(AbstractScenarioRunner.class.getCanonicalName()),
                                                 String.format("#%d: %s", index, scenario.getDescription()));
    }

    public static ScenarioRunnerProvider getSpecificRunnerProvider(Type type) {
        if (Type.RULE.equals(type)) {
            return RuleScenarioRunner::new;
        } else if (Type.DMN.equals(type)) {
            return DMNScenarioRunner::new;
        } else {
            throw new IllegalArgumentException("Impossible to run simulation of type " + type);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        simulationRunMetadataBuilder = SimulationRunMetadataBuilder.create();

        notifier.fireTestStarted(getDescription());
        for (ScenarioWithIndex scenarioWithIndex : scenarioRunnerDTO.getScenarioWithIndices()) {
            singleRunScenario(scenarioWithIndex, notifier, scenarioRunnerDTO.getSettings(), scenarioRunnerDTO.getBackground())
                    .ifPresent(simulationRunMetadataBuilder::addScenarioResultMetadata);
        }
        notifier.fireTestStarted(getDescription());
    }

    @Override
    public Description getDescription() {
        return this.desc;
    }

    protected Optional<ScenarioResultMetadata> singleRunScenario(ScenarioWithIndex scenarioWithIndex, RunNotifier runNotifier, Settings settings, Background background) {
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

        int index = scenarioWithIndex.getIndex();
        Description descriptionForScenario = getDescriptionForScenario(getFileName(), index, scenarioWithIndex.getScesimData());
        runNotifier.fireTestStarted(descriptionForScenario);

        try {
            internalRunScenario(scenarioWithIndex, scenarioRunnerData, settings, background);
        } catch (ScenarioException e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, e);
            indexedScenarioException.setFileName(scenarioRunnerDTO.getFileName());
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        } catch (Exception e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, "Unexpected test error in scenario '" +
                    scenarioWithIndex.getScesimData().getDescription() + "'", e);
            indexedScenarioException.setFileName(scenarioRunnerDTO.getFileName());
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        }

        runNotifier.fireTestFinished(descriptionForScenario);

        return scenarioRunnerData.getMetadata();
    }

    protected void internalRunScenario(ScenarioWithIndex scenarioWithIndex, ScenarioRunnerData scenarioRunnerData, Settings settings, Background background) {
        newRunnerHelper().run(getKieContainer(),
                              scenarioRunnerDTO.getSimulationModelDescriptor(),
                              scenarioWithIndex,
                              expressionEvaluatorFactory,
                              getClassLoader(),
                              scenarioRunnerData,
                              settings,
                              background);
    }

    public Optional<String> getFileName() {
        return Optional.ofNullable(scenarioRunnerDTO.getFileName());
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public Optional<SimulationRunMetadata> getLastRunResultMetadata() {
        return this.simulationRunMetadataBuilder != null ?
                Optional.of(this.simulationRunMetadataBuilder.build()) :
                Optional.empty();
    }

    protected abstract AbstractRunnerHelper newRunnerHelper();
}
