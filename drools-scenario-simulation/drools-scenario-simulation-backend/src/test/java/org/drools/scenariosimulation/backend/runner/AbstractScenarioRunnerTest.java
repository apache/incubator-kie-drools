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

import java.util.Optional;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class AbstractScenarioRunnerTest {

    private final static int SCENARIO_DATA = 5;
    @Mock
    protected KieContainer kieContainerMock;
    protected AbstractScenarioRunner abstractScenarioRunnerLocal;
    protected Settings settingsLocal;
    private Simulation simulationLocal;

    @Before
    public void setup() {
        settingsLocal = new Settings();
        simulationLocal = getSimulation();
        abstractScenarioRunnerLocal = spy(
                new AbstractScenarioRunner(kieContainerMock,
                                           new Simulation(),
                                           "",
                                           ExpressionEvaluatorFactory.create(
                                                   this.getClass().getClassLoader(),
                                                   ScenarioSimulationModel.Type.RULE),
                                           settingsLocal) {
                    @Override
                    protected AbstractRunnerHelper newRunnerHelper() {
                        return null;
                    }
                });
    }

    @Test
    public void getDescriptionForSimulationByClassNameAndSimulation() {
        Description retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.empty(), simulationLocal);
        commonVerifyDescriptionForSimulation(retrieved, AbstractScenarioRunner.class.getCanonicalName());
        retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.of(String.class.getCanonicalName()), simulationLocal);
        commonVerifyDescriptionForSimulation(retrieved, String.class.getCanonicalName());
    }

    @Test
    public void getDescriptionForSimulationByClassNameAndScenarios() {
        Description retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.empty(), simulationLocal.getScenarioWithIndex());
        commonVerifyDescriptionForSimulation(retrieved, AbstractScenarioRunner.class.getCanonicalName());
        retrieved = AbstractScenarioRunner.getDescriptionForSimulation(Optional.of(String.class.getCanonicalName()), simulationLocal.getScenarioWithIndex());
        commonVerifyDescriptionForSimulation(retrieved, String.class.getCanonicalName());
    }

    @Test
    public void getDescriptionForScenario() {
        final Scenario scenario = simulationLocal.getUnmodifiableData().get(2);
        Description retrieved = AbstractScenarioRunner.getDescriptionForScenario(Optional.empty(), 1, scenario);
        commonVerifyDescriptionForScenario(retrieved, 1, scenario.getDescription(), AbstractScenarioRunner.class.getCanonicalName());
        retrieved = AbstractScenarioRunner.getDescriptionForScenario(Optional.of(String.class.getCanonicalName()), 1, scenario);
        commonVerifyDescriptionForScenario(retrieved, 1, scenario.getDescription(), String.class.getCanonicalName());
    }

    @Test
    public void getSpecificRunnerProvider() {
        // all existing types should have a dedicated runner
        for (ScenarioSimulationModel.Type value : ScenarioSimulationModel.Type.values()) {
            final ScenarioRunnerProvider retrieved = AbstractScenarioRunner.getSpecificRunnerProvider(value);
            assertNotNull(retrieved);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSpecificRunnerProviderNullType() {
        settingsLocal.setType(null);
        AbstractScenarioRunner.getSpecificRunnerProvider(null);
    }

    @Test
    public void testRun() {
        assertNull(abstractScenarioRunnerLocal.simulationRunMetadataBuilder);
        abstractScenarioRunnerLocal.run(new RunNotifier());
        assertNotNull(abstractScenarioRunnerLocal.simulationRunMetadataBuilder);
    }

    private void commonVerifyDescriptionForSimulation(final Description retrieved, final String className) {
        assertNotNull(retrieved);
        assertEquals("Test Scenarios (Preview) tests", retrieved.getDisplayName());
        assertEquals(SCENARIO_DATA, retrieved.getChildren().size());
        assertNull(retrieved.getTestClass());
        assertEquals("Test Scenarios (Preview) tests", retrieved.getClassName());
        IntStream.range(0, SCENARIO_DATA).forEach(index -> {
            final Description description = retrieved.getChildren().get(index);
            commonVerifyDescriptionForScenario(description, index + 1, simulationLocal.getUnmodifiableData().get(index).getDescription(), className);
        });
    }

    private void commonVerifyDescriptionForScenario(final Description description, int index, final String scenarioDescription, final String className) {
        String expected = String.format("#%1$d: %2$s(%3$s)", index, scenarioDescription, className);
        assertEquals(expected, description.getDisplayName());
    }

    private Simulation getSimulation() {
        Simulation toReturn = new Simulation();
        IntStream.range(0, SCENARIO_DATA).forEach(index -> {
            Scenario scenario = toReturn.addData();
            scenario.setDescription("INDEX-" + index);
        });
        return toReturn;
    }
}