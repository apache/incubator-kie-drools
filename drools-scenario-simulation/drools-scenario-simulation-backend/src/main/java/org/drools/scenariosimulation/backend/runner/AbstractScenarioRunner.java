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

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
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
    protected final SimulationDescriptor simulationDescriptor;
    protected List<ScenarioWithIndex> scenarios;
    protected String fileName;
    protected SimulationRunMetadataBuilder simulationRunMetadataBuilder;

    public AbstractScenarioRunner(KieContainer kieContainer,
                                  Simulation simulation,
                                  String fileName,
                                  ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        this(kieContainer, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex(), fileName, expressionEvaluatorFactory);
    }

    public AbstractScenarioRunner(KieContainer kieContainer,
                                  SimulationDescriptor simulationDescriptor,
                                  List<ScenarioWithIndex> scenarios,
                                  String fileName,
                                  ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        this.kieContainer = kieContainer;
        this.simulationDescriptor = simulationDescriptor;
        this.scenarios = scenarios;
        this.fileName = fileName;
        this.desc = getDescriptionForSimulation(getFileName(), simulationDescriptor, scenarios);
        this.classLoader = kieContainer.getClassLoader();
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }

    @Override
    public void run(RunNotifier notifier) {
        simulationRunMetadataBuilder = SimulationRunMetadataBuilder.create();

        notifier.fireTestStarted(getDescription());
        for (ScenarioWithIndex scenarioWithIndex : scenarios) {
            singleRunScenario(scenarioWithIndex, notifier)
                    .ifPresent(simulationRunMetadataBuilder::addScenarioResultMetadata);
        }
        notifier.fireTestStarted(getDescription());
    }

    @Override
    public Description getDescription() {
        return this.desc;
    }

    protected Optional<ScenarioResultMetadata> singleRunScenario(ScenarioWithIndex scenarioWithIndex, RunNotifier runNotifier) {
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

        int index = scenarioWithIndex.getIndex();
        Description descriptionForScenario = getDescriptionForScenario(getFileName(), index, scenarioWithIndex.getScenario());
        runNotifier.fireTestStarted(descriptionForScenario);

        try {
            internalRunScenario(scenarioWithIndex, scenarioRunnerData);
        } catch (ScenarioException e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        } catch (Throwable e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, "Unexpected test error in scenario '" +
                    scenarioWithIndex.getScenario().getDescription() + "'", e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        }

        runNotifier.fireTestFinished(descriptionForScenario);

        return scenarioRunnerData.getMetadata();
    }

    protected void internalRunScenario(ScenarioWithIndex scenarioWithIndex, ScenarioRunnerData scenarioRunnerData) {
        newRunnerHelper().run(getKieContainer(),
                              getSimulationDescriptor(),
                              scenarioWithIndex,
                              expressionEvaluatorFactory,
                              getClassLoader(),
                              scenarioRunnerData);
    }

    public Optional<String> getFileName() {
        return Optional.ofNullable(fileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    public Optional<SimulationRunMetadata> getLastRunResultMetadata() {
        return this.simulationRunMetadataBuilder != null ?
                Optional.of(this.simulationRunMetadataBuilder.build()) :
                Optional.empty();
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, Simulation simulation) {
        return getDescriptionForSimulation(filename, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex());
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, SimulationDescriptor simulationDescriptor, List<ScenarioWithIndex> scenarios) {
        Description suiteDescription = Description.createSuiteDescription("Test Scenarios (Preview) tests");
        scenarios.forEach(scenarioWithIndex -> suiteDescription.addChild(
                getDescriptionForScenario(filename, scenarioWithIndex.getIndex(), scenarioWithIndex.getScenario())));
        return suiteDescription;
    }

    public static Description getDescriptionForScenario(Optional<String> className, int index, Scenario scenario) {
        return Description.createTestDescription(className.orElse(AbstractScenarioRunner.class.getCanonicalName()),
                                                 String.format("#%d: %s", index, scenario.getDescription()));
    }

    public static ScenarioRunnerProvider getSpecificRunnerProvider(SimulationDescriptor simulationDescriptor) {
        if (Type.RULE.equals(simulationDescriptor.getType())) {
            return RuleScenarioRunner::new;
        } else if (Type.DMN.equals(simulationDescriptor.getType())) {
            return DMNScenarioRunner::new;
        } else {
            throw new IllegalArgumentException("Impossible to run simulation of type " + simulationDescriptor.getType());
        }
    }

    protected abstract AbstractRunnerHelper newRunnerHelper();
}
